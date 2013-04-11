package com.sumioturk.satomi.infrastrucure

import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import com.sumioturk.satomi.domain.converter.DBObjectConverter
import com.sumioturk.satomi.domain.Entity

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:55 AM
 *
 */

class MongoRepository[T <: Entity]
(name: String, converter: DBObjectConverter[T], mongoColl: MongoCollection)
  extends Repository[T] {


  def resolve(id: Int): Option[T] = {
    mongoColl.findOne(MongoDBObject("id" -> id)) match {
      case Some(user) =>
        Some(converter.fromDBObject(user))
      case None =>
        None
    }
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
