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

import uk.gov.hmrc.api.models
import uk.gov.hmrc.api.models.{AuthorisationRequest, AuthorisationResponse, AuthorisationsResponse, EisAuthorisationRequest, EisAuthorisationResponse, EisAuthorisationsResponse}

import java.time.{LocalDate, ZonedDateTime}

trait TestData extends Eoris {

  def createRequest(localDateTime: LocalDate, eoris: Seq[String]): AuthorisationRequest =
    AuthorisationRequest(eoris)

  def createResponse(zonedDate: ZonedDateTime, eoris: Seq[String], httpCode: Int): EisAuthorisationsResponse =
    models.EisAuthorisationsResponse(
      zonedDate,
      eoris.map { anEori =>
        EisAuthorisationResponse(anEori, authorisedEoris.contains(anEori))
      },
      httpCode
    )

  def createResponseTest(zonedDate: ZonedDateTime, eoris: Seq[String]): AuthorisationsResponse =
    AuthorisationsResponse(
      zonedDate,
      eoris.map { anEori =>
        AuthorisationResponse(anEori, authorisedEoris.contains(anEori))
      }
    )
}
