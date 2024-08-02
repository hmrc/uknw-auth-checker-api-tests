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

package uk.gov.hmrc.api.utils

import uk.gov.hmrc.api.utils.resources.requests._
import uk.gov.hmrc.api.utils.resources.responses._

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

trait TestData extends Requests200 with Request400 with Responses200 with ErrorResponses {

  def createRequest(localDateTime: LocalDate, eoris: Seq[String]): EisAuthorisationRequest =
    EisAuthorisationRequest(Some(localDateTime), "UKNW", eoris)

  def createResponse(zonedDate: ZonedDateTime, eoris: Seq[String]): EisAuthorisationsResponse =
    EisAuthorisationsResponse(
      zonedDate,
      eoris.map { anEori =>
        EisAuthorisationResponse(anEori, true)
      }
    )
}
