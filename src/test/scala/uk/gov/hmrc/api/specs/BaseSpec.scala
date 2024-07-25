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
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.api.service.UknwAuthCheckerApiService
import uk.gov.hmrc.api.utils.JsonGetter.getJsonFile

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers {
  val checkerApiService = new UknwAuthCheckerApiService

  // Req
  val req200_single: JsValue       = getJsonFile("/requests/authRequest200_single.json")
  val req200_multiple: JsValue     = getJsonFile("/requests/authRequest200_multiple.json")
  val req400_invalidForm: JsValue  = getJsonFile("/requests/authRequest400_invalidForm.json")
  val req400_dateForm: JsValue     = getJsonFile("/requests/authRequest400_dateForm.json")
  val req400_singleEori: JsValue   = getJsonFile("/requests/authRequest400_dateForm.json")
  val req400_multipleEori: JsValue = getJsonFile("/requests/authRequest400_multipleEori.json")
  val req400_mixedErrors: JsValue  = getJsonFile("/requests/authRequest400_mixedErrors.json")
  val req400_missing: JsValue      = getJsonFile("/requests/authRequest400_missingBits.json")
  val req400_tooMany: JsValue      = getJsonFile("/requests/authRequest400_tooManyEoris.json")
  val req400_noEoris: JsValue      = Json.parse("""{
                                                  |  "date": "2024-02-08",
                                                  |  "eoris" : []
                                                  |}""".stripMargin)

  val req403_single: JsValue = getJsonFile("/requests/authRequest403_single.json")

  // Res
  val expectedRes200_single: String   = getJsonFile("/responses/authResponse200_single.json").toString
  val expectedRes200_multiple: String = getJsonFile("/responses/authResponse200_multiple.json").toString

  val expectedRes400_dateForm: String           =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"2024-02-xx is not a valid date in the format YYYY-MM-DD","path":"date"}]}"""
  val expectedRes400_invalidForm: String        =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"date field missing from JSON","path":"date"}]}"""
  val expectedRes400_singleEori: String         =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"2024-02-xx is not a valid date in the format YYYY-MM-DD","path":"date"}]}"""
  val expectedRes400_multipleEori: String       =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"ABCD000000000200 is not a supported EORI number","path":"eoris"},{"code":"INVALID_FORMAT","message":"EFGH000000000200 is not a supported EORI number","path":"eoris"}]}"""
  val expectedRes400_mixedErrors: String        =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"ABCD000000000200 is not a supported EORI number","path":"eoris"},{"code":"INVALID_FORMAT","message":"2024-02-xx is not a valid date in the format YYYY-MM-DD","path":"date"}]}"""
  val expectedRes400_missing: String            =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"date field missing from JSON","path":"date"},{"code":"INVALID_FORMAT","message":"eoris field missing from JSON","path":"eoris"}]}"""
  val expectedRes400_wrongNumberOfEoris: String =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"The request payload must contain between 1 and 3000 EORI entries","path":"eoris"}]}"""

  val expectedRes401_invalid: String     = """{"code":"UNAUTHORIZED","message":"Invalid bearer token"}"""
  val expectedRes401_notSupplied: String = """{"code":"UNAUTHORIZED","message":"Bearer token not supplied"}"""

  val expectedRes403_forbidden: String  =
    """{"code":"FORBIDDEN","message":"You are not allowed to access this resource"}"""
  val expectedRes405_notAllowed: String = """{"code":"METHOD_NOT_ALLOWED","message":"This method is not supported"}"""
  val expectedRes406: String            =
    """{"code":"NOT_ACCEPTABLE","message":"Cannot produce an acceptable response. The Accept or Content-Type header is missing or invalid"}"""
  val expectedRes500: String            = """{"code":"INTERNAL_SERVER_ERROR","message":"Unexpected internal server error"}"""
}
