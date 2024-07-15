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

package uk.gov.hmrc.api.service

import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.client.HttpClient
import uk.gov.hmrc.api.conf.TestConfiguration
import uk.gov.hmrc.api.utils.JsonGetter.getJsonFile

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class UknwAuthCheckerApiService extends HttpClient {

  val authorisationsUrl: String = TestConfiguration.url("uknw-auth-checker-api")

  def authorisations(authToken: String): StandaloneWSRequest#Self#Response = {
    val individualPayload = getJsonFile("authRequest200_single.json")
    Await.result(
      post(
        authorisationsUrl,
        Json.stringify(individualPayload),
        ("Content-Type", "application/json"),
        ("Authorization", authToken),
        ("Accept", "application/vnd.hmrc.1.0+json")
      ),
      10.seconds
    )
  }

}
