/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.api.service

import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.client.HttpClient
import uk.gov.hmrc.api.conf.TestConfiguration

import scala.concurrent.Await
import scala.concurrent.duration._

class AuthService extends HttpClient {
  val host: String = TestConfiguration.url("auth")

  val authPayload: String =
    s"""
       |{
       |  "clientId": "id-123232",
       |  "authProvider": "PrivilegedApplication",
       |  "applicationId":"app-1",
       |  "applicationName": "App 1",
       |  "enrolments": ["read:individuals-matching",
       |  "read:individuals-income",
       |  "read:individuals-income-sa",
       |  "read:individuals-income-sa-additional-information",
       |  "read:individuals-income-sa-employments",
       |  "read:individuals-income-sa-foreign",
       |  "read:individuals-income-sa-interests-and-dividends",
       |  "read:individuals-income-sa-partnerships",
       |  "read:individuals-income-sa-pensions-and-state-benefits",
       |  "read:individuals-income-sa-other",
       |  "read:individuals-income-sa-self-employments",
       |  "read:individuals-income-sa-source",
       |  "read:individuals-income-sa-summary",
       |  "read:individuals-income-sa-trusts",
       |  "read:individuals-income-sa-uk-properties",
       |  "read:individuals-income-paye",
       |  "read:individuals-employments",
       |  "read:individuals-employments-paye"],
       |  "ttl": 5000
       |}
     """.stripMargin

  def postLogin: StandaloneWSRequest#Self#Response = {
    val url = s"$host/application/session/login"
    Await.result(
      post(url, authPayload, ("Content-Type", "application/json")),
      10.seconds
    )
  }

}
