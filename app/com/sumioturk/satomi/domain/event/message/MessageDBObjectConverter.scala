package com.sumioturk.satomi.domain.message

import com.sumioturk.satomi.domain.converter.DBObjectConverter
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.Json
import com.sumioturk.satomi.domain.message.MessageJsonFormat._
import com.sumioturk.satomi.domain.converter.exception.DBObjectConversionException


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 7:19 PM
 *
 */

object MessageDBObjectConverter extends DBObjectConverter[Message] {

  def toDBObject(obj: Message): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "text" -> obj.text
    builder.result
  }

  def fromDBObject(dbObj: DBObject): Message = {
    Json.fromJson(Json.parse(dbObj.toString)).asOpt.getOrElse(throw new DBObjectConversionException)
  }

}
