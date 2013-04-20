package com.sumioturk.satomi

import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import akka.event.Logging
import akka.routing.RoundRobinRouter
import com.mongodb.casbah.MongoConnection
import com.mongodb.DBObject
import scala.annotation.tailrec
import scala.concurrent.duration._

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

  object StartDispatch extends Message

  case class Work(obj: List[DBObject], from: Int, to: Int, connectionId: String) extends Message

  case class Dispatched(from: Int, to: Int) extends Message


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


  class Worker extends Actor {

    val logger = Logging(context.system, this)

    def receive = {
      case Work(obj, from, to, connectionId) =>
        //logger.info("connectionId: %s".format(connectionId))
        obj.map {
          a =>
            val channelId = a.get("toChannelId").asInstanceOf[String]
            val coll = connectionPool("master")("satomi")("channel_" + channelId)
            coll += a
            sender ! Dispatched(from, to)
        }
    }
  }

  class Master(numOfWorkers: Int, listener: ActorRef) extends Actor {

    val logger = Logging(context.system, this)

    val start = System.currentTimeMillis();

    var sent = 0

    val chunkSize = 1

    var dispatched = 0

    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(numOfWorkers)), name = "workerRouter")


    override def preStart() = {

      logger.info("schedule start")

      system.scheduler.schedule(0 milliseconds, 5000 milliseconds, self, StartDispatch)

    }

    def receive = {
      case StartDispatch =>
        val messages = connectionPool("master")("satomi")("MessageEvent").find().slice(0, 10000).toList
        dispatched = Math.ceil(messages.length / chunkSize).toInt
        loop(messages, 0, messages.length)
      case Dispatched(from, to) =>
        //logger.info("dispatched! %d -- %d".format(from, to))
        sent = sent + 1
        dispatched == sent match {
          case true =>
            logger.error("duration : %d".format(System.currentTimeMillis() - start))
            listener ! Terminate
          case false =>
            Unit
        }
    }


    @tailrec
    final def loop(messages: List[DBObject], from: Int, length: Int): Unit = {
      from > length match {
        case true =>
        //logger.info("from > length")
        case false =>
          //logger.info("from <= length")
          val to = from + chunkSize
          //logger.info("%d --- %d".format(from, to))
          workerRouter ! Work(messages.slice(from, to), from, to, Math.ceil((Math.random() * 10000) % 2).toInt.toString)
          loop(messages, to, length)
      }
    }
  }

  class Listener extends Actor {

    val logger = Logging(context.system, this)

    def receive = {
      case Terminate =>
        logger.info("Terminating...")
        context.system.shutdown()
    }
  }


  val system = ActorSystem("EventODispatcher")

  val listener = system.actorOf(Props[Listener], name = "listener")

  val master = system.actorOf(Props(new Master(100, listener)), name = "master")

  //system.scheduler.scheduleOnce(Duration.Zero) {
  //  master ! StartDispatch
  //}


}
