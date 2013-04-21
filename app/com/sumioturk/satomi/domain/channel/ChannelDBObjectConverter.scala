package com.sumioturk.satomi.domain.channel

import com.sumioturk.satomi.domain.converter.DBObjectConverter
import com.mongodb.DBObject
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.sumioturk.satomi.domain.user.UserDBObjectConverter
import com.sumioturk.satomi.domain.converter.exception.DBObjectConversionException
import play.api.libs.json.Json._
import com.sumioturk.satomi.domain.channel.ChannelJsonFormat._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/21/13
 * Time: 1:54 PM
 *
 */

object ChannelDBObjectConverter extends DBObjectConverter[Channel] {
  implicit def toDBObject(obj: Channel): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "id" -> obj.id
    builder += "name" -> obj.name
    builder += "users" -> obj.users.map(user => UserDBObjectConverter.toDBObject(user))
    builder.result()
  }

  implicit def fromDBObject(dbObj: DBObject): Channel = {
    fromJson(parse(dbObj.toString)).asOpt.getOrElse(throw new DBObjectConversionException)
  }
}
