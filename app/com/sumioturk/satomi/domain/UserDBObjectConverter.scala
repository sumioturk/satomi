package com.sumioturk.satomi.domain

import play.api.libs.json.{JsValue, Json, JsObject}
import scala.Int
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.ValidBSONType.BasicDBObject

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:27 AM
 *
 */
object UserDBObjectConverter extends MongoDBObjectConverter[User] {
  def toDBObject(obj: User): MongoDBObject = {
    MongoDBObject("id" -> obj.id, "name" -> obj.name)
  }

  def fromDBObject(dbObject: MongoDBObject): User = {
    val json = Json.parse(dbObject.toString)
    User(
      ((json \ "id").asOpt[Int]).getOrElse(throw new DBObjectConversionException),
      ((json \ "name").asOpt[String]).getOrElse(throw new DBObjectConversionException)
    )
  }
}
