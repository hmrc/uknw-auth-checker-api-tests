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

package uk.gov.hmrc.api.specs

import org.scalatest.Assertion
import play.api.libs.json.JsValue
import uk.gov.hmrc.api.models.*
import uk.gov.hmrc.api.models.ApiErrorDetails.*
import uk.gov.hmrc.api.models.ApiErrorResponse.*
import uk.gov.hmrc.api.models.AuthorisationRequest.{toInvalidJsObject, toInvalidJsonStructure}
import uk.gov.hmrc.api.models.constants.ApiErrorMessages
import uk.gov.hmrc.api.models.constants.ApiErrorMessages.*
import uk.gov.hmrc.api.utils.AuthorisationsSpecHelper

class AuthorisationsSpec extends AuthorisationsSpecHelper {

  Feature("Is bearer token valid") {
    Scenario("Checking bearer token") {
      When("Getting bearer token")
      Then("Said Bearer Token shouldn't contain an error")
      authBearerToken shouldNot contain("Could not obtain auth bearer token. Auth Service Response:")
    }
  }

  Feature("Authorised EORI Scenarios - 200 OK") {
    Scenario("Single authorised EORI") {
      val eoris            = useEoriGenerator(1)
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }

    Scenario("Multiple authorised EORIs") {
      val eoris            = useEoriGenerator(fetchRandomNumber(2, authorisedEoris.size))
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }
  }

  Feature("Unauthorised EORI Scenarios - 200 OK") {
    Scenario("Single unauthorised EORI") {
      val eoris            = useEoriGenerator(1, Some(0))
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }

    Scenario("Multiple unauthorised EORIs") {
      Given("a bearer token")
      val eoris            = useEoriGenerator(2, Some(1))
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }
  }

  Feature("Mixed EORI Scenarios - 200 OK") {
    Scenario("Single authorised and unauthorised EORIs") {
      val eoris            = useEoriGenerator(1, Some(0))
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }

    Scenario("Multiple authorised and unauthorised EORIs") {
      Given("a bearer token")
      val eoris            = useEoriGenerator(authorisedEoris.size, Some(authorisedEoris.size / 2))
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }
  }

  Feature("Duplicate EORI Scenarios - 200 OK") {
    Scenario("Two duplicate authorised EORIs") {
      val eori             = useEoriGenerator(1).head
      val eoris            = Seq(eori, eori)
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris.distinct, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }

    Scenario("Two duplicate unauthorised EORIs") {
      val eori             = useEoriGenerator(1, Some(0)).head
      val eoris            = Seq(eori, eori)
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris.distinct, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }

    Scenario("Two duplicate authorised and unauthorised EORIs") {
      val unauthorisedEori = useEoriGenerator(1, Some(0)).head
      val authorisedEori   = useEoriGenerator(1).head
      val eoris            = Seq(unauthorisedEori, unauthorisedEori, authorisedEori, authorisedEori)
      val request          = AuthorisationRequest(eoris)
      val expectedResponse = generateExpectedOkResponse(eoris.distinct, authorised = true)
      postAndAssertOk(request, expectedResponse)
    }
  }

