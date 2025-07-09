lazy val root = (project in file("."))
  .settings(
    name := "uknw-auth-checker-api-tests",
    version := "0.1.0",
    scalaVersion := "3.7.1",
    libraryDependencies ++= Dependencies.test
  )

Test / javaOptions += "-Dlogger.resource=logback-test.xml"
Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat

addCommandAlias("fmtAll", ";scalafmtSbt;scalafmtAll;")
addCommandAlias("preCommit", ";clean;compile;fmtAll;")
