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
import uk.gov.hmrc.api.models.ApiErrorDetails.*
import uk.gov.hmrc.api.models.ApiErrorResponse.*
import uk.gov.hmrc.api.models.AuthorisationRequest.toInvalidJsonStructure
import uk.gov.hmrc.api.models.constants.ApiErrorMessages
import uk.gov.hmrc.api.models.constants.ApiErrorMessages.*
import uk.gov.hmrc.api.models.*
import uk.gov.hmrc.api.utils.TestData
import uk.gov.hmrc.api.utils.generators.EoriGenerator

class AuthorisationsSpec extends BaseSpec, EoriGenerator, TestData {

  Feature("Is bearer token valid") {
    Scenario("Checking bearer token") {
      When("Getting bearer token")
      Then("Said Bearer Token shouldn't contain an error")
      authBearerToken shouldNot contain("Could not obtain auth bearer token. Auth Service Response:")
    }
  }

  Feature("200 case scenarios") {

    Scenario("Happy path with single authorised EORI - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris = useEoriGenerator(1)

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = true))
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200")
      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Happy path with multiple authorised EORIs - 200 OK") {
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
      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Happy path with single unauthorised EORI - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris = useEoriGenerator(1, Some(0))

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = false))
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200")
      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Happy path with multiple unauthorised EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")

      val eoris = useEoriGenerator(fetchRandomNumber(2, authorisedEoris.size), Some(0))

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = false))
      ).toResult(expectedStatus = OK)

      Then("I am returned a status code 200")
      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Happy path with single authorised and unauthorised EORI - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris = useEoriGenerator(2, Some(1))

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = authorisedEoris.contains(r)))
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200")
      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Happy path with multiple authorised and unauthorised EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris = useEoriGenerator(authorisedEoris.size, Some(authorisedEoris.size / 2))

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = authorisedEoris.contains(r)))
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200")
      response hasStatusAndBodyAndTimestamp expectedResponse
    }
  }

  Feature("200, case duplicate scenarios") {
    Scenario("Happy path with two authorised duplicate EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")

      val eori  = useEoriGenerator(1).head
      val eoris = Seq(eori, eori)

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.distinct.map(r => AuthorisationResponse(r, authorised = true))
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)
      Then("I am returned a status code 200 and the correct response")
      response hasStatusAndBodyAndTimestamp expectedResponse
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
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      Then("I am returned a status code 200 and the correct response")

      response hasStatusAndBodyAndTimestamp expectedResponse
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
      ).toResult(expectedStatus = OK)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      Then("I am returned a status code 200 and the correct response")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }
  }

  Feature("400, BAD_REQUEST case scenarios") {

    Scenario("Singular Invalid Formatted EORI - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris                = useGarbageGenerator(1)
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = Seq(InvalidEoriApiError(eoris.head))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Multiple Invalid Formatted EORIs - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris = useGarbageGenerator(
        authorisedEoris.size
      )

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = eoris.map(eori => InvalidEoriApiError(eori))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Multiple Invalid Formatted EORIs with some valid authorised EORIs - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val invalidEoris    = useGarbageGenerator(fetchRandomNumber(1, 10))
      val authorisedEoris = useEoriGenerator(fetchRandomNumber(1, 10))
      val eoris           = invalidEoris ++ authorisedEoris

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = invalidEoris.map(eori => InvalidEoriApiError(eori))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Multiple Invalid Formatted EORIs with some valid unauthorised EORIs - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val invalidEoris      = useGarbageGenerator(fetchRandomNumber(1, 10))
      val unauthorisedEoris = useEoriGenerator(fetchRandomNumber(1, 10), Some(0))
      val eoris             = invalidEoris ++ unauthorisedEoris

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = invalidEoris.map(eori => InvalidEoriApiError(eori))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario(
      "Multiple Invalid Formatted EORIs with some valid authorised and valid unauthorised EORIs - 400 Bad Request"
    ) {
      Given("a bearer token")
      And("an invalid payload")

      val invalidEoris      = useGarbageGenerator(fetchRandomNumber(1, 10))
      val unauthorisedEoris = useEoriGenerator(fetchRandomNumber(1, 10), Some(0))
      val authorisedEoris   = useEoriGenerator(fetchRandomNumber(1, 10))
      val eoris             = invalidEoris ++ unauthorisedEoris ++ authorisedEoris

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = invalidEoris.map(eori => InvalidEoriApiError(eori))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Duplicate Invalid Formatted EORI - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val eori                 = useGarbageGenerator(1).head
      val eoris                = Seq(eori, eori)
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = Seq(InvalidEoriApiError(eoris.head))

      val expectedResponse = BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Not enough EORIS (0) - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val authorisationRequest = AuthorisationRequest(Seq.empty)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = Seq(InvalidEoriCountApiError)

      val expectedResponse =
        BadRequestApiError(errors)

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse.toResult
    }

    Scenario("Too many EORIS (3001) - 400 Bad Request") {
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

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("JSON structure is incorrect - 400 Bad Request") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris                = useEoriGenerator(1)
      val authorisationRequest = toInvalidJsonStructure(AuthorisationRequest(eoris))

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val errors = Seq(JSONStructureInvalidApiError)

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val expectedResponse =
        BadRequestApiError(errors).toResult

      Then("I am returned a status code 400")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }
  }

  Feature("401, UNAUTHORIZED case scenarios") {
    Scenario("Invalid Bearer Token - 401 Unauthorized") {
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

      response hasStatusAndBodyAndTimestamp expectedResponse

    }

    Scenario("Missing Bearer Token - 401 Unauthorized") {
      Given("There's no bearer token")
      And("a valid payload")

      val eoris                = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest))

      val expectedResponse =
        UnauthorizedApiError(ApiErrorMessages.unauthorized).toResult

      Then("I am returned a status code 401")

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

  }

  Feature("406, NOT_ACCEPTABLE case scenarios") {
    Scenario("Invalid Accept - 406 Not Acceptable") {
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

      response hasStatusAndBodyAndTimestamp expectedResponse
    }

    Scenario("Invalid Content-Type - 406 Not Acceptable") {
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

      response hasStatusAndBodyAndTimestamp expectedResponse
    }
  }

  Feature("413, REQUEST_ENTITY_TOO_LARGE case Scenarios") {
    Scenario("Request entity too large - 413 Request Entity Too Large") {
      Given("Valid bearer token")
      And("an invalid payload with a massive string size of EORIs causing the request to be over 100KB")

      val eoris   = useUnrestrictedGarbageGenerator(authorisedEoris.size)
      val request = createRequest(localNow, eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      val expectedResponse =
        RequestEntityTooLargeError.toResult

      Then("I am returned a status code 413")
      response hasStatusAndBodyAndTimestamp expectedResponse
    }
  }
}
