package com.sumioturk.satomi.infrastrucure

import com.sumioturk.satomi.domain.{MongoDBObjectConverter, UserDBObjectConverter, User}
import com.mongodb.casbah.{MongoConnection, MongoCollection, MongoClient}
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.{JsValue, Json}
import com.mongodb.DBObject

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:55 AM
 *
 */
class MongoRepository[T <: Entity](implicit converter: MongoDBObjectConverter[T]) extends Repository[T] {


  val mongoColl = MongoConnection()("satomi")("users")

  def resolve(id: Int): Option[T] = {
    Some(converter.fromDBObject(mongoColl.findOne(MongoDBObject("id" -> id)).get))
  }


  def store(entity: T) {
    mongoColl += converter.toDBObject(entity)
  }


  def remove(entity: T) {
    mongoColl -= converter.toDBObject(entity)
  }

  def update(entity: T) {
    mongoColl.update(MongoDBObject("id" -> entity.id), converter.toDBObject(entity))
  }

}
