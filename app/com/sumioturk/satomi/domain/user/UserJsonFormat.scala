package com.sumioturk.satomi.domain.user

import play.api.libs.json._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 12:57 PM
 *
 */

object UserJsonFormat {

  implicit val userRead = new Reads[User] {
    def reads(json: JsValue): JsResult[User] = {
      (json \ "id").validate[String].flatMap(id =>
        (json \ "name").validate[String].flatMap(name =>
          (json \ "isGay").validate[Boolean].map(isGay =>
            User(id, name, isGay)
          )
        )
      )
    }
  }

  implicit val userWrite = new Writes[User] {
    def writes(user: User): JsValue = {
      JsObject(List(
        "id" -> JsString(user.id),
        "name" -> JsString(user.name),
        "isGay" -> JsBoolean(user.isGay)
      ))
    }
  }

}
