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
import uk.gov.hmrc.api.models.constants.{ApiErrorCodes, ApiErrorMessages, JsonPaths, MinMaxValues}

enum ApiErrorResponse(val statusCode: Int, val code: String, val message: String) {
  case ForbiddenApiError extends ApiErrorResponse(FORBIDDEN, ApiErrorCodes.forbidden, ApiErrorMessages.forbidden)
  case InternalServerApiError
      extends ApiErrorResponse(
        INTERNAL_SERVER_ERROR,
        ApiErrorCodes.internalServerError,
        ApiErrorMessages.internalServerError
      )
  case NotFoundApiError
      extends ApiErrorResponse(
        NOT_FOUND,
        ApiErrorCodes.matchingResourceNotFound,
        ApiErrorMessages.matchingResourceNotFound
      )
  case MethodNotAllowedApiError
      extends ApiErrorResponse(METHOD_NOT_ALLOWED, ApiErrorCodes.methodNotAllowed, ApiErrorMessages.methodNotAllowed)
  case NotAcceptableApiError
      extends ApiErrorResponse(NOT_ACCEPTABLE, ApiErrorCodes.notAcceptable, ApiErrorMessages.notAcceptable)
  case ServiceUnavailableApiError
      extends ApiErrorResponse(
        SERVICE_UNAVAILABLE,
        ApiErrorCodes.serviceUnavailable,
        ApiErrorMessages.serviceUnavailable
      )
  case RequestEntityTooLargeError
      extends ApiErrorResponse(
        REQUEST_ENTITY_TOO_LARGE,
        ApiErrorCodes.requestEntityTooLarge,
        ApiErrorMessages.requestEntityTooLarge
      )

  case UnauthorizedApiError(reason: String)
      extends ApiErrorResponse(UNAUTHORIZED, ApiErrorCodes.unauthorized, ApiErrorMessages.unauthorized)

  case BadRequestApiError(errors: Seq[ApiErrorDetails])
      extends ApiErrorResponse(BAD_REQUEST, ApiErrorCodes.badRequest, ApiErrorMessages.badRequest)

  def toResult: Result = Status(statusCode)(Json.toJson(this))
}

object ApiErrorResponse {
  implicit val writes: Writes[ApiErrorResponse] = {
    case badRequest: ApiErrorResponse.BadRequestApiError =>
      Json.obj(
        JsonPaths.code    -> badRequest.code,
        JsonPaths.message -> badRequest.message,
        JsonPaths.errors  -> badRequest.errors
      )
    case o                                               =>
      Json.obj(
        JsonPaths.code    -> o.code,
        JsonPaths.message -> o.message
      )
  }
}

enum ApiErrorDetails(val statusCode: Int, val code: String, val message: String, val path: String) {
  case InvalidEoriCountApiError
      extends ApiErrorDetails(
        BAD_REQUEST,
        ApiErrorCodes.invalidFormat,
        ApiErrorMessages.invalidEoriCount(MinMaxValues.maxEori),
        JsonPaths.eoris
      )

  case InvalidEoriApiError(eori: String)
      extends ApiErrorDetails(
        BAD_REQUEST,
        ApiErrorCodes.invalidFormat,
        ApiErrorMessages.invalidEori(eori),
        JsonPaths.eoris
      )
}

object ApiErrorDetails {
  implicit val writes: Writes[ApiErrorDetails] = (o: ApiErrorDetails) =>
    Json.obj(
      JsonPaths.code    -> o.code,
      JsonPaths.message -> o.message,
      JsonPaths.path    -> o.path
    )
}
