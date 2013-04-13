package controllers

import play.api.mvc._
import com.sumioturk.satomi.domain.user.{UserDBObjectConverter, User}
import com.sumioturk.satomi.infrastrucure.MongoRepository
import com.mongodb.casbah.MongoConnection
import java.util.UUID
import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.libs.json.Json._
import com.sumioturk.satomi.domain.converter.JsonConversionProtocol.userWrite

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 10:13 PM
 *
 */

object UserController extends Controller {

  val mongoColl = MongoConnection()("satomi")("User")

  val userRepo = new MongoRepository[User](
    UserDBObjectConverter,
    mongoColl)

  def create() = Action {
    req =>
      (req.queryString.get("name"), req.queryString.get("isGay")) match {
        case (Some(name), Some(isGay)) =>
          val user = User(
            id = UUID.randomUUID().toString,
            name = name.foldLeft("")(_ + _),
            isGay = isGay.foldLeft("")(_ + _) == "true"
          )
          val promiseOfWriteResult = Akka.future(userRepo.store(user))
          Async {
            promiseOfWriteResult.map(wr => Ok(toJson(user)))
          }
        case _ =>
          Forbidden("Invalid Params")
      }
  }

  def read(id: String) = Action {
    Async {
      Akka.future(userRepo.resolve(id)) map {
        user: Option[User] =>
          user match {
            case None =>
              NotFound("Not Found %s".format(id))
            case Some(user) =>
              Ok(toJson(user))
          }
      }
    }
  }

  def delete(id: String) = Action {
    Async {
      Akka.future(userRepo.resolve(id)) map {
        userOpt: Option[User] =>
          userOpt match {
            case None =>
              NotFound("Not Found %s".format(id))
            case Some(user) =>
              userRepo.remove(user)
              Ok("Deleted %s".format(id))
          }
      }
    }
  }

  def update(id: String) = Action {
    req =>
      (req.getQueryString("name"), req.getQueryString("isGay")) match {
        case (Some(name), Some(isGay)) =>
          Async {
            Akka.future(userRepo.resolve(id)).map {
              userOpt: Option[User] =>
                userOpt match {
                  case None =>
                    NotFound("Not Found %s".format(id))
                  case Some(user) =>
                    val newUser = User(id = user.id, name = name, isGay = isGay == "true")
                    userRepo.update(newUser)
                    Ok(toJson(newUser))
                }
            }
          }
        case _ =>
          Forbidden("Invalid Params")
      }
  }

}
