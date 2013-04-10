package com.sumioturk.satomi.domain

import com.sumioturk.satomi.infrastrucure.Entity
import com.mongodb.casbah.commons.MongoDBObject

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:23 AM
 *
 */
case class User (id: Int, name: String) extends Entity
