package controllers

import akka.actor._
import akka.event.Logging
import akka.routing.RoundRobinRouter
import com.mongodb.casbah.{MongoCollection, MongoConnection}
import com.mongodb.DBObject
import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import com.sumioturk.satomi.infrastrucure.MongoRepository
import com.sumioturk.satomi.domain.channel.{ChannelDBObjectConverter, Channel}

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/20/13
 * Time: 12:35 AM
 *
 */


object EventDispatcher {

  sealed trait Message

  case object Terminate extends Message

  object StartDist extends Message

  case class DistWork(obj: List[DBObject], from: Int, to: Int, connectionId: String) extends Message

  case class DistSuccess(obj: DBObject) extends Message

  case class DistError(obj: DBObject) extends Message

  val conn = MongoConnection()


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
            val channelColl = conn("satomi")("Channel")
            val channelRepo = new MongoRepository[Channel](
              ChannelDBObjectConverter,
              channelColl
            )
            channelRepo.resolve(channelId) match {
              case None =>
                Unit
              case Some(channel) =>
                channel.users map {
                  user =>
                    logger.info("Distributing message to %s in %s".format(user.id, channelId))
                    val coll = conn("satomi")("user_" + user.id)
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

    }

    def receive = {
      case StartDist =>
        sent = 0
        size = 0
        val messages = conn("satomi")("MessageEvent").find().slice(0, 100).toList
        size = messages.length
        if (size == 0) {
          context.system.scheduler.scheduleOnce(Duration.Zero, self, StartDist)
        }
        loop(messages, 0, messages.length)
      case DistSuccess(obj) =>
        sent = sent + 1
        val coll = conn("satomi")("MessageEvent")
        coll.remove(obj)
        sent == size match {
          case true =>
            context.system.scheduler.scheduleOnce(Duration.Zero, self, StartDist)
          case false =>
            Unit
        }
      case DistError(obj) =>
        sent = sent + 1
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
        logger.info("Terminating...akka system")
        mySystem.shutdown()
    }
  }

  val mySystem = ActorSystem("EventDispatcher")

  val listener = mySystem.actorOf(Props[Listener], name = "listener")

  val master = mySystem.actorOf(Props(new Master(100, listener)), name = "master")

  def start() = {
    master ! StartDist
  }

  def terminate() = {
    listener ! Terminate
  }


}
