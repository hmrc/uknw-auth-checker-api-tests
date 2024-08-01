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

import akka.actor.ActorSystem
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import uk.gov.hmrc.api.conf.ZapConfiguration._

import scala.concurrent.{ExecutionContext, Future}

trait HttpClient {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext     = ExecutionContext.global

  private val wsClient: StandaloneAhcWSClient = StandaloneAhcWSClient()
  private val dummyJson: JsValue              = Json.toJson("""{"data": "garbage" }""")

  private def defaultRequest(url: String, headers: (String, String)*): StandaloneWSRequest#Self = {
    val request = wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
    if (isEnabled) {
      request.withProxyServer(proxyServer)
    } else {
      request
    }
  }

  def get(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .get()

  def post(url: String, bodyAsJson: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .post(bodyAsJson)

  def delete(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .delete()

  def head(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .head()

  def option(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .options()

  def patch(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .patch(dummyJson)

  def put(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    defaultRequest(url, headers: _*)
      .put(dummyJson)

}
