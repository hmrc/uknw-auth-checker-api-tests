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
import uk.gov.hmrc.api.utils.resources.Iso8601DateTimeWrites

import java.time.ZonedDateTime

case class EisAuthorisationsResponse(date: ZonedDateTime, eoris: Seq[EisAuthorisationResponse], expectedHttpCode: Int)
    extends Response {
  def toJsonString: String = Json.toJson(this).toString()
  def httpCode: Int        = expectedHttpCode
}

object EisAuthorisationsResponse {
  implicit val zonedDateTimeWrites: Writes[ZonedDateTime] = Iso8601DateTimeWrites.iso8601DateTimeWrites

  implicit val writes: OWrites[EisAuthorisationsResponse] = OWrites[EisAuthorisationsResponse] { response =>
    Json.obj(
      "date"  -> response.date,
      "eoris" -> Json.toJson(response.eoris)
    )
  }

  implicit val reads: Reads[EisAuthorisationsResponse] = Json.reads[EisAuthorisationsResponse]
}

case class EisAuthorisationResponse(eori: String, authorised: Boolean)

object EisAuthorisationResponse {
  implicit val format: OFormat[EisAuthorisationResponse] = Json.format[EisAuthorisationResponse]
}
