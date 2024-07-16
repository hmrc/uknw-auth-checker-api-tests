
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

* Argument `<environment>` must be `local`, `dev`, `qa` or `staging`.

```bash
./run-tests.sh <environment>
```

If no argument is passed, `local` will be defaulted.

## Running with ZAP - on a local machine

```bash
./run-tests-zap.sh
``` 
Results of your ZAP run will be placed in `dast-config-manager/target/dast-reports/index.html` file

## API pipeline tests - Jenkins

Run tests as follows:

```bash
./run-tests-jenkins.sh
```

The Jenkins test script differs from run-tests.sh only in that it sets the `-Dzap.proxy` argument to true to allow security tests in Jenkins to work.

## Scalafmt

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

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
