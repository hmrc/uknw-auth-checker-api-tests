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
import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError}
import uk.gov.hmrc.api.models.*
import uk.gov.hmrc.api.models.constants.{ApiErrorMessages, MinMaxValues}
import uk.gov.hmrc.api.service.AuthService
import uk.gov.hmrc.api.utils.TestData
import uk.gov.hmrc.api.utils.generators.EoriGenerator

class AuthorisationsSpec extends BaseSpec with EoriGenerator with TestData {
  private val myAuthService           = new AuthService
  private val authBearerToken: String = myAuthService.getAuthBearerToken

  Feature("Example of creating bearer token") {
    Scenario("Checking bearer token") {
      When("Getting bearer token")

      Then("Said Bearer Token shouldn't contain an error")
      authBearerToken shouldNot contain("Could not obtain auth bearer token. Auth Service Response:")
    }
  }

  Feature("200 case Scenarios") {

    Scenario("Happy path with single EORI - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris = useEoriGenerator(1, Some(1))

      val authorisationRequest = AuthorisationRequest(eoris)

      val expectedResponse = AuthorisationsResponse(
        zonedNow,
        authorisationRequest.eoris.map(r => AuthorisationResponse(r, authorised = true))
      ).toResult(expectedStatus = OK)

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

  Feature("400, BAD_REQUEST case scenarios") {

    Scenario("400 Bad Eori") {
      Given("a bearer token")

      And("an invalid payload")
      val eoris                = useGarbageGenerator(1)
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val jsError = JsError(JsPath \ "eoris", JsonValidationError(ApiErrorMessages.invalidEori(eoris.head)))

      val expectedResponse = JsonValidationApiError(jsError).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Bad Eoris") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris = useGarbageGenerator(
        authorisedEoris.size
      )

      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val errors = eoris.map(eori => JsonValidationError(ApiErrorMessages.invalidEori(eori)))

      val jsError = JsError(
        Seq(
          JsPath \ "eoris" -> errors
        )
      )

      val expectedResponse =
        JsonValidationApiError(jsError).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Not enough EORIS (0)") {
      Given("a bearer token")
      And("an invalid payload")
      val authorisationRequest = AuthorisationRequest(Seq.empty)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val jsError =
        JsError(JsPath \ "eoris", JsonValidationError(ApiErrorMessages.invalidEoriCount(MinMaxValues.maxEori)))

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val expectedResponse =
        JsonValidationApiError(jsError).toResult

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Too many EORIS (3001)") {
      Given("a bearer token")
      And("an invalid payload")
      val eoris                = useEoriGenerator(authorisedEoris.size + 10, Some(authorisedEoris.size))
      val authorisationRequest = AuthorisationRequest(eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")

      val jsError =
        JsError(JsPath \ "eoris", JsonValidationError(ApiErrorMessages.invalidEoriCount(MinMaxValues.maxEori)))

      val response = checkerApiService.authorisations(Json.toJson(authorisationRequest), authBearerToken)

      val expectedResponse =
        JsonValidationApiError(jsError).toResult

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

  Feature("405, METHOD_NOT_ALLOWED case scenarios") {

    val methodsToTest = Seq("GET", "DELETE", "HEAD", "OPTIONS", "PATCH", "PUT")

    methodsToTest.foreach { method =>
      Scenario(s"405, $method") {
        Given("Valid bearer token")

        When(s"a $method authorisations request to uknw-auth-checker-api with bearer token")
        val response = checkerApiService.authorisations405(method, authBearerToken)

        val expectedResponse = MethodNotAllowedApiError.toResult

        Then("I am returned a status code 405")
        if (method == "HEAD") {
          response.status shouldBe 405
        } else {
          response hasStatusAndBody expectedResponse
        }
      }
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

  Feature("500, INTERNAL_SERVER_ERROR case Scenarios") { // TODO: A different way to generate a 500 from the API/Service?
    Scenario("500, internal server error") {
      Given("Valid bearer token")
      And("a invalid payload with massive string size of EORIs")
      val eoris   = useUnrestrictedGarbageGenerator(authorisedEoris.size)
      val request = createRequest(localNow, eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      val expectedResponse =
        InternalServerApiError.toResult

      Then("I am returned a status code 500")
      response hasStatusAndBody expectedResponse
    }
  }
}
