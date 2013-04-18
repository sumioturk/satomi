package controllers


import play.api.mvc._
import com.mongodb.casbah.query.Imports._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import com.mongodb.casbah.MongoConnection
import play.api.libs.iteratee.Enumerator.TreatCont0
import java.util.concurrent.TimeUnit
import org.bson.types.ObjectId
import com.sumioturk.satomi.domain.event.EventDBObjectConverter
import com.sumioturk.satomi.domain.event.EventJsonFormat._
import com.sumioturk.satomi.domain.message.MessageJsonFormat.messageWrite
import scala.Some
import com.sumioturk.satomi.domain.message.{MessageJsonFormat, MessageDBObjectConverter, Message}
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.Json

object StreamController extends Controller {


  val sent = MongoConnection()("satomi")("sent")
  val messageEvent = MongoConnection()("satomi")("MessageEvent")
  val mongoColl = MongoConnection()("satomi")("User")

  implicit val eventWrite = getEventWrites[Message]

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
    sent.findOne("userId" $in List(userId)) match {
      case None =>
        sent.insert(MongoDBObject("userId" -> userId, "last" -> ObjectId.get()))
        chunk(channelId, userId)
      case Some(entry) =>
        val last = entry
        val messages = messageEvent.find().filter(
          obj => ObjectId.massageToObjectId(obj.get("id").asInstanceOf[String]).compareTo(last.get("last").asInstanceOf[ObjectId]) > 0
        ).filter(
          message => message.get("toChannelId").asInstanceOf[String] == channelId
        ).map {
          message =>
            new EventDBObjectConverter[Message](
              MessageJsonFormat.messageRead,
              MessageDBObjectConverter
            ).fromDBObject(message)
        }.toList

        messages.isEmpty match {
          case true =>
            None
          case false =>
            val lastMessage = messages.maxBy(message => ObjectId.massageToObjectId(message.id))
            val userSent = sent.findOne(MongoDBObject("userId" -> userId)).get
            sent.update(userSent, MongoDBObject("userId" -> userId, "last" -> ObjectId.massageToObjectId(lastMessage.id)))
            val messageStrings = messages.map(m => Json.toJson(m).toString())
            Some(messageStrings.foldLeft("\r\n")((s: String, t: String) => s + "\r\n" + t))
        }
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
