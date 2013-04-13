package controllers

import play.mvc.Controller
import com.mongodb.casbah.MongoConnection
import com.sumioturk.satomi.infrastrucure.MongoRepository
import com.sumioturk.satomi.domain.event.{EventDBObjectConverter, Event}

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/13/13
 * Time: 12:34 PM
 *
 */

class EventController extends Controller {

  val mongoColl = MongoConnection()("satomi")("Event")

  val eventRepo = new MongoRepository[Event](
    EventDBObjectConverter,
    mongoColl
  )



}
