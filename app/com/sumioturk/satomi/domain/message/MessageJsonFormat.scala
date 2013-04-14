package com.sumioturk.satomi.domain.message

import play.api.libs.json._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 7:12 PM
 *
 */

object MessageJsonFormat {

  implicit val messageRead = new Reads[Message] {
    def reads(json: JsValue): JsResult[Message] = {
      (json \ "text").validate[String].map(text =>
        Message(text)
      )
    }
  }

  implicit val messageWrite = new Writes[Message] {
    def writes(obj: Message): JsValue = {
      JsObject(List(
        "text" -> JsString(obj.text)
      ))
    }
  }

}
