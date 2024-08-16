/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.api.models

import play.api.libs.json._

sealed trait StatusAndMessage {
  def code: String

  def message: String

  def httpCode: Int
}

object StatusAndMessage {

  case object InvalidBearerToken extends StatusAndMessage {
    override val code: String = "UNAUTHORIZED"
    override val message: String = "Invalid bearer token"
    override val httpCode: Int = 401
  }

  case object BearerTokenNotSupplied extends StatusAndMessage {
    override val code: String = "UNAUTHORIZED"
    override val message: String = "Bearer token not supplied"
    override val httpCode: Int = 401
  }

  case object Unauthorized extends StatusAndMessage {
    override val code: String = "UNAUTHORIZED"
    override val message: String = "The bearer token is invalid, missing, or expired"
    override val httpCode: Int = 401
  }

  case object MethodNotAllowed extends StatusAndMessage {
    override val code: String = "METHOD_NOT_ALLOWED"
    override val message: String = "This method is not supported"
    override val httpCode: Int = 405
  }

  case object NotAcceptable extends StatusAndMessage {
    override val code: String = "NOT_ACCEPTABLE"
    override val message: String = "Cannot produce an acceptable response. The Accept or Content-Type header is missing or invalid"
    override val httpCode: Int = 406
  }

  case object InternalServerError extends StatusAndMessage {
    override val code: String = "INTERNAL_SERVER_ERROR"
    override val message: String = "Unexpected internal server error"
    override val httpCode: Int = 500
  }

  private val statusAndMessages: Map[(String, String), StatusAndMessage] = Map(
    ("UNAUTHORIZED", "Invalid bearer token") -> InvalidBearerToken,
    ("UNAUTHORIZED", "Bearer token not supplied") -> BearerTokenNotSupplied,
    ("UNAUTHORIZED", "The bearer token is invalid, missing, or expired") -> Unauthorized,
    ("METHOD_NOT_ALLOWED", "This method is not supported") -> MethodNotAllowed,
    ("NOT_ACCEPTABLE", "Cannot produce an acceptable response. The Accept or Content-Type header is missing or invalid") -> NotAcceptable,
    ("INTERNAL_SERVER_ERROR", "Unexpected internal server error") -> InternalServerError
  )

  implicit val writes: Writes[StatusAndMessage] = (o: StatusAndMessage) => Json.obj(
    "code" -> o.code,
    "message" -> o.message
  )

  implicit val reads: Reads[StatusAndMessage] = (json: JsValue) => {
    val code = (json \ "code").as[String]
    val message = (json \ "message").as[String]
    statusAndMessages.get((code, message))
      .map(JsSuccess(_))
      .getOrElse(JsError("Unknown StatusAndMessage"))
  }
}

