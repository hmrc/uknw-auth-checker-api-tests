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

package uk.gov.hmrc.api.utils.resources.responses

import play.api.libs.json.{Json, OFormat, Writes}
import uk.gov.hmrc.api.utils.resources.Iso8601DateTimeWrites

import java.time.ZonedDateTime

sealed trait Errors {
  def code: String
  def message: String
}

object Errors {

  case class InvalidEoriFormat(eori: String) extends Errors {
    override def code: String    = "INVALID_FORMAT"
    override def message: String = s"$eori is not a supported EORI number"
  }

  case object MissingEoriField extends Errors {
    override def code: String    = "INVALID_FORMAT"
    override def message: String = "eoris field missing from JSON"
  }

  case object WrongNumberOfEoris extends Errors {
    override def code: String    = "INVALID_FORMAT"
    override def message: String = "The request payload must contain between 1 and 3000 EORI entries"
  }

  case object InvalidBearerToken extends Errors {
    override def code: String    = "UNAUTHORIZED"
    override def message: String = "Invalid bearer token"
  }

  case object BearerTokenNotSupplied extends Errors {
    override def code: String    = "UNAUTHORIZED"
    override def message: String = "Bearer token not supplied"
  }

  case object Unauthorized extends Errors {
    override def code: String    = "UNAUTHORIZED"
    override def message: String = "The bearer token is invalid, missing, or expired"
  }

  case object MethodNotAllowed extends Errors {
    override def code: String    = "METHOD_NOT_ALLOWED"
    override def message: String = "This method is not supported"
  }

  case object NotAcceptable extends Errors {
    override def code: String    = "NOT_ACCEPTABLE"
    override def message: String =
      "Cannot produce an acceptable response. The Accept or Content-Type header is missing or invalid"
  }

  case object InternalServerError extends Errors {
    override def code: String    = "INTERNAL_SERVER_ERROR"
    override def message: String = "Unexpected internal server error"
  }

  implicit val invalidEoriFormatFormat: OFormat[InvalidEoriFormat]                = Json.format[InvalidEoriFormat]
  implicit val missingEoriFieldFormat: OFormat[MissingEoriField.type]             = Json.format[MissingEoriField.type]
  implicit val wrongNumberOfEorisFormat: OFormat[WrongNumberOfEoris.type]         = Json.format[WrongNumberOfEoris.type]
  implicit val invalidBearerTokenFormat: OFormat[InvalidBearerToken.type]         = Json.format[InvalidBearerToken.type]
  implicit val bearerTokenNotSuppliedFormat: OFormat[BearerTokenNotSupplied.type] =
    Json.format[BearerTokenNotSupplied.type]
  implicit val unauthorizedFormat: OFormat[Unauthorized.type]                     = Json.format[Unauthorized.type]
  implicit val methodNotAllowedFormat: OFormat[MethodNotAllowed.type]             = Json.format[MethodNotAllowed.type]
  implicit val notAcceptableFormat: OFormat[NotAcceptable.type]                   = Json.format[NotAcceptable.type]
  implicit val internalServerErrorFormat: OFormat[InternalServerError.type]       = Json.format[InternalServerError.type]
}
