package com.sumioturk.satomi.domain.channel

import java.util.UUID
import com.sumioturk.satomi.domain.user.User
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.channel.ChannelJsonFormat._


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/21/13
 * Time: 1:33 PM
 *
 */

class ChannelJsonFormatTest extends org.specs2.mutable.Specification {

  val uuid = UUID.randomUUID().toString
  val name = "John Helington"
  val isGay = false
  val user = User(
    id = uuid,
    name = name,
    isGay = isGay
  )

  val uuid2 = UUID.randomUUID().toString
  val name2 = "John Mayer"
  val isGay2 = true
  val user2 = User(
    id = uuid2,
    name = name2,
    isGay = isGay2
  )

  val uuid3 = UUID.randomUUID().toString
  val name3 = "John Carlson"
  val isGay3 = false
  val user3 = User(
    id = uuid3,
    name = name3,
    isGay = isGay3
  )

  val users = List(user, user2, user3)

  val channelId = "12345"
  val channelName = "The Channel"

  val channel = Channel(
    id = channelId,
    name = channelName,
    users = users
  )

  "Channel" should {
    "be converted to Json vice versa" in {
      channel must_== Json.fromJson[Channel](Json.toJson[Channel](channel)).get
    }
  }


}