  Feature("Invalid EORI Scenarios - 400 Bad Request") {
    Scenario("Single invalid EORI") {
      val eoris  = useGarbageGenerator(1)
      val errors = generateInvalidEoriErrors(eoris)
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }

    Scenario("Multiple invalid EORIs") {
      val eoris  = useGarbageGenerator(authorisedEoris.size)
      val errors = generateInvalidEoriErrors(eoris)
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Mixed Invalid EORI Scenarios - 400 Bad Request") {
    Scenario("Multiple invalid EORIs with some valid authorised EORIs") {
      val invalidEoris    = useGarbageGenerator(fetchRandomNumber(1, 10))
      val authorisedEoris = useEoriGenerator(fetchRandomNumber(1, 10))
      val eoris           = invalidEoris ++ authorisedEoris
      val errors          = generateInvalidEoriErrors(invalidEoris)
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }

    Scenario("Multiple invalid EORIs with some valid unauthorised EORIs") {
      val invalidEoris      = useGarbageGenerator(fetchRandomNumber(1, 10))
      val unauthorisedEoris = useEoriGenerator(fetchRandomNumber(1, 10), Some(0))
      val eoris             = invalidEoris ++ unauthorisedEoris
      val errors            = generateInvalidEoriErrors(invalidEoris)
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }

    Scenario("Multiple invalid EORIs with some valid authorised and unauthorised EORIs") {
      val invalidEoris      = useGarbageGenerator(fetchRandomNumber(1, 10))
      val authorisedEoris   = useEoriGenerator(fetchRandomNumber(1, 10))
      val unauthorisedEoris = useEoriGenerator(fetchRandomNumber(1, 10), Some(0))
      val eoris             = invalidEoris ++ authorisedEoris ++ unauthorisedEoris
      val errors            = generateInvalidEoriErrors(invalidEoris)
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Duplicate Invalid EORI Scenarios - 400 Bad Request") {
    Scenario("Duplicate invalid EORI") {
      val eori   = useGarbageGenerator(1).head
      val eoris  = Seq(eori, eori)
      val errors = generateInvalidEoriErrors(eoris.distinct)
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Invalid amount of EORIs Scenarios - 400 Bad Request") {
    Scenario("Not enough EORIS (0)") {
      val eoris  = useEoriGenerator(authorisedEoris.size + 10, Some(authorisedEoris.size))
      val errors = InvalidEoriCountApiError.toAPIErrorResponse
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }

    Scenario("Too many EORIS (3001)") {
      val eoris  = useEoriGenerator(authorisedEoris.size + 10, Some(authorisedEoris.size))
      val errors = InvalidEoriCountApiError.toAPIErrorResponse
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Invalid JSON Structure Scenarios - 400 Bad Request") {
    Scenario("JsObject passed instead of JsArray") {
      val eoris                               = useEoriGenerator(1)
      val invalidJsObjectAuthorisationRequest = toInvalidJsObject(AuthorisationRequest(eoris))
      val errors                              = JsObjectInvalidApiError.toAPIErrorResponse
      postAndAssertBadRequest(
        eoris,
        errors,
        Some(invalidJsObjectAuthorisationRequest)
      )
    }

    Scenario("JSON structure is incorrect") {
      val eoris                                    = useEoriGenerator(1)
      val invalidJsonStructureAuthorisationRequest = toInvalidJsonStructure(AuthorisationRequest(eoris))
      val errors                                   = JSONStructureInvalidApiError.toAPIErrorResponse
      postAndAssertBadRequest(
        eoris,
        errors,
        Some(invalidJsonStructureAuthorisationRequest)
      )
    }
  }

  Feature("Bearer Token Invalid Scenarios - 401 Forbidden") {
    Scenario("Invalid Bearer Token") {
      val eoris                      = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val invalidBearerToken: String = "Invalid Token"
      val errors                     = UnauthorizedApiError(ApiErrorMessages.unauthorized)

      postAndAssertBadRequest(
        eoris,
        errors,
        bearerToken = Some(invalidBearerToken)
      )
    }

    Scenario("Empty Bearer Token") {
      val eoris                      = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val invalidBearerToken: String = ""
      val errors                     = UnauthorizedApiError(ApiErrorMessages.unauthorized)

      postAndAssertBadRequest(
        eoris,
        errors,
        bearerToken = Some(invalidBearerToken)
      )
    }

    Scenario("Missing Bearer Token") {
      val eoris  = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val errors = UnauthorizedApiError(ApiErrorMessages.unauthorized)

      postAndAssertBadRequest(
        eoris,
        errors,
        bearerToken = None
      )
    }
  }

  Feature("Forbidden Scenarios - 403 Forbidden") {
    Scenario("A valid non authorised request") {
      val eoris  = Seq(reservedEoris(403))
      val errors = ForbiddenApiError
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Invalid headers - 406 Not Acceptable") {
    Scenario("Invalid Accept Header") {
      val eoris  = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val errors = NotAcceptableApiError

      postAndAssertBadRequest(
        eoris,
        errors,
        acceptInputHeader = "Invalid"
      )
    }

    Scenario("Invalid Content Type Header") {
      Given("Valid bearer token")
      val eoris  = useEoriGenerator(fetchRandomNumber(1, authorisedEoris.size))
      val errors = NotAcceptableApiError

      postAndAssertBadRequest(
        eoris,
        errors,
        contentTypeHeader = "Invalid"
      )
    }
  }

  Feature("Request Too Large Scenarios - REQUEST_ENTITY_TOO_LARGE 413") {
    Scenario("Invalid request with a massive string size of EORIs causing the request to be over 100KB") {
      val eoris  = useUnrestrictedGarbageGenerator(authorisedEoris.size)
      val errors = RequestEntityTooLargeError
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Internal Server Error Scenarios - 500 Internal Server Error") {
    Scenario("A valid request and something goes wrong on the server") {
      val eoris  = Seq(reservedEoris(500))
      val errors = InternalServerApiError
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }

  Feature("Service Unavailable Scenarios - 503 Service Unavailable") {
    Scenario("A valid request and the server is unavailable") {
      val eoris  = Seq(reservedEoris(503))
      val errors = ServiceUnavailableApiError
      postAndAssertBadRequest(
        eoris,
        errors
      )
    }
  }
}
