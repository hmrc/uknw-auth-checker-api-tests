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

package uk.gov.hmrc.api.models

import play.api.libs.json.*

case class AuthorisationRequest(eoris: Seq[String])

object AuthorisationRequest {
  implicit val format: OFormat[AuthorisationRequest] = Json.format[AuthorisationRequest]

  def toInvalidJsonStructure(request: AuthorisationRequest): JsValue =
    Json.toJson(Json.stringify(Json.toJson(request)(using format)).replaceAll("\"eoris\"", "eoris"))

  def toInvalidJsObject(request: AuthorisationRequest): JsValue = Json.toJson(Json.obj("eoris" -> request.eoris.head))
}
