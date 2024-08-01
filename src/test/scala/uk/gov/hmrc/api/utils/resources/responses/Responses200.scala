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

package uk.gov.hmrc.api.utils.resources.responses

import play.api.libs.json.{JsValue, Json}

trait Responses200 {
  val expetedRes200_single: JsValue =
    Json.parse("""{"date":"{{dateTime}}","eoris":[{"eori":"GB000000000200","authorised":true}]}""".mkString)

  val expectedRes200_multiple: JsValue = Json.parse(
    """{"date":"{{dateTime}}","eoris":[{"eori":"GB000000000200","authorised":true},{"eori":"XI000000000200","authorised":true}]}""".mkString
  )

}
