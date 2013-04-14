package controllers


import play.api.mvc._

import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.domain.user.{User, UserDBObjectConverter}
import play.api.libs.json.Json
import play.api.libs.iteratee.Enumerator.TreatCont0
import java.util.concurrent.TimeUnit
import org.bson.types.ObjectId
import com.sumioturk.satomi.domain.event.EventDBObjectConverter
import com.sumioturk.satomi.domain.event.EventJsonFormat._
import com.sumioturk.satomi.domain.message.MessageJsonFormat.messageWrite
import scala.Some
import com.sumioturk.satomi.domain.message.{MessageJsonFormat, MessageDBObjectConverter, Message}

object StreamController extends Controller {


  val sent = MongoConnection()("satomi")("sent")
  val messageEvent = MongoConnection()("satomi")("MessageEvent")
  val mongoColl = MongoConnection()("satomi")("User")

  implicit val eventWrite = getEventWrites[Message]

  /**
   * A String Enumerator producing a formatted Time message every 100 millis.
   * A callback enumerator is pure an can be applied on several Iteratee.
   */

  def pollMQ(channelId: String): Enumerator[String] = {
    generateM {
      Promise.timeout(chunk(channelId), 10, TimeUnit.MILLISECONDS)
    }
  }

  def index = Action {
    Ok
  }

  private def generateM[E](e: => Future[Option[E]]): Enumerator[E] = checkContinue0(new TreatCont0[E] {
    def apply[A](loop: Iteratee[E, A] => Future[Iteratee[E, A]], k: Input[E] => Iteratee[E, A]) = e.flatMap {
      case Some(e) => loop(k(Input.El(e)))
      case None => loop(k(Input.Empty))
    }
  })

  private def checkContinue0[E](inner: TreatCont0[E]) = new Enumerator[E] {
    def apply[A](it: Iteratee[E, A]): Future[Iteratee[E, A]] = {
      def step(it: Iteratee[E, A]): Future[Iteratee[E, A]] = it.fold {
        case Step.Done(a, e) => Future.successful(Done(a, e))
        case Step.Cont(k) => inner[A](step, k)
        case Step.Error(msg, e) => Future.successful(Error(msg, e))
      }
      step(it)
    }
  }

  private def chunk(channelId: String): Option[String] = {
    if (sent isEmpty) {
      sent += UserDBObjectConverter.toDBObject(User("", "", false))
    }
    val last = sent.last.get("_id").asInstanceOf[ObjectId]

    val messages = messageEvent.find.filter(
      obj => obj.get("_id").asInstanceOf[ObjectId].compareTo(last) > 0
    ).filter(
      message => message.get("toChannelId").toString == channelId
    ).map {
      message =>
        sent += message
        Json.toJson(
          new EventDBObjectConverter[Message](
            MessageJsonFormat.messageRead,
            MessageDBObjectConverter
          ).fromDBObject(message))
    }

    messages.isEmpty match {
      case true =>
        None
      case false =>
        System.out.println("there was a chunk");
        Some(messages.foldLeft("")(_ + "\r\n" + _) + "\r\n")
    }
  }

  def connect(userId: String, channelId: String) = Action {
    req =>
      sent.drop()
      req.getQueryString("key") match {
        case None =>
          Forbidden("You are not authorized")
        case Some(string) =>
          string match {
            case "secret" =>
              Ok.stream(pollMQ(channelId))
            case _ =>
              Forbidden("You are not authorized")
          }
      }
  }

}
