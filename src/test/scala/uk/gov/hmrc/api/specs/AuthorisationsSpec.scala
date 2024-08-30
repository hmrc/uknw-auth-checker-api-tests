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

import play.api.http.Status
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.api.models.*
import uk.gov.hmrc.api.models.constants.ApiErrorMessages
import uk.gov.hmrc.api.service.AuthService
import uk.gov.hmrc.api.utils.TestData
import uk.gov.hmrc.api.utils.generators.EoriGenerator

class AuthorisationsSpec extends BaseSpec with EoriGenerator with TestData {
  Feature("200 case Scenarios") {

    Scenario("Happy path with single EORI - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris = useEoriGenerator(1)

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = true))
      ).toResult(expectedStatus = 200)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200")
      response hasStatusAndBody expectedResponse
    }

    Scenario("Happy path with multiple EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")

      val eoris = useEoriGenerator(fetchRandomNumber(2, authorisedEoris.size))

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = true))
      ).toResult(expectedStatus = OK)

      Then("I am returned a status code 200")
      response hasStatusAndBody expectedResponse
    }
  }

  Feature("200 case duplicate scenarios") {

    Scenario("Happy path with two authorised duplicate EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eori  = useEoriGenerator(1).head
      val eoris = Seq(eori, eori)

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.distinct.map(r => AuthorisationResponse(r, authorised = true))
      ).toResult(expectedStatus = 200)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200 and the correct response")
      response hasStatusAndBody expectedResponse
    }

    Scenario("Happy path with two unauthorised duplicate EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eori  = useEoriGenerator(1, Some(0)).head
      val eoris = Seq(eori, eori)

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.distinct.map(r => AuthorisationResponse(r, authorised = authorisedEoris.contains(r)))
      ).toResult(expectedStatus = 200)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200 and the correct response")
      response hasStatusAndBody expectedResponse
    }

    Scenario("Happy path with two unauthorised duplicate EORIs and two authorised duplicate EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val unauthorisedEori = useEoriGenerator(1, Some(0)).head
      val authorisedEori   = useEoriGenerator(1).head

      val eoris = Seq(unauthorisedEori, unauthorisedEori, authorisedEori, authorisedEori)

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.distinct.map(r => AuthorisationResponse(r, authorised = authorisedEoris.contains(r)))
      ).toResult(expectedStatus = 200)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200 and the correct response")
      response hasStatusAndBody expectedResponse
    }
  }

  Feature("400, BAD_REQUEST case scenarios") {

    Scenario("400 Singular Invalid Formated Eori") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris                = useGarbageGenerator(1)
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = Seq(invalidEoriApiError(eoris.head))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Multiple Invalid Formated Eoris") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris = useGarbageGenerator(
        authorisedEoris.size
      )

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = eoris.map(eori => invalidEoriApiError(eori))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Duplicate Invalid Formated Eori") {
      Given("a bearer token")
      And("an invalid payload")

      val eori                 = useGarbageGenerator(1).head
      val eoris                = Seq(eori, eori)
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = Seq(invalidEoriApiError(eoris.head))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Not enough EORIS (0)") {
      Given("a bearer token")
      And("an invalid payload")

      val authorisationRequest = AuthorisationRequest(Seq.empty)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = Seq(InvalidEoriCountApiError)

      val expectedResponse =
        BadRequestApiError(errors)

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse.toResult
    }

    Scenario("400 Too many EORIS (3001)") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris                = useEoriGenerator(authorisedEoris.size + 10, Some(authorisedEoris.size))
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val errors = Seq(InvalidEoriCountApiError)

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val expectedResponse =
        BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }
  }

  Feature("401, UNAUTHORIZED case scenarios") {
    Scenario("Invalid Bearer Token") {
      Given("an invalid bearer token")

      val eoris                  = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val anInvalidToken: String = "Invalid Token"

      And("a valid payload")
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), anInvalidToken)

      val expectedResponse =
        UnauthorizedApiError(ApiErrorMessages.unauthorized).toResult

      Then("I am returned a status code 401")
      response hasStatusAndBody expectedResponse

    }

    Scenario("Missing Bearer Token") {
      Given("There's no bearer token")
      And("a valid payload")

      val eoris                = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest))

      val expectedResponse =
        UnauthorizedApiError(ApiErrorMessages.unauthorized).toResult

      Then("I am returned a status code 401")
      response hasStatusAndBody expectedResponse
    }

  }

  Feature("406, NOT_ACCEPTABLE case scenarios") {
    Scenario("406 invalid Accept") {
      Given("Valid bearer token")
      And("a valid payload")

      val eoris                = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val authorisationRequest = AuthorisationRequest(eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response =
        checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken, acceptInput = "invalid")

      val expectedResponse =
        NotAcceptableApiError.toResult

      Then("I am returned a status code 406")
      response hasStatusAndBody expectedResponse
    }

    Scenario("406 invalid Content-Type") {
      Given("Valid bearer token")
      And("a valid payload")

      val eoris                = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val authorisationRequest = AuthorisationRequest(eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response =
        checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken, contentType = "invalid")

      val expectedResponse =
        NotAcceptableApiError.toResult

      Then("I am returned a status code 406")
      response hasStatusAndBody expectedResponse
    }
  }

  Feature("413, REQUEST_ENTITY_TOO_LARGE case Scenarios") {
    Scenario("413, request entity too large error") {
      Given("Valid bearer token")
      And("a invalid payload with massive string size of EORIs")

      val eoris   = useUnrestrictedGarbageGenerator(authorisedEoris.size)
      val request = createRequest(localNow, eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      val expectedResponse =
        RequestEntityTooLargeError.toResult

      Then("I am returned a status code 413")
      response hasStatusAndBody expectedResponse
    }
  }
}
