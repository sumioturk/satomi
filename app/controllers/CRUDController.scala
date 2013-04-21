package controllers

import play.api.mvc._

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/21/13
 * Time: 2:34 PM
 *
 */

trait CRUDController extends Controller {

  def create(): Action[AnyContent]

  def read(id: String): Action[AnyContent]

  def update(id: String): Action[AnyContent]

  def delete(id: String): Action[AnyContent]

}
