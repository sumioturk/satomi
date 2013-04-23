/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/24/13
 * Time: 1:23 AM
 *
 */

import controllers.EventDispatcher
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Booting up steve dispatcher")
    EventDispatcher.start()
  }

  override def onStop(app: Application) {
    Logger.info("Shutting down steve dispatcher")
    EventDispatcher.terminate()
  }

}

