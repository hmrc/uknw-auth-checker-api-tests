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

package uk.gov.hmrc.api.client

import org.apache.pekko.actor.ActorSystem
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.DefaultBodyWritables.*
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{StandaloneWSRequest, StandaloneWSResponse}
import uk.gov.hmrc.api.conf.ZapConfiguration.*

import scala.concurrent.{ExecutionContext, Future}

trait HttpClient {
  implicit lazy val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext          = ExecutionContext.global

  val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()
  val dummyJson: JsValue              = Json.obj("data" -> "garbage")

  private def defaultRequest(url: String, headers: (String, String)*): StandaloneWSRequest = {
    val request = wsClient
      .url(url)
      .withHttpHeaders(headers*)
    if (isEnabled) request.withProxyServer(proxyServer) else request
  }

  def post(url: String, bodyAsJson: String, headers: (String, String)*): Future[StandaloneWSResponse] =
    defaultRequest(url, headers*)
      .post(bodyAsJson)
}
