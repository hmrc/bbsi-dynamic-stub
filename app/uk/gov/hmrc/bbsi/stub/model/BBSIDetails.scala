/*
 * Copyright 2016 HM Revenue & Customs
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

package uk.gov.hmrc.bbsi.stub.model

import play.api.libs.json.{Json}
import play.api.libs.json.{Format}

case class BBSIDetails(
                        nino: String,
                        taxYear: String,
                        accounts: List[Account]
                        )

case class Account(accountNumber: String, sortCode: String, bankName: String,
                   numberOfAccountHolders: Option[Int], grossInterest: Long, percentageSplit: Option[Int])

object BBSIDetails {

  implicit lazy val bbsiDetailsFormat: Format[BBSIDetails] = Json.format[BBSIDetails]
}

object Account {

  implicit lazy val accountFormat: Format[Account] = Json.format[Account]
}
