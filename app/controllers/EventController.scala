package controllers

import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.domain.event.{Event, EventDBObjectConverter}
import com.sumioturk.satomi.domain.user.{UserDBObjectConverter, User}
import play.api.mvc._
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.converter.JsonConversionProtocol._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.UUID


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 12:34 PM
 *
 */

object EventController extends Controller {

  val mongoColl = MongoConnection()("satomi")("Event")

  val converter = new EventDBObjectConverter[User](userRead, UserDBObjectConverter)

  implicit val eventWrite = getEventWrites[User](userWrite)

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
    val eventPosition = 34567L
    val eventMessage = "watzup"

    val event = Event[User](
      id = eventId,
      createTime = eventCreateTime,
      broadcastTime = eventBroadcastTime,
      invokerId = eventInvokerId,
      toChannelId = eventToChannelId,
      position = eventPosition,
      instruction = user,
      message = eventMessage
    )
    mongoColl += converter.toDBObject(event)
    Ok(Json.toJson[Event[User]](converter.fromDBObject(mongoColl.findOne(MongoDBObject("id" -> eventId)).get)))


  }


}
