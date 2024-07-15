/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.api.helpers

import org.scalatest.Assertions.fail
import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.service.AuthService

class AuthHelper {

  val authAPI: AuthService = new AuthService

  def getAuthBearerToken: String = {
    val authServiceRequestResponse: StandaloneWSRequest#Self#Response = authAPI.postLogin
    authServiceRequestResponse
      .header("Authorization")
      .getOrElse(fail(s"Could not obtain auth bearer token. Auth Service Response: $authServiceRequestResponse"))
  }

}
