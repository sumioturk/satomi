package com.sumioturk.satomi.domain.event

import play.api.libs.json._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/12/13
 * Time: 11:07 PM
 *
 */

object EventJsonFormat {

  def getEventReads[T](implicit read: Reads[T]): Reads[Event[T]] = {
    new Reads[Event[T]] {
      def reads(json: JsValue): JsResult[Event[T]] = {
        (json \ "id").validate[String].flatMap(id =>
          (json \ "createTime").validate[Long].flatMap(createTime =>
            (json \ "broadcastTime").validate[Long].flatMap(broadcastTime =>
              (json \ "invokerId").validate[String].flatMap(invokerId =>
                (json \ "toChannelId").validate[String].flatMap(toChannelId =>
                  (json \ "bodyType").validate[String].flatMap(bodyType =>
                    (json \ "body").validate[JsObject].map(body =>
                      Event[T](
                        id = id,
                        createTime = createTime,
                        broadcastTime = broadcastTime,
                        invokerId = invokerId,
                        toChannelId = toChannelId,
                        bodyType = EventType.withName(bodyType),
                        body = Json.fromJson[T](body).get
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
  }

  def getEventWrites[T](implicit write: Writes[T]): Writes[Event[T]] = {
    new Writes[Event[T]] {
      def writes(event: Event[T]): JsValue = {
        JsObject(List(
          "id" -> JsString(event.id),
          "createTime" -> JsNumber(event.createTime),
          "broadcastTime" -> JsNumber(event.broadcastTime),
          "invokerId" -> JsString(event.invokerId),
          "toChannelId" -> JsString(event.toChannelId),
          "bodyType" -> JsString(event.bodyType.toString),
          "body" -> Json.toJson[T](event.body)
        ))
      }
    }
  }

}

