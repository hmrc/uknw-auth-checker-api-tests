/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.api.helpers

import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.service.UknwAuthCheckerApiService

class UknwAuthCheckerApiHelper {

  val uknwAuthCheckerApiService: UknwAuthCheckerApiService = new UknwAuthCheckerApiService

  def postAuthorisations(authBearerToken: String): Int = {
    val response: StandaloneWSRequest#Self#Response =
      uknwAuthCheckerApiService.authorisations(authBearerToken)
    response.status
  }

}
