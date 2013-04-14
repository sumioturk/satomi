package com.sumioturk.satomi.domain.user

import java.util.UUID
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.user.UserJsonFormat._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 5:27 PM
 *
 */

class UserJsonFormatTest extends org.specs2.mutable.Specification {

  val uuid = UUID.randomUUID().toString
  val name = "John Helington"
  val isGay = false
  val user = User(
    id = uuid,
    name = name,
    isGay = isGay
  )

  "User" should {
    "be converted to Json vice versa" in {
      user must_== Json.fromJson(Json.toJson(user)).get
    }
  }

}
