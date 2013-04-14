package com.sumioturk.satomi.infrastrucure

import com.mongodb.DBObject

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/14/13
 * Time: 2:10 PM
 *
 */

trait DBQueue {

  val id: String

  def resolve(id: String): Option[DBObject]

  def latest(): Option[DBObject]

  def oldest(): Option[DBObject]

  def range(from: String,  to: String): List[DBObject]

  def from(from: String): List[DBObject]

}
