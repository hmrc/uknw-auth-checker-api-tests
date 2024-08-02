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

import com.typesafe.config.{Config, ConfigFactory}

object TestConfiguration {

  private val servicesHost: String = "services.host"
  private val environment: String  = "environment"
  private val local: String        = "local"

  private val config: Config          = ConfigFactory.load()
  private val env: String             = config.getString(environment)
  private val defaultConfig: Config   = config.getConfig(local)
  private val envConfig: Config       = config.getConfig(env).withFallback(defaultConfig)
  private val environmentHost: String = envConfig.getString(servicesHost)

  def url(service: String): String = {
    val host = env match {
      case "local" => s"$environmentHost:${servicePort(service)}"
      case _       => s"${envConfig.getString(servicesHost)}"
    }
    s"$host${serviceRoute(service)}"
  }

  private def servicePort(serviceName: String): String = envConfig.getString(s"services.$serviceName.port")

  private def serviceRoute(serviceName: String): String = envConfig.getString(s"services.$serviceName.productionRoute")

}