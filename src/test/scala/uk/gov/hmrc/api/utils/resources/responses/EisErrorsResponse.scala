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

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.*
import uk.gov.hmrc.api.utils.resources.Iso8601DateTimeWrites

import java.time.ZonedDateTime

case class EisErrorsResponse(code: String, message: String, errors: Option[Seq[Errors]] = None)

object EisErrorsResponse {
  implicit val writes: OWrites[EisErrorsResponse] = (
    (__ \ "code").write[String] and
      (__ \ "message").write[String] and
      (__ \ "errors").writeNullable[Seq[Errors]]
  )(o => Tuple.fromProductTyped(o))

  implicit val reads: Reads[EisErrorsResponse] = Json.reads[EisErrorsResponse]
}
