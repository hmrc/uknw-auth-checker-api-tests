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

package uk.gov.hmrc.api.utils.generators

import org.scalacheck.Gen
import uk.gov.hmrc.api.models.constants.{CustomRegex, Eoris}
import wolfendale.scalacheck.regexp.RegexpGen

trait EoriGenerator extends Eoris, Generators {

  extension (gen: Gen[String]) {
    def excludeReserved: Gen[String] =
      gen.suchThat(eori => !reservedEoris.values.toSet.contains(eori))
  }

  private val eoriGen: Gen[String] = RegexpGen.from(CustomRegex.eoriPattern).excludeReserved

  private def authorisedEoriGen(numberOfAuthorisedEoris: Int): Gen[Seq[String]] =
    Gen.pick(numberOfAuthorisedEoris, authorisedEoris).map(_.toSeq)

  private def invalidEoriGen(numberOfEoris: Int): Gen[Seq[String]] =
    Gen.listOfN(numberOfEoris, eoriGen)

  private def combinedEoriGen(numberOfEoris: Int, numberOfAuthorisedEoris: Int): Gen[Seq[String]] =
    for {
      authorisedEoris <- authorisedEoriGen(numberOfAuthorisedEoris)
      invalidEoris    <- invalidEoriGen(numberOfEoris - numberOfAuthorisedEoris)
    } yield authorisedEoris ++ invalidEoris

  private def validateEoriCounts(numberOfEoris: Int, numberOfAuthorisedEoris: Int): Unit = {
    if (numberOfAuthorisedEoris > numberOfEoris) {
      throw new IllegalArgumentException("Number of authorised EORIs cannot be greater than the total number of EORIs")
    }

    if (numberOfAuthorisedEoris > authorisedEoris.size) {
      throw new IllegalArgumentException(
        s"Number of authorised EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set (${authorisedEoris.size})"
      )
    }
  }

  protected def eoriGenerator(numberOfEoris: Int, numberOfAuthorisedEoris: Option[Int] = None): Gen[Seq[String]] = {
    val numOfAuthEoris = numberOfAuthorisedEoris.getOrElse(numberOfEoris)
    validateEoriCounts(numberOfEoris, numOfAuthEoris)
    combinedEoriGen(numberOfEoris, numOfAuthEoris)
  }

  protected def useEoriGenerator(numberOfEoris: Int, numberOfAuthorisedEoris: Option[Int] = None): Seq[String] =
    eoriGenerator(numberOfEoris, numberOfAuthorisedEoris).sample.get
}
