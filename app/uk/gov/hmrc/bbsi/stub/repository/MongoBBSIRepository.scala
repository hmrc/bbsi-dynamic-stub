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

package uk.gov.hmrc.bbsi.stub.repository

import uk.gov.hmrc.bbsi.stub.model.{Iabds, BBSIDetails}
import uk.gov.hmrc.mongo.{Repository, ReactiveRepository}

import play.modules.reactivemongo.MongoDbConnection

import reactivemongo.api.indexes.{IndexType, Index}
import reactivemongo.api.DB
import reactivemongo.api.commands.WriteConcern
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{ExecutionContext, Future}

object MongoBBSIRepository extends MongoDbConnection {
  private lazy val repository = new MongoBBSIRepository

  def apply(): MongoBBSIRepository = repository
}

trait BBSIRepository extends Repository[BBSIDetails, BSONObjectID] {

  def findBBSIDetailsByNino(nino: String)(implicit ec: ExecutionContext): Future[List[BBSIDetails]]

  def removeAllBBSIDetails()(implicit ec: ExecutionContext): Future[Unit]

  def removeBBSICollection()(implicit ec: ExecutionContext): Future[Boolean]
}

class MongoBBSIRepository(implicit mongo: () => DB)
  extends ReactiveRepository[BBSIDetails, BSONObjectID]("bbsiDetails", mongo, BBSIDetails.bbsiDetailsFormat)
  with BBSIRepository {

  override def indexes = Seq(
    Index(Seq("nino" -> IndexType.Ascending),
      name = Some("ninoIdx"),
      unique = true,
      sparse = true)
  )

  override def findBBSIDetailsByNino(nino: String)(implicit ec: ExecutionContext): Future[List[BBSIDetails]] = {
    find("nino" -> nino)
  }

  override def removeAllBBSIDetails()(implicit ec: ExecutionContext): Future[Unit] = {
    removeAll(WriteConcern.Acknowledged).map { _ =>}
  }

  override def removeBBSICollection()(implicit ec: ExecutionContext): Future[Boolean] =
    drop(ec)

}
