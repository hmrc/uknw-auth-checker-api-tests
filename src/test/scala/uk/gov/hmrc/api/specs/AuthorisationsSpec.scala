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

import play.api.libs.json.Json
import uk.gov.hmrc.api.models.{EisErrorsResponse, Errors, StatusAndMessage}
import uk.gov.hmrc.api.service.AuthService
import uk.gov.hmrc.api.utils.{EoriGenerator, TestData}

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

      val eoris   = useEoriGenerator(1, Some(1))
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations(Json.toJson(request), authBearerToken)
      val expectedResponse = createResponse(zonedNow, eoris, 200)

      Then("I am returned a status code 200")
      response hasStatusAndBody expectedResponse
    }

    Scenario("Happy path with multiple EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val eoris   = useEoriGenerator(fetchRandomNumber(2, authorisedEoris.size))
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations(Json.toJson(request), authBearerToken)
      val expectedResponse = createResponse(zonedNow, eoris, 200)

      Then("I am returned a status code 200")
      response hasStatusAndBody expectedResponse
    }
  }

  Feature("400, BAD_REQUEST case scenarios") {

    Scenario("400 Bad Eori") {
      Given("a bearer token")

      And("an invalid payload")
      val eoris   = useGarbageGenerator(1)
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      val invalidFormatErrors = eoris.map(eori => Errors.InvalidEoriFormat(eori))

      val expectedResponse = EisErrorsResponse(StatusAndMessage.BadRequest, Some(invalidFormatErrors))

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Bad Eoris") {
      Given("a bearer token")
      And("an invalid payload")

      val eoris   = useGarbageGenerator(
        authorisedEoris.size
      ) // TODO Note: Had to limit the garbage generator as it was causing the test to fail when the strings were too long causing a 500, is this something we need to look into?.
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      val invalidFormatErrors = eoris.map(eori => Errors.InvalidEoriFormat(eori))

      val expectedResponse = EisErrorsResponse(StatusAndMessage.BadRequest, Some(invalidFormatErrors))

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Not enough EORIS (0)") {
      Given("a bearer token")
      And("an invalid payload")
      val request = createRequest(localNow, Seq.empty)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations(Json.toJson(request), authBearerToken)
      val expectedResponse = EisErrorsResponse(StatusAndMessage.BadRequest, Some(Seq(Errors.WrongNumberOfEoris)))

      Then("I am returned a status code 400")
      response hasStatusAndBody expectedResponse
    }

    Scenario("400 Too many EORIS (3001)") {
      Given("a bearer token")
      And("an invalid payload")
      val eoris   = useEoriGenerator(authorisedEoris.size + 10, Some(authorisedEoris.size))
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations(Json.toJson(request), authBearerToken)
      val expectedResponse = EisErrorsResponse(StatusAndMessage.BadRequest, Some(Seq(Errors.WrongNumberOfEoris)))

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
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), anInvalidToken)

      val expectedResponse = EisErrorsResponse(StatusAndMessage.Unauthorized, None)

      Then("I am returned a status code 401")
      response hasStatusAndBody expectedResponse

    }

    Scenario("Missing Bearer Token") {
      Given("There's no bearer token")
      And("a valid payload")

      val eoris   = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val request = createRequest(localNow, eoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request))

      val expectedResponse = EisErrorsResponse(StatusAndMessage.Unauthorized, None)

      Then("I am returned a status code 401")
      response hasStatusAndBody expectedResponse
    }

  }

  Feature("405, METHOD_NOT_ALLOWED case scenarios") {

    Scenario("405, GET") {
      Given("Valid bearer token")

      When("a GET authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations405_get(authBearerToken)
      val expectedResponse = EisErrorsResponse(StatusAndMessage.MethodNotAllowed, None)

      Then("I am returned a status code 403")
      response hasStatusAndBody expectedResponse
    }

    Scenario("405, DELETE") {
      Given("Valid bearer token")

      When("a DELETE authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations405_delete(authBearerToken)
      val expectedResponse = EisErrorsResponse(StatusAndMessage.MethodNotAllowed, None)

      Then("I am returned a status code 405")
      response hasStatusAndBody expectedResponse
    }

    Scenario("405, HEAD") {
      Given("Valid bearer token")

      When("a HEAD authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_head(authBearerToken)

      Then("I am returned a status code 405")
      response.isMethodNotAllowed
    }

    Scenario("405, OPTION") {
      Given("Valid bearer token")

      When("a OPTION authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations405_option(authBearerToken)
      val expectedResponse = EisErrorsResponse(StatusAndMessage.MethodNotAllowed, None)

      Then("I am returned a status code 405")
      response hasStatusAndBody expectedResponse
    }

    Scenario("405, PATCH") {
      Given("Valid bearer token")

      When("a PATCH authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_patch(authBearerToken)

      val expectedResponse = EisErrorsResponse(StatusAndMessage.MethodNotAllowed, None)

      Then("I am returned a status code 405")
      response hasStatusAndBody expectedResponse
    }

    Scenario("405, PUT") {
      Given("Valid bearer token")
      When("a PUT authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_put(authBearerToken)

      val expectedResponse = EisErrorsResponse(StatusAndMessage.MethodNotAllowed, None)

      Then("I am returned a status code 405")
      response hasStatusAndBody expectedResponse
    }

  }

  Feature("406, NOT_ACCEPTABLE case scenarios") {

    Scenario("406 invalid Accept") {
      Given("Valid bearer token")
      And("a valid payload")
      val eoris   = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val request = createRequest(localNow, eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken, acceptInput = "invalid")

      val expectedResponse = EisErrorsResponse(StatusAndMessage.NotAcceptable, None)

      Then("I am returned a status code 406")
      response hasStatusAndBody expectedResponse
    }

    Scenario("406 invalid Content-Type") {
      Given("Valid bearer token")
      And("a valid payload")
      val eoris   = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val request = createRequest(localNow, eoris)

      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
      val response         = checkerApiService.authorisations(Json.toJson(request), authBearerToken, contentType = "invalid")
      val expectedResponse = EisErrorsResponse(StatusAndMessage.NotAcceptable, None)

      Then("I am returned a status code 406")
      response hasStatusAndBody expectedResponse
    }
  }

//  Feature("500, INTERNAL_SERVER_ERROR case Scenarios") { // TODO: How do we trigger an internal server error in the stub/EIS?
//    Scenario("500, internal server error") {
//      Given("Valid bearer token")
//      And("a valid payload")
//      val eoris   = useEoriGenerator(fetchRandomNumber(2, authorisedEoris.size))
//      val request = createRequest(localNow, eoris)
//
//      When("a POST authorisations request to uknw-auth-checker-api with bearer token")
//      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)
//
//      val expectedResponse = EisErrorsResponse(StatusAndMessage.InternalServerError, None)
//
//      Then("I am returned a status code 500")
//      response hasStatusAndBody expectedResponse
//    }
//  }
}
