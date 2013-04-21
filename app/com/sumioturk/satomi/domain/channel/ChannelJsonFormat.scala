package com.sumioturk.satomi.domain.channel

import play.api.libs.json._
import com.sumioturk.satomi.domain.user.User
import com.sumioturk.satomi.domain.user.UserJsonFormat. _
/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/21/13
 * Time: 1:21 PM
 *
 */

object ChannelJsonFormat {

  implicit val channelRead = new Reads[Channel] {
    def reads(json: JsValue): JsResult[Channel] = {
      (json \ "id").validate[String].flatMap(id =>
        (json \ "name").validate[String].flatMap(name =>
          (json \ "users").validate[JsArray].map(users =>
            Channel(
              id = id,
              name = name,
              users = users.value.map(user => Json.fromJson[User](user).get).toList
            )
          )
        )
      )
    }
  }

  implicit val channelWrite = new Writes[Channel] {
    def writes(obj: Channel): JsValue = {
      JsObject(List(
        "id" -> JsString(obj.id),
        "name" -> JsString(obj.name),
        "users" -> JsArray(obj.users.map(user => Json.toJson(user)))
      ))
    }
  }

}
