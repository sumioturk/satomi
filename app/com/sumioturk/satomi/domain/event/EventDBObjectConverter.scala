package com.sumioturk.satomi.domain.event

import com.sumioturk.satomi.domain.converter.{JsonConversionProtocol, DBObjectConverter}
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.Json._
import com.sumioturk.satomi.domain.converter.exception.DBObjectConversionException
import play.api.libs.json.Reads

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 12:25 PM
 *
 */

class EventDBObjectConverter[T](reads: Reads[T]) extends DBObjectConverter[Event[T]] {

  implicit val eventReads = JsonConversionProtocol.getEventReads[T](reads)

  def toDBObject(obj: Event[T]): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "id" -> obj.id
    builder += "createTime" -> obj.id
    builder += "broadcastTime" -> obj.id
    builder += "invokerId" -> obj.id
    builder += "toChannelId" -> obj.id
    builder += "position" -> obj.id
    builder += "instruction" -> obj.id
    builder += "message" -> obj.id
    builder.result()
  }

  def fromDBObject(dbObj: DBObject): Event[T] = {
    fromJson[Event[T]](parse(dbObj.toString)).getOrElse(throw new DBObjectConversionException)
  }
}
