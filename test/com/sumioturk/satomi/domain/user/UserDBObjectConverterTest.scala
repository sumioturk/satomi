package com.sumioturk.satomi.domain.user

import java.util.UUID
import com.sumioturk.satomi.domain.user.UserDBObjectConverter._
import com.sumioturk.satomi.domain.event.EventDBObjectConverter
import com.sumioturk.satomi.domain.converter.JsonConversionProtocol._
import com.sumioturk.satomi.domain.event.Event

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 6:06 PM
 *
 */

class UserDBObjectConverterTest extends org.specs2.mutable.Specification {

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


  "User" should {
    "be converted to DBObject vice versa" in {
      user must_== fromDBObject(toDBObject(user))
    }
  }

  "Event[User]" should {
    "be converted to DBObject vice versa" in {

      implicit val converter = new EventDBObjectConverter[User](userRead, UserDBObjectConverter)

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
      event must_== converter.fromDBObject(converter.toDBObject(event))
    }
  }

}
