package uk.gov.hmrc.api.service

import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.client.HttpClient
import uk.gov.hmrc.api.conf.TestConfiguration
import uk.gov.hmrc.api.utils.JsonGetter.getJsonFile

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class UknwAuthCheckerApiService extends HttpClient {

  val authorisationsUrl: String = TestConfiguration.url("uknw-auth-checker-api")

  def authorisations(authToken: String): StandaloneWSRequest#Self#Response = {
    val individualPayload = getJsonFile("authRequest200_single.json")
    Await.result(
      post(
        authorisationsUrl,
        Json.stringify(individualPayload),
        ("Content-Type", "application/json"),
        ("Authorization", authToken),
        ("Accept", "application/vnd.hmrc.1.0+json")
      ),
      10.seconds
    )
  }

}
