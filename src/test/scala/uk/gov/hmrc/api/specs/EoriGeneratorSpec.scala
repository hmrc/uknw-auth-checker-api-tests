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

package uk.gov.hmrc.api.specs

import org.scalacheck.Gen
import uk.gov.hmrc.api.utils.{CustomRegex, EoriGenerator}

class EoriGeneratorSpec extends BaseSpec with EoriGenerator {
  Feature("eoriGenerator") {
    Scenario("generate a chosen sized list of authorised eoris") {
      When("only passed a numberOfEoris value")

      val numberOfEoris: Int = fetchRandomNumber(1, authorisedEoris.size)
      val eoris              = useEoriGenerator(numberOfEoris)

      Then("The list should contain the chosen number of eoris")

      eoris.size shouldBe numberOfEoris

      And("All eoris should be authorised")
      eoris.forall(eori => authorisedEoris.contains(eori)) shouldBe true

      And("All eoris should be unique")
      eoris.distinct.size shouldBe numberOfEoris

    }

    Scenario("generate all unique authorised eoris") {
      When("passed the max size of authorised eori test set")

      val eoris = useEoriGenerator(authorisedEoris.size)

      Then("The list should contain the chosen number of eoris")

      eoris.size shouldBe authorisedEoris.size

      And("All eoris should be authorised")
      eoris.forall(eori => authorisedEoris.contains(eori)) shouldBe true

      And("All eoris should be unique")
      eoris.distinct.size shouldBe authorisedEoris.size

    }

    Scenario("generate invalid eoris") {
      When("0 passed as the numberOfAuthorisedEoris test set")

      val numberOfEoris: Int = fetchRandomNumber(1, authorisedEoris.size)
      val eoris              = useEoriGenerator(numberOfEoris, 0)

      Then("The list should contain the chosen number of eoris")

      eoris.size shouldBe numberOfEoris

      And("All eoris should be authorised")
      eoris.forall(eori => authorisedEoris.contains(eori)) shouldBe false

      And("All eoris should be unique")
      eoris.distinct.size shouldBe numberOfEoris

    }

    Scenario("generate a chosen sized list of invalid eoris") {
      When("not passed a numberOfAuthorisedEoris value")

      val numberOfEoris: Int = fetchRandomNumber(1, authorisedEoris.size)
      val eoris              = useEoriGenerator(numberOfEoris, 0)

      Then("The list should contain the chosen number of eoris")

      eoris.size shouldBe numberOfEoris

      And("All eoris should be authorised")
      eoris.forall(eori => authorisedEoris.contains(eori)) shouldBe false

      And("All eoris should be unique")
      eoris.distinct.size shouldBe numberOfEoris

    }

    Scenario("generate a chosen sized list of authorised eoris and invalid eoris") {
      When("passed a numberOfEoris and numberOfValidEoris value")

      val numberOfEoris: Int = fetchRandomNumber(1, authorisedEoris.size)
      val validEoris: Int    = fetchRandomNumber(1, numberOfEoris - 1)

      val eoris: Seq[String] = useEoriGenerator(numberOfEoris, validEoris)

      val extractedAuthorisedEoris: Seq[String] = eoris.filter(authorisedEoris.contains)
      val extractedInvalidEoris: Seq[String]    = eoris.filter(!authorisedEoris.contains(_))

      Then("The list should contain the chosen number of authorised eoris")

      extractedAuthorisedEoris.size shouldBe validEoris

      And("The list should contain the chosen number of invalid eoris")

      extractedInvalidEoris.size shouldBe numberOfEoris - validEoris

      And("invalid eoris should have the correct regex")

      extractedInvalidEoris.forall(eori => eori.matches(CustomRegex.eoriPattern)) shouldBe true

      And("All authorised eoris should be contained in the authorisedEoris set")

      extractedAuthorisedEoris.forall(eori => authorisedEoris.contains(eori)) shouldBe true

      And("All eoris should be unique")

      eoris.distinct.size shouldBe numberOfEoris
    }

    Scenario("throw an exception with message Number of valid EORIs cannot be greater than the total number of EORIs") {
      When("when the number of valid eoris is greater than the total number of eoris")
      Then("An IllegalArgumentException should be thrown")

      val exception = intercept[IllegalArgumentException] {
        useEoriGenerator(1, 100)
      }

      exception.getMessage shouldBe "Number of authorised EORIs cannot be greater than the total number of EORIs"
    }

    Scenario(
      "two arguments must throw an exception with message Number of EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set"
    ) {
      When(
        "when the number of authorised eoris is greater than the total number of authorised eoris within the testing set"
      )
      Then("An IllegalArgumentException should be thrown")

      val exception = intercept[IllegalArgumentException] {
        useEoriGenerator(authorisedEoris.size + 2, authorisedEoris.size + 1)
      }

      exception.getMessage shouldBe s"Number of authorised EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set (${authorisedEoris.size})"
    }

    Scenario(
      "one argument must throw an exception with message Number of EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set"
    ) {
      When(
        "when the number of authorised eoris is greater than the total number of authorised eoris within the testing set"
      )
      Then("An IllegalArgumentException should be thrown")

      val exception = intercept[IllegalArgumentException] {
        useEoriGenerator(authorisedEoris.size + 1)
      }

      exception.getMessage shouldBe s"Number of authorised EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set (${authorisedEoris.size})"
    }
  }
}
