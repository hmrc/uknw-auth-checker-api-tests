package uk.gov.hmrc.api.specs.payloads

import play.api.libs.json.JsValue
import uk.gov.hmrc.api.utils.JsonReader

trait RequestPayloads extends JsonReader {

  val req200_multiple: JsValue         = getJsonFile("/requests/authRequest200_multiple.json")
  val req200_single: JsValue           = getJsonFile("/requests/authRequest200_single.json")
  val req400_invalidField: JsValue     = getJsonFile("/requests/authRequest400_invalidField.json")
  val req400_missingEoriField: JsValue = getJsonFile("/requests/authRequest400_missingEoriField.json")
  val req400_multipleEori: JsValue     = getJsonFile("/requests/authRequest400_multipleEori.json")
  val req400_noEoris: JsValue          = getJsonFile("/requests/authRequest400_noEoris.json")
  val req400_singleEori: JsValue       = getJsonFile("/requests/authRequest400_singleEori.json")
  val req403_single: JsValue           = getJsonFile("/requests/authRequest403_single.json")
  val req400_tooManyEoris: JsValue     = getJsonFile("/requests/authRequest400_tooManyEoris.json")
}
