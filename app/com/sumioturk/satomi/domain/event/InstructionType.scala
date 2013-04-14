package com.sumioturk.satomi.domain.event

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/12/13
 * Time: 11:47 PM
 *
 */

object InstructionType extends Enumeration {

  type InstructionType = Value

  val play = Value("play")
  val pause = Value("pause")
  val seek = Value("seek")
  val stop = Value("stop")
  val jump = Value("jump")
  val message = Value("message")
  val unknown = Value("unknown")

}
