package com.sumioturk.satomi.domain.user

import com.mongodb.casbah.commons.MongoDBObject
import com.sumioturk.satomi.domain.Entity

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:23 AM
 *
 */
case class User(id: String, name: String, isGay: Boolean) extends Entity
