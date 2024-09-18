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

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.client.HttpClient
import uk.gov.hmrc.api.conf.TestConfiguration

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class UknwAuthCheckerApiService extends HttpClient {

  private val authorisationsUrl: String = TestConfiguration.url("uknw-auth-checker-api")

  def authorisations(
    individualPayload: JsValue,
    authToken: Option[String],
    acceptInput: String,
    contentType: String
  ): StandaloneWSResponse = {

    val headers = Seq(
      "Accept"       -> acceptInput,
      "Content-Type" -> contentType
    ) ++ authToken.map(bearerToken => "Authorization" -> bearerToken)

    awaitRequest(
      authorisationsUrl,
      headers,
      individualPayload
    )
  }

  private def awaitRequest(
    url: String,
    headers: Seq[(String, String)] = Seq.empty,
    body: JsValue = Json.obj()
  ): StandaloneWSResponse = Await.result(post(url, body.toString, headers*), 10.seconds)
}
