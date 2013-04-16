package controllers


import play.api.mvc._
import com.mongodb.casbah.query.Imports._
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
import com.mongodb.casbah.commons.MongoDBObject

object StreamController extends Controller {


  val sent = MongoConnection()("satomi")("sent")
  val messageEvent = MongoConnection()("satomi")("MessageEvent")
  val mongoColl = MongoConnection()("satomi")("User")

  implicit val eventWrite = getEventWrites[Message]

  /**
   * A String Enumerator producing a formatted Time message every 100 millis.
   * A callback enumerator is pure an can be applied on several Iteratee.
   */

  def pollMQ(channelId: String, userId: String): Enumerator[String] = {
    generateM {
      Promise.timeout(chunk(channelId, userId), 10, TimeUnit.MILLISECONDS)
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

  private def chunk(channelId: String, userId: String): Option[String] = {
    //System.out.println("chunk called")
    if (sent.find().filter(obj => obj.get("id") == userId) isEmpty) {
      val lastId = ObjectId.massageToObjectId(messageEvent.find().filter(
        obj =>
          obj.get("toChannelId") == channelId
      ).maxBy(obj => ObjectId.massageToObjectId(obj.get("id").asInstanceOf[String])).get("id").asInstanceOf[String])

      //System.out.println("empty last: %s".format(lastId.toString))
      sent += MongoDBObject(
        "userId" -> userId,
        "last" -> lastId
      )
      //System.out.println("empty")
    }
    val last = sent.find().filter(obj => obj.get("userId").asInstanceOf[String] == userId).maxBy(obj => obj.get("last").asInstanceOf[ObjectId])
    //System.out.println("last: %s".format(last.get("last").asInstanceOf[ObjectId].toString))
    val messages = messageEvent.find().filter(
      obj => ObjectId.massageToObjectId(obj.get("id").asInstanceOf[String]).compareTo(last.get("last").asInstanceOf[ObjectId]) > 0
    ).filter(
      message => message.get("toChannelId").asInstanceOf[String] == channelId
    ).map {
      message =>
        sent.insert(MongoDBObject("userId" -> userId, "last" -> ObjectId.massageToObjectId(message.get("id").asInstanceOf[String])))
        Json.toJson(
          new EventDBObjectConverter[Message](
            MessageJsonFormat.messageRead,
            MessageDBObjectConverter
          ).fromDBObject(message)).toString
    }

    //System.out.println("before match")
    messages.isEmpty match {
      case true =>
        //System.out.println("None")
        None
      case false =>
        //System.out.println("Some");
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
              Ok.stream(pollMQ(channelId, userId))
            case _ =>
              Forbidden("You are not authorized")
          }
      }
  }

}
