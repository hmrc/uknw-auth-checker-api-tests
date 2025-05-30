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

import org.scalatest.Assertion
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.api.models.*
import uk.gov.hmrc.api.models.ApiErrorDetails.*
import uk.gov.hmrc.api.models.ApiErrorResponse.*
import uk.gov.hmrc.api.models.constants.Eoris
import uk.gov.hmrc.api.specs.BaseSpec
import uk.gov.hmrc.api.utils.generators.EoriGenerator

trait AuthorisationsSpecHelper extends BaseSpec, EoriGenerator, Eoris {

  private val defaultContentType = "application/json"
  private val defaultAcceptInput = "application/vnd.hmrc.1.0+json"

  extension (errors: ApiErrorDetails) {
    def toAPIErrorResponse: ApiErrorResponse = BadRequestApiError(Seq(errors))
  }

  extension (errors: Seq[ApiErrorDetails]) {
    def toAPIErrorResponse: ApiErrorResponse = BadRequestApiError(errors)
  }

  def generateInvalidEoriErrors(eoris: Seq[String]): ApiErrorResponse =
    eoris.map(eori => InvalidEoriApiError(eori)).toAPIErrorResponse

  def generateExpectedOkResponse(eoris: Seq[String], authorised: Boolean): Result = AuthorisationsResponse(
    zonedNow,
    eoris.distinct.map(r => AuthorisationResponse(r, authorised = authorisedEoris.contains(r) && authorised))
  ).toResult(expectedStatus = OK)

  def postAndAssertOk(
    request: AuthorisationRequest,
    expectedResponse: Result
  ): Assertion = {
    val response = checkerApiService.authorisations(
      Json.toJson(request),
      Some(authBearerToken),
      defaultAcceptInput,
      defaultContentType
    )
    response hasStatusAndBodyAndTimestamp expectedResponse
  }

  def postAndAssertError(
    eoris: Seq[String],
    errors: ApiErrorResponse,
    authorisationRequest: Option[JsValue] = None,
    bearerToken: Option[String] = Some(authBearerToken),
    acceptInputHeader: String = defaultAcceptInput,
    contentTypeHeader: String = defaultContentType
  ): Assertion = {

    val request: JsValue = authorisationRequest.getOrElse(Json.toJson(AuthorisationRequest(eoris)))

    val response = checkerApiService.authorisations(request, bearerToken, acceptInputHeader, contentTypeHeader)

    val expectedResponse = errors.toResult
    response hasStatusAndBodyAndTimestamp expectedResponse
  }
}
