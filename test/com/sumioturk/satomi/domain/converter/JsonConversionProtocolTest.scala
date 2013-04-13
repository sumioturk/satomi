package com.sumioturk.satomi.domain.converter

import com.sumioturk.satomi.domain.user.User
import java.util.UUID
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.converter.JsonConversionProtocol._
import com.sumioturk.satomi.domain.event.Event


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 5:27 PM
 *
 */
class JsonConversionProtocolTest extends org.specs2.mutable.Specification {

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
  val eventInstruction = UUID.randomUUID().toString
  val eventMessage = "watzup"


  "User" should {
    "be converted to Json vice versa" in {
      user must_== Json.fromJson(Json.toJson(user)).get
    }
  }

  "Event[User]" should {
    "be converted to Json vice versa" in {
      implicit val eventReads = getEventReads[User]
      implicit val eventWrites = getEventWrites[User]
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
      event must_== Json.fromJson[Event[User]](Json.toJson(event)).get
    }
  }


}
