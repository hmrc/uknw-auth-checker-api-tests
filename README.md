# uknw-auth-checker-api-tests

Notice of Presentation API tests.

## Pre-requisites

### Services

Start `UKNW_AUTH_CHECKER_API` services as follows:

```bash
sm2 --start NOTIFICATION_OF_PRESENTATION_ALL
```

## Tests

### Testing Approach

This repository makes the use of a dynamic testing approach to follow that of the
[Stub](https://github.com/hmrc/uknw-auth-checker-api-stub)
and [performance tests](https://github.com/hmrc/uknw-auth-checker-api-performance-tests).
This works through having a [pre-determined set of authorised EORIs](src/test/scala/uk/gov/hmrc/api/models/constants/Eoris.scala)
and a [custom EORI generator](src/test/scala/uk/gov/hmrc/api/utils/generators/EoriGenerator.scala).

The EORI generator allows a chosen number of EORIs and a chosen number of valid EORIs (up to the number of EORIs in the
predetermined list) to be generated. The generator will output a sequence with the correct combination based on the
input.

### Running the tests

Run tests as follows:

```bash
./run-tests.sh
```

## Running security tests with ZAP - on a local machine

Clones dast-config-manager and runs its makefile, sets up proxy ports for ZAP, then runs the API tests and opens the
results in your system browser.

```bash
./run-tests-zap.sh
``` 

Results of your ZAP run will be placed in `dast-config-manager/target/dast-reports`.
The index.html is the root page to view the results in a browser.

> Any changes to alert filters must be made in the
> [alert-filters.json](https://github.com/hmrc/uknw-auth-checker-api-tests/blob/main/alert-filters.json) file.

## API pipeline tests - Jenkins
### Worth noting that since version 3.1.0-M1 of the play-ws-standalone-json, Java 17 is required for the correct functioning of the pipeline

Please note that this is not meant to be run locally.

Run tests as follows:

```bash
./run-tests-jenkins.sh
```

The Jenkins test script differs from run-tests.sh only in that it sets the `-Dzap.proxy` argument to true to allow
security tests in Jenkins to work.

> The reason we don't use `run-tests-zap.sh` in the Jenkins pipelines is because of all the dast-config-manager set up
> in the bash script that's not required in the pipeline, as the job builders there take care of it.

## Commands

### Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```

## Custom commands

### Pre-Commit

This is a sbt command alias specific to this project. It will clean, compile then run a scala format of the
code and build.sbt files.

> `sbt preCommit`

### Format all

This is a sbt command alias specific to this project. It will format the code and build.sbt files.

> `sbt fmtAll`

## License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
