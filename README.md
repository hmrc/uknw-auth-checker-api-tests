# uknw-auth-checker-api-tests

Notice of Presentation API tests.

## Pre-requisites

### Services

Start `UKNW_AUTH_CHECKER_API` services as follows:

```bash
sm2 --start NOTIFICATION_OF_PRESENTATION_ALL
```

## Tests

Run tests as follows:

```bash
./run-tests.sh
```

### Testing Approach //TODO: Look at this before merging

This repository makes the use of a dynamic testing approach to follow that of the
[Stub](https://github.com/hmrc/uknw-auth-checker-api-stub) and [performance tests](https://github.com/hmrc/uknw-auth-checker-api-performance-tests).
This works through having a pre-determined set of authorised EORIs and a custom EORI generator.

The EORI generator is located within the [EoriGenerator](src/test/scala/uk/gov/hmrc/test/api/utils/EoriGenerator.scala) trait.
This works by allowing the input of the amount of EORIs and the amount of valid EORIs. The generator will then create a sequence with the correct combination based on the input.

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

Run tests as follows:

```bash
./run-tests-jenkins.sh
```

The Jenkins test script differs from run-tests.sh only in that it sets the `-Dzap.proxy` argument to true to allow security tests in Jenkins to work.

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

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
