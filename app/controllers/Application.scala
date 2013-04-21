package controllers

import play.api.mvc._
import play.api.libs.iteratee.Enumerator

object Application extends Controller {

  def index = Action {
    Ok.stream(
      Enumerator("kiki", "foo", "bar").andThen(Enumerator.eof)
    )
  }

}