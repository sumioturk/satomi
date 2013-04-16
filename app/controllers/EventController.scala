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
  val messageEvents = MongoConnection()("satomi")("MessageEvent")

  val converter = new EventDBObjectConverter[User](UserJsonFormat.userRead, UserDBObjectConverter)

  implicit val eventWrite = getEventWrites[User](UserJsonFormat.userWrite)

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

    userEvents += converter.toDBObject(event)
    Ok(Json.toJson[Event[User]](converter.fromDBObject(userEvents.findOne(MongoDBObject("id" -> eventId)).get)))

  }


  def message(channelId: String, text: String) = Action {
    val userConverter = new EventDBObjectConverter[Message](MessageJsonFormat.messageRead, MessageDBObjectConverter)
    val event = Event[Message](
      id = ObjectId.get.toString,
      invokerId = "1",
      createTime = System.currentTimeMillis(),
      broadcastTime = System.currentTimeMillis(),
      toChannelId = channelId,
      bodyType = EventType.message,
      body = Message(text)
    )
    messageEvents += userConverter.toDBObject(event)
    Ok
  }


}
