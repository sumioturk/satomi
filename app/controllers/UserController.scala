package controllers

import play.api.mvc._
import com.sumioturk.satomi.domain.user.{UserDBObjectConverter, UserJsonConverter, User}
import com.sumioturk.satomi.domain.user.UserJsonConverter._
import com.sumioturk.satomi.infrastrucure.MongoRepository
import com.mongodb.casbah.MongoConnection
import com.mongodb.{CommandResult, WriteResult}
import java.util.UUID

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

  def register() = Action {
    req =>
      (req.queryString.get("name"), req.queryString.get("isGay")) match {
        case (Some(name), Some(isGay)) =>
          val user = User(
            id = UUID.randomUUID().toString,
            name = name.foldLeft("")(_ + _),
            isGay = isGay.foldLeft("")(_ + _) == "true"
          )
          userRepo.store(user)
          Ok(convertToJson(user))
        case _ =>
          Forbidden("Invalid Params")
      }
  }

  def find(id: String) = Action {
    userRepo.resolve(id) match {
      case None =>
        NotFound("Not Fount %s".format(id))
      case Some(user) =>
        Ok(convertToJson(user))
    }
  }

  def remove(id: String) = Action {
    userRepo.resolve(id) match {
      case None =>
        NotFound("Not Found %s".format(id))
      case Some(user) =>
        userRepo.remove(user)
        Ok("Deleted %s".format(id))
    }
  }

}
