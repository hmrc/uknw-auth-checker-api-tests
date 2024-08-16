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

import play.api.libs.json.*

case class EisErrorsResponse(codeAndMessage: StatusAndMessage, errors: Option[Seq[Errors]] = None)

object EisErrorsResponse {
  implicit val writes: OWrites[EisErrorsResponse] = (response: EisErrorsResponse) => {
    val baseJson = Json.obj(
      "code" -> response.codeAndMessage.code,
      "message" -> response.codeAndMessage.message
    )

    response.errors match {
      case Some(errors) => baseJson + ("errors" -> Json.toJson(errors))
      case None => baseJson
    }
  }

  implicit val reads: Reads[EisErrorsResponse] = (json: JsValue) => {
    for {
      code <- (json \ "code").validate[String]
      message <- (json \ "message").validate[String]
      errors <- (json \ "errors").validateOpt[Seq[Errors]]
      statusAndMessage <- (code, message) match {
        case ("UNAUTHORIZED", "Invalid bearer token") => JsSuccess(StatusAndMessage.InvalidBearerToken)
        case ("UNAUTHORIZED", "Bearer token not supplied") => JsSuccess(StatusAndMessage.BearerTokenNotSupplied)
        case ("UNAUTHORIZED", "The bearer token is invalid, missing, or expired") => JsSuccess(StatusAndMessage.Unauthorized)
        case ("METHOD_NOT_ALLOWED", "This method is not supported") => JsSuccess(StatusAndMessage.MethodNotAllowed)
        case ("NOT_ACCEPTABLE", "Cannot produce an acceptable response. The Accept or Content-Type header is missing or invalid") => JsSuccess(StatusAndMessage.NotAcceptable)
        case ("INTERNAL_SERVER_ERROR", "Unexpected internal server error") => JsSuccess(StatusAndMessage.InternalServerError)
        case _ => JsError("Unknown StatusAndMessage")
      }
    } yield EisErrorsResponse(statusAndMessage, errors)
  }
}
