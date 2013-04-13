package com.sumioturk.satomi.domain.converter

import com.sumioturk.satomi.domain.event.InstructionType
import play.api.libs.json._
import com.sumioturk.satomi.domain.event.Event
import com.sumioturk.satomi.domain.user.User

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/12/13
 * Time: 11:07 PM
 *
 */

object JsonConversionProtocol {

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

  implicit val eventRead = new Reads[Event] {
    def reads(json: JsValue): JsResult[Event] = {
      (json \ "id").validate[String].flatMap(id =>
        (json \ "createTime").validate[Long].flatMap(createTime =>
          (json \ "broadcastTime").validate[Long].flatMap(broadcastTime =>
            (json \ "invokerId").validate[String].flatMap(invokerId =>
              (json \ "toChannelId").validate[String].flatMap(toChannelId =>
                (json \ "position").validate[Long].flatMap(position =>
                  (json \ "instruction").validate[String].flatMap(instruction =>
                    (json \ "message").validate[String].map(message =>
                      Event(
                        id = id,
                        broadcastTime = broadcastTime,
                        createTime = createTime,
                        invokerId = invokerId,
                        toChannelId = toChannelId,
                        position = position,
                        instruction = InstructionType.withName(instruction),
                        message = message
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    }
  }

  implicit val eventWrite = new Writes[Event] {
    def writes(event: Event): JsValue = {
      JsObject(List(
        "id" -> JsString(event.id),
        "createTime" -> JsNumber(event.createTime),
        "broadcastTime" -> JsNumber(event.createTime),
        "invokerId" -> JsString(event.invokerId),
        "toChannelId" -> JsString(event.toChannelId),
        "position" -> JsNumber(event.position),
        "instruction" -> JsString(event.instruction.toString),
        "message" -> JsString(event.message)
      ))
    }
  }

}
