import sbt.*

object Dependencies {
  private val bootstrapVersion = "9.18.0"

  val test: Seq[ModuleID] = Seq(
    "org.commonmark"        % "commonmark"             % "0.25.0"         % Test,
    "uk.gov.hmrc"          %% "api-test-runner"        % "0.10.0"         % Test,
    "io.github.wolfendale" %% "scalacheck-gen-regexp"  % "1.1.0"          % Test,
    "org.scalacheck"       %% "scalacheck"             % "1.18.1"         % Test,
    "uk.gov.hmrc"          %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )

}
