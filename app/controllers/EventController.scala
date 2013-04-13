package controllers

import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.domain.event.{Event, EventDBObjectConverter}
import com.sumioturk.satomi.domain.user.User
import play.api.mvc._
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.converter.JsonConversionProtocol._


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 12:34 PM
 *
 */

object EventController extends Controller {

  val mongoColl = MongoConnection()("satomi")("Event")

  val converter = new EventDBObjectConverter[User](userRead)

  implicit val eventWrite = getEventWrites[User](userWrite)

  def show = Action {
    val newEvent =
      Event(
        id = "this is id",
        createTime = 12345,
        broadcastTime = 23456,
        invokerId = "i am invoker",
        toChannelId = "this is channel id",
        position = 34567,
        instruction = User("userid", "name", false),
        message = "yello"
      )

    mongoColl += converter.toDBObject(newEvent)
    Ok(Json.toJson(newEvent))
  }


}
