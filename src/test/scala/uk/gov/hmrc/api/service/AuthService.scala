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

import org.scalatest.Assertions.fail
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.client.HttpClient
import uk.gov.hmrc.api.conf.TestConfiguration

import scala.concurrent.Await
import scala.concurrent.duration._

class AuthService extends HttpClient {
  val host: String = TestConfiguration.url("auth")

  val authPayload: String =
    s"""
       |{
       |  "clientId": "uknw-auth-checker-api",
       |  "authProvider": "StandardApplication",
       |  "applicationId":"uknw-auth-checker-api",
       |  "applicationName": "uknw-auth-checker-api",
       |  "enrolments": [],
       |  "ttl": 5000
       |}
     """.stripMargin

  def postLogin: StandaloneWSResponse = {
    val url = s"$host/application/session/login"
    Await.result(
      post(url, authPayload, ("Content-Type", "application/json")),
      10.seconds
    )
  }

  def getAuthBearerToken: String = {
    val authServiceRequestResponse: StandaloneWSResponse = postLogin
    authServiceRequestResponse
      .header("Authorization")
      .getOrElse(fail(s"Could not obtain auth bearer token. Auth Service Response: $authServiceRequestResponse"))
  }

}
