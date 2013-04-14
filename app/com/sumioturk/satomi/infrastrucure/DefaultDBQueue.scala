package com.sumioturk.satomi.infrastrucure

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 2:22 PM
 *
 */

class DefaultDBQueue(val id: String) extends DBQueue {

  val db = MongoConnection()("satomi")(id)

  def resolve(id: String): Option[DBObject] = {
    db.findOne(MongoDBObject("id" -> id))
  }

  def latest(): Option[DBObject] = {
    Some(db.last)
  }

  def oldest(): Option[DBObject] = {
    Some(db.head)
  }

  def range(from: String, to: String): List[DBObject] = {
    db.find.filter(
      obj =>
        (obj.get("id").asInstanceOf[ObjectId]
          compareTo ObjectId.massageToObjectId(from)) > 0
          &&
          (obj.get("id").asInstanceOf[ObjectId]
            compareTo ObjectId.massageToObjectId(to)) < 0
    ).toList
  }

  def from(from: String): List[DBObject] = {
    db.find.filter(
      obj =>
        (obj.get("id").asInstanceOf[ObjectId]
          compareTo ObjectId.massageToObjectId(from)) > 0
    ).toList
  }
}
