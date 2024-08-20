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

trait Generators {
  private val maxStringSize = 24

  private def specificSizeAlphaNumStrGen(maxStringSize: Int): Gen[String] = for {
    length <- Gen.choose(1, maxStringSize)
    str    <- Gen.listOfN(length, Gen.alphaNumChar).map(_.mkString)
  } yield str

  protected def fetchRandomNumber(min: Int, max: Int): Int = Gen.choose(min, max).sample.get

  protected def garbageGenerator(i: Int, maxSizeOfStrings: Int): Gen[Seq[String]] = Gen.listOfN(i, specificSizeAlphaNumStrGen(maxSizeOfStrings))
  protected def unrestrictedGarbageGenerator(i: Int, maxSizeOfStrings: Int): Gen[Seq[String]] = Gen.listOfN(i, Gen.alphaNumStr)

  protected def useGarbageGenerator(amountOfValues: Int, maxSizeOfStrings: Int = maxStringSize): Seq[String] = garbageGenerator(amountOfValues, maxSizeOfStrings).sample.get
  protected def useUnrestrictedGarbageGenerator(amountOfValues: Int, maxSizeOfStrings: Int = maxStringSize): Seq[String] = unrestrictedGarbageGenerator(amountOfValues, maxSizeOfStrings).sample.get
}
