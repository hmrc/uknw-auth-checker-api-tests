import sbt.*

object Dependencies {
  private val bootstrapVersion = "9.11.0"

  val test: Seq[ModuleID]      = Seq(
    "org.commonmark"        % "commonmark"              % "0.24.0"         % Test,
    "com.typesafe"          % "config"                  % "1.4.3"          % Test,
    "org.playframework"    %% "play-ahc-ws-standalone"  % "3.0.7"          % Test,
    "org.playframework"    %% "play-ws-standalone-json" % "3.0.7"          % Test,
    "com.vladsch.flexmark"  % "flexmark-all"            % "0.64.8"         % Test,
    "org.scalatest"        %% "scalatest"               % "3.2.19"         % Test,
    "org.slf4j"             % "slf4j-simple"            % "2.0.17"         % Test,
    "io.github.wolfendale" %% "scalacheck-gen-regexp"   % "1.1.0"          % Test,
    "org.scalacheck"       %% "scalacheck"              % "1.18.1"         % Test,
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapVersion % Test
  )

}
