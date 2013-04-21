package controllers

import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.infrastrucure.MongoRepository
import com.sumioturk.satomi.domain.channel.{ChannelDBObjectConverter, Channel}
import play.api.mvc.{AnyContent, Action}
import org.bson.types.ObjectId
import com.sumioturk.satomi.domain.user.User
import play.api.libs.json.Json._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import com.sumioturk.satomi.domain.channel.ChannelJsonFormat._


/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/21/13
 * Time: 2:25 PM
 *
 */

object ChannelController extends CRUDController {

  val mongoColl = MongoConnection()("satomi")("Channel")

  val channelRepo = new MongoRepository[Channel](
    ChannelDBObjectConverter,
    mongoColl
  )

  def create(): Action[AnyContent] = Action {
    req =>
      req.queryString.get("name") match {
        case Some(name) =>
          val channel = Channel(
            id = ObjectId.get.toString,
            name = name.foldLeft("")(_ + _),
            users = List.empty[User]
          )
          val promiseOfWriteResult = Akka.future(channelRepo.store(channel))
          Async {
            promiseOfWriteResult.map(wr => Ok(toJson(channel)))
          }
        case _ =>
          Forbidden("Invalid Params")
      }
  }

  def read(id: String): Action[AnyContent] = Action {
    Async {
      Akka.future(channelRepo.resolve(id)) map {
        channel: Option[Channel] =>
          channel match {
            case None =>
              NotFound("Not Found %s".format(id))
            case Some(channel) =>
              Ok(toJson[Channel](channel))
          }
      }
    }
  }

  def update(id: String): Action[AnyContent] = Action {
    req =>
      req.getQueryString("name") match {
        case Some(name) =>
          Async {
            Akka.future(channelRepo.resolve(id)) map {
              channelOpt: Option[Channel] =>
                channelOpt match {
                  case None =>
                    NotFound("Not Found %s".format(id))
                  case Some(channel) =>
                    val newChannel = Channel(
                      id = channel.id,
                      name = name,
                      users = channel.users
                    )
                    channelRepo.update(newChannel)
                    Ok(toJson(newChannel))
                }
            }
          }
      }
  }

  def delete(id: String): Action[AnyContent] = Action {
    Async {
      Akka.future(channelRepo.resolve(id)) map {
        channelOpt: Option[Channel] =>
          channelOpt match {
            case None =>
              NotFound("Not Found %s".format(id))
            case Some(channel) =>
              channelRepo.remove(channel)
              Ok("Deleted %s".format(id))
          }
      }
    }
  }

  def channels() = Action {
    Async {
      Akka.future(channelRepo.resolveAll) map {
        channels =>
          Ok(toJson[List[Channel]](channels))
      }
    }
  }
}
