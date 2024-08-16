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

sealed trait Errors {
  def code: String

  def message: String
}

object Errors {

  case class InvalidEoriFormat(eori: String) extends Errors {
    override val code: String = "INVALID_FORMAT"
    override val message: String = s"$eori is not a supported EORI number"
  }

  case object MissingEoriField extends Errors {
    override val code: String = "INVALID_FORMAT"
    override val message: String = "eoris field missing from JSON"
  }

  case object WrongNumberOfEoris extends Errors {
    override val code: String = "INVALID_FORMAT"
    override val message: String = "The request payload must contain between 1 and 3000 EORI entries"
  }

  implicit val invalidEoriFormatFormat: OFormat[InvalidEoriFormat] = Json.format[InvalidEoriFormat]
  implicit val missingEoriFieldFormat: OFormat[MissingEoriField.type] = Json.format[MissingEoriField.type]
  implicit val wrongNumberOfEorisFormat: OFormat[WrongNumberOfEoris.type] = Json.format[WrongNumberOfEoris.type]

  implicit val writes: Writes[Errors] = {
    case e: InvalidEoriFormat => Json.toJson(e)
    case MissingEoriField => Json.toJson(MissingEoriField)
    case WrongNumberOfEoris => Json.toJson(WrongNumberOfEoris)
  }

  implicit val reads: Reads[Errors] = (json: JsValue) => {
    (json \ "code").as[String] match {
      case "INVALID_FORMAT" =>
        (json \ "message").as[String] match {
          case msg if msg.contains("EORI number") => json.validate[InvalidEoriFormat]
          case "eoris field missing from JSON" => JsSuccess(MissingEoriField)
          case "The request payload must contain between 1 and 3000 EORI entries" => JsSuccess(WrongNumberOfEoris)
          case _ => JsError("Unknown error type for INVALID_FORMAT")
        }
      case _ => JsError("Unknown error code")
    }
  }
}

