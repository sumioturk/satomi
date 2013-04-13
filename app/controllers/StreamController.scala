package controllers


import play.api.mvc._

import play.api.libs.iteratee._
import play.api.libs.concurrent.Promise
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.domain.user.UserDBObjectConverter
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.converter.JsonConversionProtocol.userWrite


object StreamController extends Controller {

  val mongoColl = MongoConnection()("satomi")("User")

  /**
   * A String Enumerator producing a formatted Time message every 100 millis.
   * A callback enumerator is pure an can be applied on several Iteratee.
   */
  lazy val clock: Enumerator[String] = {
    Enumerator.generateM {
      Promise.timeout(chunk, 2000)
    }
  }

  def index = Action {
    Ok
  }

  private def chunk: Option[String] = {
    System.out.println("chunk")
    val users = mongoColl.find.map {
      user =>
        Json.toJson(UserDBObjectConverter.fromDBObject(user)).toString()
    }

    users.isEmpty match {
      case true =>
        None
      case false =>
        Some(users.foldLeft("")(_ + "\r\n" +  _) + "\r\n")
    }

  }

  def connect(userId: String, channelId: String) = Action {
    Ok.stream(clock)
  }

}
