import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "com.typesafe"         % "config"                  % "1.4.3"  % Test,
    "org.playframework"   %% "play-ahc-ws-standalone"  % "3.1.0-M2"  % Test,
    "org.playframework"   %% "play-ws-standalone-json" % "3.1.0-M2"  % Test,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.64.8" % Test,
    "org.scalatest"       %% "scalatest"               % "3.3.0-SNAP4" % Test,
    "org.slf4j"            % "slf4j-simple"            % "2.0.15" % Test
  )

}
