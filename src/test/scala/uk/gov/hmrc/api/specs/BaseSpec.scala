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

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.GivenWhenThen
import play.api.libs.json.JsValue
import uk.gov.hmrc.api.service.UknwAuthCheckerApiService
import uk.gov.hmrc.api.utils.JsonGetter.getJsonFile

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {
  val checkerApiService = new UknwAuthCheckerApiService

  // Req
  val req200_single: JsValue   = getJsonFile("/requests/authRequest200_single.json")
  val req200_multiple: JsValue = getJsonFile("/requests/authRequest200_multiple.json")
  val req403_single: JsValue   = getJsonFile("/requests/authRequest403_single.json")

  // Res
  val expectedRes401_invalid: String     = """{"code":"UNAUTHORIZED","message":"Invalid bearer token"}"""
  val expectedRes401_notSupplied: String = """{"code":"UNAUTHORIZED","message":"Bearer token not supplied"}"""
  val expected403_forbidden: String      = """{"code":"FORBIDDEN","message":"You are not allowed to access this resource"}"""
}
