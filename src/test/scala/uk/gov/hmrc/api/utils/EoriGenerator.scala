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

package uk.gov.hmrc.api.utils

import org.scalacheck.Gen
import wolfendale.scalacheck.regexp.RegexpGen

trait EoriGenerator extends Eoris with Generators {

  private val eoriGen: Gen[String] = RegexpGen.from(CustomRegex.eoriPattern)

  private def authorisedEoriGen(numberOfAuthorisedEoris: Int): Gen[Seq[String]] =
    Gen.pick(numberOfAuthorisedEoris, authorisedEoris).map(_.to(Seq))

  private def invalidEoriGen(numberOfEoris: Int): Gen[Seq[String]] =
    Gen.listOfN(numberOfEoris, eoriGen)

  private def combinedEoriGen(numberOfEoris: Int, numberOfAuthorisedEoris: Int): Gen[Seq[String]] =
    for {
      authorisedEoris <- authorisedEoriGen(numberOfAuthorisedEoris)
      invalidEoris    <- invalidEoriGen(numberOfEoris - numberOfAuthorisedEoris)
    } yield authorisedEoris ++ invalidEoris

  protected def eoriGenerator(numberOfAuthorisedEoris: Int): Gen[Seq[String]] = {
    if (numberOfAuthorisedEoris > authorisedEoris.size) {
      throw new IllegalArgumentException(
        s"Number of authorised EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set (${authorisedEoris.size})"
      )
    }
    combinedEoriGen(numberOfAuthorisedEoris, numberOfAuthorisedEoris)
  }

  protected def eoriGenerator(numberOfEoris: Int, numberOfAuthorisedEoris: Int): Gen[Seq[String]] = {
    if (numberOfAuthorisedEoris > numberOfEoris) {
      throw new IllegalArgumentException("Number of authorised EORIs cannot be greater than the total number of EORIs")
    }

    if (numberOfAuthorisedEoris > authorisedEoris.size) {
      throw new IllegalArgumentException(
        s"Number of authorised EORIs cannot be greater than the total number of authorised EORIs within the authorised EORI test set (${authorisedEoris.size})"
      )
    }

    combinedEoriGen(numberOfEoris, numberOfAuthorisedEoris)
  }

  protected def useEoriGenerator(numberOfEoris: Int, numberOfAuthorisedEoris: Int): Seq[String] =
    eoriGenerator(numberOfEoris, numberOfAuthorisedEoris).sample.get

  protected def useEoriGenerator(numberOfAuthorisedEoris: Int): Seq[String] =
    eoriGenerator(numberOfAuthorisedEoris, numberOfAuthorisedEoris).sample.get
}
