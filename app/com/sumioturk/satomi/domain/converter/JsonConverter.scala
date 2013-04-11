package com.sumioturk.satomi.domain.converter

import org.codehaus.jackson.annotate.JsonValue
import play.api.libs.json.JsValue

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/11/13
 * Time: 10:19 PM
 *
 */
trait JsonConverter[T] {

  def convertToJson(obj: T): JsValue

  def convertFromJson(json: JsValue): T

}
