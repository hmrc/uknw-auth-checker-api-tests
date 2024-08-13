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

package uk.gov.hmrc.api.specs

import org.scalatest.{Assertion, GivenWhenThen}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.service.UknwAuthCheckerApiService

import java.time.{LocalDate, LocalTime, ZoneId, ZonedDateTime}

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  protected val checkerApiService       = new UknwAuthCheckerApiService
  protected val zonedNow: ZonedDateTime = ZonedDateTime.of(LocalDate.now.atTime(LocalTime.MIDNIGHT), ZoneId.of("UTC"))
  protected val localNow: LocalDate     = LocalDate.now

  implicit class ResponseExtensions(wsResponse: StandaloneWSResponse) {
    def hasStatusAndBody(response: (Int, String)): Assertion = {
      wsResponse.status        shouldBe response._1
      wsResponse.body.toString shouldBe response._2
    }

    def isBadRequest(response: String): Assertion = {
      wsResponse.status        shouldBe 400
      wsResponse.body.toString shouldBe response
    }

    def isMethodNotAllowed(response: String): Assertion = {
      wsResponse.status        shouldBe 405
      wsResponse.body.toString shouldBe response
    }

    // This is for the HEAD request which doesn't receive a body
    def isMethodNotAllowed: Assertion =
      wsResponse.status shouldBe 405
  }
}
