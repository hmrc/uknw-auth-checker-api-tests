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

import play.api.libs.json.{Json, OFormat, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.Status
import uk.gov.hmrc.api.utils.Iso8601DateTimeWrites

import java.time.ZonedDateTime

case class AuthorisationsResponse(date: ZonedDateTime, eoris: Seq[AuthorisationResponse]) {
  def toResult(expectedStatus: Int): Result = Status(expectedStatus)(Json.toJson(this))
}

object AuthorisationsResponse {

  implicit val zonedDateTimeWrites: Writes[ZonedDateTime] = Iso8601DateTimeWrites.iso8601DateTimeWrites

  implicit val format: OFormat[AuthorisationsResponse] = Json.format[AuthorisationsResponse]
}

case class AuthorisationResponse(eori: String, authorised: Boolean)

object AuthorisationResponse {
  implicit val format: OFormat[AuthorisationResponse] = Json.format[AuthorisationResponse]
}
