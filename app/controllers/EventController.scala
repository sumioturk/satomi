package controllers

import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.domain.event.{EventType, EventJsonFormat, Event, EventDBObjectConverter}
import com.sumioturk.satomi.domain.user.{UserJsonFormat, UserDBObjectConverter, User}
import play.api.mvc._
import play.api.libs.json.Json
import EventJsonFormat._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.UUID
import com.sumioturk.satomi.domain.message.{MessageDBObjectConverter, MessageJsonFormat, Message}
import org.bson.types.ObjectId
import com.sumioturk.satomi.infrastrucure.MongoRepository


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 12:34 PM
 *
 */

object EventController extends Controller {

  val userEvents = MongoConnection()("satomi")("UserEvent")
  val playEvent = MongoConnection()("satomi")("PlayEvent")
  val messageEvents = MongoConnection()

  val mongoColl = MongoConnection()("satomi")("User")

  val userRepo = new MongoRepository[User](
    UserDBObjectConverter,
    mongoColl)

  val userConverter = new EventDBObjectConverter[User](UserJsonFormat.userRead, UserDBObjectConverter)
  val messageConverter = new EventDBObjectConverter[Message](MessageJsonFormat.messageRead, MessageDBObjectConverter)

  implicit val eventWrite = getEventWrites[User](UserJsonFormat.userWrite)
  implicit val messageWrite = getEventWrites[Message](MessageJsonFormat.messageWrite)

  def show = Action {
    val uuid = UUID.randomUUID().toString
    val name = "John Helington"
    val isGay = false
    val user = User(
      id = uuid,
      name = name,
      isGay = isGay
    )

    val eventId = UUID.randomUUID().toString
    val eventCreateTime = 1234L
    val eventBroadcastTime = 2345L
    val eventInvokerId = UUID.randomUUID().toString
    val eventToChannelId = UUID.randomUUID().toString

    val event = Event[User](
      id = eventId,
      createTime = eventCreateTime,
      broadcastTime = eventBroadcastTime,
      invokerId = eventInvokerId,
      toChannelId = eventToChannelId,
      bodyType = EventType.play,
      body = user
    )

    userEvents += userConverter.toDBObject(event)
    Ok(Json.toJson[Event[User]](userConverter.fromDBObject(userEvents.findOne(MongoDBObject("id" -> eventId)).get)))

  }


  def message(channelId: String, userId: String, text: String) = Action {
    //play.Logger.info("channelId: %s text: %s".format(channelId, text))
    userRepo.resolve(userId) match {
      case Some(user) =>
        val event = Event[Message](
          id = ObjectId.get.toString,
          invokerId = userId,
          createTime = System.currentTimeMillis(),
          broadcastTime = System.currentTimeMillis(),
          toChannelId = channelId,
          bodyType = EventType.message,
          body = Message(text)
        )
        messageEvents("satomi")("MessageEvent") += messageConverter.toDBObject(event)
        Ok
      case None =>
        Forbidden("User not found");
    }
  }

  def events(id: String) = Action {
    Ok(Json.toJson[List[Event[Message]]](messageEvents("satomi")("user_" + id).find().map(obj => messageConverter.fromDBObject(obj)).toList))
  }


}
