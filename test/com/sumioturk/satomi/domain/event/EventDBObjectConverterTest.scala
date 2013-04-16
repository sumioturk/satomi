package com.sumioturk.satomi.domain.event

import java.util.UUID
import com.sumioturk.satomi.domain.user.{UserDBObjectConverter, UserJsonFormat, User}

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 1:53 PM
 *
 */

class EventDBObjectConverterTest extends org.specs2.mutable.Specification {

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

  "Event[User]" should {
    "be converted to DBObject vice versa" in {

      implicit val converter = new EventDBObjectConverter[User](UserJsonFormat.userRead, UserDBObjectConverter)

      val event = Event[User](
        id = eventId,
        createTime = eventCreateTime,
        broadcastTime = eventBroadcastTime,
        invokerId = eventInvokerId,
        toChannelId = eventToChannelId,
        bodyType = EventType.play,
        body = user
      )
      event must_== converter.fromDBObject(converter.toDBObject(event))
    }
  }


}
