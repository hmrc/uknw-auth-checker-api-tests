lazy val root = (project in file("."))
  .settings(
    name := "uknw-auth-checker-api-tests",
    version := "0.1.0",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Dependencies.test
  )

addCommandAlias("fmtAll", ";scalafmtSbt;scalafmtAll;")
addCommandAlias("preCommit", ";clean;compile;fmtAll;")
