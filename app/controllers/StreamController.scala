package controllers


import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import com.mongodb.casbah.MongoConnection
import play.api.libs.iteratee.Enumerator.TreatCont0
import java.util.concurrent.TimeUnit
import org.bson.types.ObjectId
import com.sumioturk.satomi.domain.event.{Event, EventDBObjectConverter}
import com.sumioturk.satomi.domain.event.EventJsonFormat._
import com.sumioturk.satomi.domain.message.MessageJsonFormat.messageWrite
import scala.Some
import com.sumioturk.satomi.domain.message.{MessageJsonFormat, MessageDBObjectConverter, Message}
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.Json

object StreamController extends Controller {


  val sent = MongoConnection()("satomi")
  val queue = MongoConnection()("satomi")
  val messageEvent = MongoConnection()("satomi")("MessageEvent")
  val mongoColl = MongoConnection()("satomi")("User")

  implicit val eventWrite = getEventWrites[Message]

  def pollMQ(userId: String): Enumerator[String] = {
    generateM {
      Promise.timeout(chunk(userId), 10, TimeUnit.MILLISECONDS)
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

  private def chunk(userId: String): Option[String] = {
    val userLasts = sent("UserLasts_" + userId)
    val userQueue = queue("user_" + userId)
    val lasts = userLasts.find().toList
    lasts isEmpty match {
      case true =>
        userLasts.insert(MongoDBObject("last" -> ObjectId.get()))
        chunk(userId)
      case false =>
        val last = lasts.maxBy(obj => obj.get("id").asInstanceOf[ObjectId])
        val messages = userQueue.find().filter(
          obj => ObjectId.massageToObjectId(obj.get("id").asInstanceOf[String])
            .compareTo(last.get("last").asInstanceOf[ObjectId]) > 0
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
            userLasts.update(last, MongoDBObject("last" -> ObjectId.massageToObjectId(lastMessage.id)))
            Some("\r\n" + Json.toJson[List[Event[Message]]](messages).toString + "\r\n")
        }
    }
  }

  def connect(userId: String) = Action {
    req =>
      req.getQueryString("key") match {
        case None =>
          Forbidden("You are not authorized")
        case Some(string) =>
          string match {
            case "secret" =>
              Ok.stream(pollMQ(userId))
            case _ =>
              Forbidden("You are not authorized")
          }
      }
  }

}
