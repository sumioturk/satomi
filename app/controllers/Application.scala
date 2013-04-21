package controllers

import play.api.mvc._
import play.Logger
import play.api.libs.concurrent.Akka
import akka.actor.{Actor, Props}
import scala.concurrent.duration._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Application extends Controller {

  def index = Action {
    // say hello
    Logger.info("hello, index action started")

    val Tick = "tick"
    val Tack = "tack"

    val tickActor = Akka.system.actorOf(Props(new Actor {
      def receive = {
        case Tick => Logger.info("that still ticks!")
        case Tack => Logger.warn("... 7 seconds after start, only once")
      }
    }))

    // Repeat every 5 seconds, start 5 seconds after start
    Akka.system.scheduler.schedule(
      5 seconds,
      5 seconds,
      tickActor,
      Tick
    )

    // do only once, 7 seconds after start
    Akka.system.scheduler.scheduleOnce(7 seconds, tickActor, Tack)

    Ok(views.html.index("Your new application is ready."))
  }


}