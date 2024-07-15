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

import uk.gov.hmrc.api.utils.JsonGetter.getJsonFile

class AuthorisationsSpec extends BaseSpec {

  Feature("Example of creating bearer token") {

    Scenario("Happy path - 200 OK") {
      Given("a bearer token")
      val authBearerToken: String = authHelper.getAuthBearerToken
      And("and payload")
      val individualPayload       = getJsonFile("authRequest200_single.json")

      When("post a authorisations request to uknw-auth-checker-api with bearer token")
      val responseStatus = authCheckerApiHelper.postAuthorisations(authBearerToken, individualPayload)

      Then("I am returned a status code 200")
      responseStatus shouldBe 200
    }
  }

}
