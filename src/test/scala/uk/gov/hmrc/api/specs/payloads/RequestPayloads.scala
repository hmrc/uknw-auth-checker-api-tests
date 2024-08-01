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

package uk.gov.hmrc.api.specs.payloads

import play.api.libs.json.JsValue
import uk.gov.hmrc.api.utils.JsonReader

trait RequestPayloads extends JsonReader {

  val req200_multiple: JsValue         = getJsonFile("/requests/authRequest200_multiple.json")
  val req200_single: JsValue           = getJsonFile("/requests/authRequest200_single.json")
  val req400_missingEoriField: JsValue = getJsonFile("/requests/authRequest400_missingEoriField.json")
  val req400_multipleEori: JsValue     = getJsonFile("/requests/authRequest400_multipleEori.json")
  val req400_noEoris: JsValue          = getJsonFile("/requests/authRequest400_noEoris.json")
  val req400_singleEori: JsValue       = getJsonFile("/requests/authRequest400_singleEori.json")
  val req403_single: JsValue           = getJsonFile("/requests/authRequest403_single.json")
  val req400_tooManyEoris: JsValue     = getJsonFile("/requests/authRequest400_tooManyEoris.json")
  val req500: JsValue                  = getJsonFile("/requests/authRequest500.json")
}
