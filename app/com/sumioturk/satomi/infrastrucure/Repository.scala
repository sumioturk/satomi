package com.sumioturk.satomi.infrastrucure

import com.sumioturk.satomi.domain.Entity

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 12:55 AM
 *
 */

trait Repository[T <: Entity] {

  def resolve(id: Int): Option[T]

  def store(entity: T): Unit

  def remove(entity: T): Unit

  def update(entity: T): Unit

}
