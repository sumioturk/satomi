package com.sumioturk.satomi.domain.converter

import play.api.libs.json.{JsValue, JsObject}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:24 AM
 *
 */
trait DBObjectConverter[T] {

  def toDBObject(obj: T): DBObject

  def fromDBObject(dbObj: DBObject): T

}
