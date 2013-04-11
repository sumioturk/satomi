package com.sumioturk.satomi.domain.user

import play.api.libs.json.Json
import scala.Int
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.sumioturk.satomi.domain.converter.{DBObjectConversionException, DBObjectConverter}

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
    val json = Json.parse(dbObject.toString)
    User(
      ((json \ "id").asOpt[String]).getOrElse(throw new DBObjectConversionException),
      ((json \ "name").asOpt[String]).getOrElse(throw new DBObjectConversionException),
      ((json \ "isGay").asOpt[Boolean].getOrElse(throw new DBObjectConversionException))
    )
  }
}
