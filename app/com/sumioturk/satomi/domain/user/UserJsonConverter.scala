package com.sumioturk.satomi.domain.user

import com.sumioturk.satomi.domain.converter.{JsonConversionException, JsonConverter}
import play.api.libs.json.{JsValue, Json}

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 10:17 PM
 *
 */

object UserJsonConverter extends JsonConverter[User] {
  implicit def convertToJson(obj: User): JsValue = {
    Json.toJson(Map(
      "id" -> Json.toJson(obj.id),
      "name" -> Json.toJson(obj.name),
      "isGay" -> Json.toJson(obj.isGay)
    ))
  }

  implicit def convertFromJson(json: JsValue): User = {
    User(
      id = (json \ "id").asOpt[String].getOrElse(throw new JsonConversionException),
      name = (json \ "name").asOpt[String].getOrElse(throw new JsonConversionException),
      isGay = (json \ "isGay").asOpt[Boolean].getOrElse(throw new JsonConversionException)
    )
  }
}
