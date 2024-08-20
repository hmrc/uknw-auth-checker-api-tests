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

import play.api.http.Status.*
import play.api.libs.json.*
import play.api.mvc.Result
import play.api.mvc.Results.Status
import uk.gov.hmrc.api.models.constants.CustomRegex.invalidFormatOfEorisPattern
import uk.gov.hmrc.api.models.constants.{ApiErrorCodes, ApiErrorMessages, JsonPaths}

sealed trait ApiErrorResponse {
  def statusCode: Int
  def code: String
  def message: String

  private def convertErrorsToReadableFormat: JsValue =
    this match {
      case badRequestError: BadRequestApiError     =>
        Json.toJson(badRequestError)(ApiErrorResponse.badRequestApiErrorWrites)
      case validationError: JsonValidationApiError =>
        Json.toJson(validationError)(ApiErrorResponse.jsonValidationApiErrorWrites)
      case _                                       => Json.toJson(this)
    }

  def toResult: Result = Status(statusCode)(Json.toJson(convertErrorsToReadableFormat))
}

object ApiErrorResponse {
  implicit val jsonValidationApiErrorWrites: Writes[JsonValidationApiError] = Writes { model =>
    Json.obj(
      JsonPaths.code    -> model.code,
      JsonPaths.message -> model.message,
      JsonPaths.errors  -> model.getErrors
    )
  }

  implicit val badRequestApiErrorWrites: Writes[BadRequestApiError] = Writes { model =>
    Json.obj(
      JsonPaths.code    -> model.code,
      JsonPaths.message -> model.message,
      JsonPaths.errors  -> model.getErrors
    )
  }

  implicit val writes: Writes[ApiErrorResponse] = (o: ApiErrorResponse) =>
    JsObject(Seq(JsonPaths.code -> JsString(o.code), JsonPaths.message -> JsString(o.message)))
}

case object ForbiddenApiError extends ApiErrorResponse {
  val statusCode: Int = FORBIDDEN
  val code: String    = ApiErrorCodes.forbidden
  val message: String = ApiErrorMessages.forbidden
}

case object InternalServerApiError extends ApiErrorResponse {
  val statusCode: Int = INTERNAL_SERVER_ERROR
  val code: String    = ApiErrorCodes.internalServerError
  val message: String = ApiErrorMessages.internalServerError
}

case object NotFoundApiError extends ApiErrorResponse {
  val statusCode: Int = NOT_FOUND
  val code: String    = ApiErrorCodes.matchingResourceNotFound
  val message: String = ApiErrorMessages.matchingResourceNotFound
}

case object MethodNotAllowedApiError extends ApiErrorResponse {
  val statusCode: Int = METHOD_NOT_ALLOWED
  val code: String    = ApiErrorCodes.methodNotAllowed
  val message: String = ApiErrorMessages.methodNotAllowed
}

case object NotAcceptableApiError extends ApiErrorResponse {
  val statusCode: Int = NOT_ACCEPTABLE
  val code: String    = ApiErrorCodes.notAcceptable
  val message: String = ApiErrorMessages.notAcceptable
}

case object ServiceUnavailableApiError extends ApiErrorResponse {
  val statusCode: Int = SERVICE_UNAVAILABLE
  val code: String    = ApiErrorCodes.serviceUnavailable
  val message: String = ApiErrorMessages.serviceUnavailable
}

final case class UnauthorizedApiError(reason: String) extends ApiErrorResponse {
  val statusCode: Int = UNAUTHORIZED
  val code: String    = ApiErrorCodes.unauthorized
  val message: String = ApiErrorMessages.unauthorized
}

final case class BadRequestApiError(errorMessages: String) extends ApiErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = ApiErrorCodes.badRequest
  val message: String = ApiErrorMessages.invalidRequest

  val getErrors: JsValue = {
    val errors = errorMessages
      .split("Invalid")
      .filter(_.matches(invalidFormatOfEorisPattern))
      .flatMap(_.trim.split(":").lastOption.map(_.split(",").map(_.trim)))
      .flatten
      .map { eori =>
        Json.obj(
          JsonPaths.code    -> ApiErrorCodes.invalidFormat,
          JsonPaths.message -> ApiErrorMessages.invalidEori(eori),
          JsonPaths.path    -> JsonPaths.eoris
        )
      }

    Json.toJson(errors)
  }
}

final case class JsonValidationApiError(jsErrors: JsError) extends ApiErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = ApiErrorCodes.badRequest
  val message: String = ApiErrorMessages.badRequest

  val getErrors: JsValue = JsArray(
    jsErrors.errors.flatMap { case (jsPath, pathErrors) =>
      val path = jsPath.toJsonString.stripPrefix("obj.")
      pathErrors.map { validationError =>
        Json.obj(
          "code"    -> ApiErrorCodes.invalidFormat,
          "message" -> validationError.message,
          "path"    -> path
        )
      }
    }
  )
}
