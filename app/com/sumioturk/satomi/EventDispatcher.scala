package com.sumioturk.satomi

import akka.actor._
import akka.event.Logging
import akka.routing.RoundRobinRouter
import com.mongodb.casbah.MongoConnection
import com.mongodb.DBObject
import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/20/13
 * Time: 12:35 AM
 *
 */


object EventDispatcher extends App {

  sealed trait Message

  case object Terminate extends Message

  object StartDist extends Message

  case class DistWork(obj: List[DBObject], from: Int, to: Int, connectionId: String) extends Message

  case class DistSuccess(obj: DBObject) extends Message

  case class DistError(obj: DBObject) extends Message


  /** this supposed to be a connection pool */
  val connectionPool = Map(
    "master" -> MongoConnection(),
    "0" -> MongoConnection(),
    "1" -> MongoConnection(),
    "2" -> MongoConnection(),
    "3" -> MongoConnection(),
    "4" -> MongoConnection(),
    "5" -> MongoConnection(),
    "6" -> MongoConnection(),
    "7" -> MongoConnection(),
    "8" -> MongoConnection(),
    "9" -> MongoConnection(),
    "10" -> MongoConnection(),
    "11" -> MongoConnection(),
    "12" -> MongoConnection(),
    "13" -> MongoConnection(),
    "14" -> MongoConnection(),
    "15" -> MongoConnection(),
    "16" -> MongoConnection(),
    "17" -> MongoConnection(),
    "18" -> MongoConnection(),
    "19" -> MongoConnection(),
    "20" -> MongoConnection(),
    "21" -> MongoConnection(),
    "22" -> MongoConnection(),
    "23" -> MongoConnection(),
    "24" -> MongoConnection(),
    "25" -> MongoConnection(),
    "26" -> MongoConnection(),
    "27" -> MongoConnection(),
    "28" -> MongoConnection(),
    "29" -> MongoConnection(),
    "30" -> MongoConnection(),
    "31" -> MongoConnection(),
    "32" -> MongoConnection(),
    "33" -> MongoConnection()
  )


  /**
   * This Worker tries to store dbobject to db.
   * Send DistSuccess message to a master if write is successful, otherwise send DistError message to the master
   */
  class Worker extends Actor {

    val logger = Logging(context.system, this)

    def receive = {
      case DistWork(obj, from, to, connectionId) =>
        obj.map {
          a =>
            val channelId = a.get("toChannelId").asInstanceOf[String]
            val coll = connectionPool("master")("satomi")("channel_" + channelId)
            val result = coll += a
            // is this really working?
            result.getCachedLastError == null match {
              case true =>
                sender ! DistSuccess(a)
              case _ =>
                sender ! DistError(a)
            }
        }
    }
  }

  /**
   * The master chief.
   * @param numOfWorkers
   * @param listener
   */
  class Master(numOfWorkers: Int, listener: ActorRef) extends Actor {

    val logger = Logging(context.system, this)

    val start = System.currentTimeMillis()

    val chunkSize = 10

    var sent = 0

    var size = 0

    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(numOfWorkers)), name = "workerRouter")

    override def preStart() = {

      logger.info("schedule start")

      master ! StartDist

    }

    def receive = {
      case StartDist =>
        val messages = connectionPool("master")("satomi")("MessageEvent").find().slice(0, 100).toList
        size = messages.length
        loop(messages, 0, messages.length)
        if (size == sent) {
          context.system.scheduler.scheduleOnce(Duration.Zero, self, StartDist)
        }
      case DistSuccess(obj) =>
        sent = sent + 1
        val coll = connectionPool("master")("satomi")("MessageEvent")
        coll.remove(obj)
        sent == size match {
          case true =>
            sent = 0
            size = 0
            context.system.scheduler.scheduleOnce(Duration.Zero, self, StartDist)
          case false =>
            Unit
        }
      case DistError(obj) =>
    }


    @tailrec
    final def loop(messages: List[DBObject], from: Int, length: Int): Unit = {
      from > length match {
        case true =>
          Unit
        case false =>
          val to = from + chunkSize
          workerRouter ! DistWork(messages.slice(from, to), from, to, Math.ceil((Math.random() * 10000) % 2).toInt.toString)
          loop(messages, to, length)
      }
    }
  }

  class Listener extends Actor {

    val logger = Logging(context.system, this)

    def receive = {
      case Terminate =>
        logger.info("Terminating...")
    }
  }

  val mySystem = ActorSystem("EventDispatcher")

  val listener = mySystem.actorOf(Props[Listener], name = "listener")

  val master = mySystem.actorOf(Props(new Master(100, listener)), name = "master")

}
