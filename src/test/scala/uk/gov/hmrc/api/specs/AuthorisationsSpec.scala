/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.api.specs

class AuthorisationsSpec extends BaseSpec {

  Feature("Example of creating bearer token") {

    Scenario("Happy path - 200 OK") {
      Given("a bearer token")
      val authBearerToken: String = authHelper.getAuthBearerToken

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val responseStatus = authCheckerApiHelper.postAuthorisations(authBearerToken)

      Then("I am returned a status code 200")
      responseStatus shouldBe 200
    }
  }

}
