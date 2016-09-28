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

package uk.gov.hmrc.bbsi.stub.controllers

import uk.gov.hmrc.bbsi.stub.model.{Iabds, BBSIDetails, Error}
import uk.gov.hmrc.play.microservice.controller.BaseController

import play.api.mvc._
import play.api.libs.json._

import uk.gov.hmrc.bbsi.stub.repository.{MongoIabdsRepository, IabdsRepository, BBSIRepository, MongoBBSIRepository}

import scala.concurrent.{Future}

import scala.concurrent.ExecutionContext.Implicits.global

object BBSIStubController extends BBSIStubController {
  override val bbsiRepository = MongoBBSIRepository()
  override val iabdsRepository = MongoIabdsRepository()
}

trait BBSIStubController extends BaseController {

  val bbsiRepository: BBSIRepository
  val iabdsRepository: IabdsRepository

  def createBBSI(nino: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    val bbsiJs = request.body.validate[BBSIDetails]
    bbsiJs.fold(
      errors => Future.successful(BadRequest(Json.toJson(Error(message = "body failed validation with errors: " + errors)))),
      bbsi =>
        bbsiRepository.insert(bbsi)
          .map { _ => Ok}
          .recover { case exception => Results.InternalServerError(exception.toString)}
    )
  }

  def createIabds(nino: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    val iabdsJs = request.body.as[Seq[Iabds]]

    iabdsRepository.bulkInsert(iabdsJs).map { _ => Ok}
      .recover { case exception => Results.InternalServerError(exception.toString)}
  }

  def findBBSIDetailsByNino(nino: String) = Action.async { implicit request =>
    val allDetails = bbsiRepository.findBBSIDetailsByNino(nino)
    allDetails.map { details =>
      Ok(Json.toJson(details))
    }.recover { case exception => Results.InternalServerError(exception.toString)}
  }

  def findIabdsByNino(nino: String) = Action.async { implicit request =>
    val allDetails = iabdsRepository.findIabdsByNino(nino)
    allDetails.map { details =>
      Ok(Json.toJson(details))
    }.recover { case exception => Results.InternalServerError(exception.toString)}
  }

  def findIabdsByNinoAndType(nino: String, `type`: Int) = Action.async { implicit request =>
    val allDetails = iabdsRepository.findIabdsByNinoAndType(nino, `type`)
    allDetails.map { details =>
      Ok(Json.toJson(details))
    }.recover { case exception => Results.InternalServerError(exception.toString)}
  }

  def removeAllBBSI = Action.async { implicit request =>
    bbsiRepository.removeAllBBSIDetails
    Future.successful(Ok)
  }

  def dropBBSICollection() = Action.async { implicit request =>
    bbsiRepository.removeBBSICollection()
    Future.successful(Ok)
  }

  def dropIabdsCollection() = Action.async { implicit request =>
    iabdsRepository.removeIabdCollection()
    Future.successful(Ok)
  }
}
