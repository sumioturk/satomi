package com.sumioturk.satomi.domain
import play.api.libs.json.{JsValue, JsObject}
import com.mongodb.casbah.commons.MongoDBObject

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:24 AM
 *
 */
trait MongoDBObjectConverter[T] {

  def toDBObject(obj: T): MongoDBObject

  def fromDBObject(dbObj: MongoDBObject): T


}
