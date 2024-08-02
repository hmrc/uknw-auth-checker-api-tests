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

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsValue
import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.service.UknwAuthCheckerApiService

import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId, ZonedDateTime}

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {

  protected val checkerApiService       = new UknwAuthCheckerApiService
  protected val zonedNow: ZonedDateTime = ZonedDateTime.of(LocalDate.now.atTime(LocalTime.MIDNIGHT), ZoneId.of("UTC"))
  protected val localNow: LocalDate     = LocalDate.now

  def comprehensivelyAssert(
    response: StandaloneWSRequest#Self#Response,
    expectedCode: Int,
    expectedRes: JsValue
  ): Unit = {
    response.status shouldBe expectedCode
    response.body   shouldBe expectedRes.toString
  }

  def comprehensivelyAssert(
    response: StandaloneWSRequest#Self#Response,
    expectedCode: Int,
    expectedRes: String
  ): Unit = {
    response.status shouldBe expectedCode
    response.body   shouldBe expectedRes
  }
}
