package experiment

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/17/13
 * Time: 12:05 AM
 *
 */


object CasbahEsperiment {

  val collection = MongoConnection()("satomi")("experiment")

  val john = MongoDBObject(
    "id" -> ObjectId.get(),
    "name" -> "john",
    "message" -> "Helloo, World"
  )

  val josh = MongoDBObject(
    "id" -> ObjectId.get(),
    "name" -> "john",
    "message" -> "Helloo, World"
  )


  val supposedlyJohn = collection.find("id" $in List("john"))



}
