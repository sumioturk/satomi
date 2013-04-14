package com.sumioturk.satomi.domain.user

import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.sumioturk.satomi.domain.converter.DBObjectConverter
import play.api.libs.json.Json._
import com.sumioturk.satomi.domain.converter.exception.DBObjectConversionException
import com.sumioturk.satomi.domain.user.UserJsonFormat._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:27 AM
 *
 */

object UserDBObjectConverter extends DBObjectConverter[User] {
  def toDBObject(obj: User): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "id" -> obj.id
    builder += "name" -> obj.name
    builder += "isGay" -> obj.isGay
    builder.result()
  }

  def fromDBObject(dbObject: DBObject): User = {
    fromJson(parse(dbObject.toString)).asOpt.getOrElse(throw new DBObjectConversionException)
  }
}
