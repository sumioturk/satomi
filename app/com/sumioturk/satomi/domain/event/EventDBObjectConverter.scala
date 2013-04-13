package com.sumioturk.satomi.domain.event

import com.sumioturk.satomi.domain.converter.{JsonConversionProtocol, DBObjectConverter}
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.Json._
import com.sumioturk.satomi.domain.converter.exception.DBObjectConversionException
import play.api.libs.json.{Writes, Reads}

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 12:25 PM
 *
 */

class EventDBObjectConverter[T](reads: Reads[T], dbConverter: DBObjectConverter[T]) extends DBObjectConverter[Event[T]] {

  implicit val eventReads = JsonConversionProtocol.getEventReads[T](reads)

  def toDBObject(obj: Event[T]): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "id" -> obj.id
    builder += "createTime" -> obj.createTime
    builder += "broadcastTime" -> obj.broadcastTime
    builder += "invokerId" -> obj.invokerId
    builder += "toChannelId" -> obj.toChannelId
    builder += "position" -> obj.position
    builder += "instruction" -> dbConverter.toDBObject(obj.instruction)
    builder += "message" -> obj.message
    builder.result()
  }

  def fromDBObject(dbObj: DBObject): Event[T] = {
    fromJson[Event[T]](parse(dbObj.toString)).getOrElse(throw new DBObjectConversionException)
  }
}
