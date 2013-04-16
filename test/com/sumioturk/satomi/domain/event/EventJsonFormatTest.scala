package com.sumioturk.satomi.domain.event

import java.util.UUID
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.event.EventJsonFormat._
import com.sumioturk.satomi.domain.user.User
import com.sumioturk.satomi.domain.user.UserJsonFormat._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 1:48 PM
 *
 */

class EventJsonFormatTest extends org.specs2.mutable.Specification {

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
  val eventBodyType = EventType.play
  val eventMessage = "watzup"

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
        bodyType = EventType.play,
        body = user
      )
      event must_== Json.fromJson[Event[User]](Json.toJson(event)).get
    }
  }
}
