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

package uk.gov.hmrc.api.conf

import play.api.libs.ws.DefaultWSProxyServer

object ZapConfiguration {
  private val host: String = "localhost"

  val isEnabled: Boolean                = System.getProperty("zap.proxy", "false").toBoolean
  val proxyPort: Int                    = System.getProperty("zap.proxyPort", "11000").toInt
  val proxyHost: String                 = host
  val proxyServer: DefaultWSProxyServer = DefaultWSProxyServer(
    host = proxyHost,
    port = proxyPort
  )
}
