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
import uk.gov.hmrc.api.service.AuthService
import uk.gov.hmrc.api.utils.TestData

class AuthorisationsSpec extends BaseSpec with TestData {
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
      val request = createRequest(req200_single)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 200")
      comprehensivelyAssert(response, 200, expetedRes200_single)
    }

    Scenario("Happy path with multiple EORIs - 200 OK") {
      Given("a bearer token")
      And("a valid payload")
      val request = createRequest(req200_multiple)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 200")
      comprehensivelyAssert(response, 200, expectedRes200_multiple)
    }
  }

  Feature("400, BAD_REQUEST case scenarios") {

    Scenario("400 Bad Eori") {
      Given("a bearer token")

      And("an invalid payload")
      val request  = createRequest(req400_singleEori)
      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 400")
      comprehensivelyAssert(response, 400, expectedRes400_singleEori)
    }

    Scenario("400 Bad Eoris") {
      Given("a bearer token")
      And("an invalid payload")
      val request = createRequest(req400_multipleEori)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 400")
      comprehensivelyAssert(response, 400, expectedRes400_multipleEori)
    }

    Scenario("400 Not enough EORIS (0)") {
      Given("a bearer token")
      And("an invalid payload")
      val request = createRequest(req400_noEoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 400")
      comprehensivelyAssert(response, 400, expectedRes400_wrongNumberOfEoris)
    }

    Scenario("400 Too many EORIS (3001)") {
      Given("a bearer token")
      And("an invalid payload")
      val request = createRequest(req400_tooManyEoris)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 400")
      response.status shouldBe 400
      response.body   shouldBe expectedRes400_wrongNumberOfEoris
    }
  }

  Feature("401, UNAUTHORIZED case scenarios") {

    Scenario("Invalid Bearer Token") {
      Given("an invalid bearer token")
      val anInvalidToken: String = "Invalid Token"
      And("a valid payload")
      val request                = createRequest(req200_multiple)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), anInvalidToken)

      Then("I am returned a status code 401")
      response.status shouldBe 401
      response.body   shouldBe expectedRes401_unauthorized
    }

    Scenario("Missing Bearer Token") {
      Given("There's no bearer token")
      And("a valid payload")
      val request = createRequest(req200_multiple)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request))

      Then("I am returned a status code 401")
      response.status shouldBe 401
      response.body   shouldBe expectedRes401_unauthorized
    }

  }

  Feature("403, FORBIDDEN case Scenarios") {

    Scenario("Forcing a 403 response") {
      Given("Valid bearer token")
      And("a forbidden payload")
      val request = createRequest(req403_single)

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken)

      Then("I am returned a status code 403")
      comprehensivelyAssert(response, 403, expectedRes403_forbidden)
    }

  }

  Feature("405, METHOD_NOT_ALLOWED case scenarios") {

    Scenario("405, GET") {
      Given("Valid bearer token")

      When("a GET authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_get(authBearerToken)

      Then("I am returned a status code 403")
      response.status shouldBe 405
      response.body   shouldBe expectedRes405_notAllowed
    }

    Scenario("405, DELETE") {
      Given("Valid bearer token")

      When("a DELETE authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_delete(authBearerToken)

      Then("I am returned a status code 405")
      response.status shouldBe 405
      response.body   shouldBe expectedRes405_notAllowed
    }

    Scenario("405, HEAD") {
      Given("Valid bearer token")

      When("a HEAD authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_head(authBearerToken)

      Then("I am returned a status code 405")
      response.status shouldBe 405
    }

    Scenario("405, OPTION") {
      Given("Valid bearer token")

      When("a OPTION authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_option(authBearerToken)

      Then("I am returned a status code 405")
      response.status shouldBe 405
      response.body   shouldBe expectedRes405_notAllowed
    }

    Scenario("405, PATCH") {
      Given("Valid bearer token")

      When("a PATCH authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_patch(authBearerToken)

      Then("I am returned a status code 405")
      response.status shouldBe 405
      response.body   shouldBe expectedRes405_notAllowed
    }

    Scenario("405, PUT") {
      Given("Valid bearer token")
      When("a PUT authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations405_put(authBearerToken)

      Then("I am returned a status code 405")
      response.status shouldBe 405
      response.body   shouldBe expectedRes405_notAllowed
    }

  }

  Feature("406, NOT_ACCEPTABLE case scenarios") {

    Scenario("406 Accept") {
      Given("Valid bearer token")
      And("a valid payload")
      val request = createRequest(req200_single)

      When("a PUT authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken, acceptInput = "invalid")

      Then("I am returned a status code 405")
      response.status shouldBe 406
      response.body   shouldBe expectedRes406
    }
  }

  Feature("500, INTERNAL_SERVER_ERROR case Scenarios") {
    Scenario("500, wrong content type") {
      Given("Valid bearer token")
      And("a valid payload")
      val request = createRequest(req200_single)

      When("a PUT authorisations request to uknw-auth-checker-api with bearer token")
      val response = checkerApiService.authorisations(Json.toJson(request), authBearerToken, "invalid")

      Then("I am returned a status code 405")
      response.status shouldBe 500
      response.body   shouldBe expectedRes500
    }
  }

}
