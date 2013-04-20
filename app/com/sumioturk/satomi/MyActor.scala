package com.sumioturk.satomi

import akka.actor.Actor
import akka.event.Logging

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/20/13
 * Time: 12:45 AM
 *
 */
class MyActor extends Actor {

  val logger = Logging(context.system, this)

  def receive = {
    case "test" =>
      logger.info("received test message")
    case _ =>
      logger.info("unknown message")
  }
}
