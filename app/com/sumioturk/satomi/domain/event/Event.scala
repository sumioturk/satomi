package com.sumioturk.satomi.domain.event

import com.sumioturk.satomi.domain.Entity
import com.sumioturk.satomi.domain.event.InstructionType.InstructionType

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/12/13
 * Time: 9:28 PM
 *
 */

case class
Event
(
  id: String,
  createTime: Long,
  broadcastTime: Long,
  invokerId: String,
  toChannelId: String,
  position: Long,
  instruction: InstructionType,
  message: String
) extends Entity





