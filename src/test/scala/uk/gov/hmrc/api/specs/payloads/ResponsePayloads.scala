package uk.gov.hmrc.api.specs.payloads

import uk.gov.hmrc.api.utils.JsonReader

trait ResponsePayloads extends JsonReader {

  val expectedRes200_single: String   = getJsonFile("/responses/authResponse200_single.json").toString
  val expectedRes200_multiple: String = getJsonFile("/responses/authResponse200_multiple.json").toString

  val expectedRes400_invalidForm: String        =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"date field missing from JSON","path":"date"}]}"""
  val expectedRes400_singleEori: String         =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"ABCD000000000200 is not a supported EORI number","path":"eoris"}]}"""
  val expectedRes400_multipleEori: String       =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"ABCD000000000200 is not a supported EORI number","path":"eoris"},{"code":"INVALID_FORMAT","message":"EFGH000000000200 is not a supported EORI number","path":"eoris"}]}"""
  val expectedRes400_missingEori: String        =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"eoris field missing from JSON","path":"eoris"}]}"""
  val expectedRes400_wrongNumberOfEoris: String =
    """{"code":"BAD_REQUEST","message":"Bad request","errors":[{"code":"INVALID_FORMAT","message":"The request payload must contain between 1 and 3000 EORI entries","path":"eoris"}]}"""

  val expectedRes401_invalid: String     = """{"code":"UNAUTHORIZED","message":"Invalid bearer token"}"""
  val expectedRes401_notSupplied: String = """{"code":"UNAUTHORIZED","message":"Bearer token not supplied"}"""

  val expectedRes403_forbidden: String  =
    """{"code":"FORBIDDEN","message":"You are not allowed to access this resource"}"""
  val expectedRes405_notAllowed: String = """{"code":"METHOD_NOT_ALLOWED","message":"This method is not supported"}"""
  val expectedRes406: String            =
    """{"code":"NOT_ACCEPTABLE","message":"Cannot produce an acceptable response. The Accept or Content-Type header is missing or invalid"}"""
  val expectedRes500: String            = """{"code":"INTERNAL_SERVER_ERROR","message":"Unexpected internal server error"}"""
}
