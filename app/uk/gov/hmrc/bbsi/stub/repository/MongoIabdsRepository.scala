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

import uk.gov.hmrc.bbsi.stub.model.Iabds
import uk.gov.hmrc.mongo.{Repository,ReactiveRepository}

import play.modules.reactivemongo.MongoDbConnection

import reactivemongo.api.indexes.{IndexType, Index}
import reactivemongo.api.DB
import reactivemongo.api.commands.WriteConcern
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{ExecutionContext, Future}

object MongoIabdsRepository extends MongoDbConnection {
  private lazy val repository = new MongoIabdsRepository

  def apply() : MongoIabdsRepository = repository
}

trait IabdsRepository extends Repository[Iabds, BSONObjectID] {
  def findIabdsByNino(nino: String)(implicit ec: ExecutionContext): Future[List[Iabds]]

  def findIabdsByNinoAndType(nino: String, `type`: Int)(implicit ec: ExecutionContext): Future[List[Iabds]]

  def removeAllIabds()(implicit ec: ExecutionContext): Future[Unit]

  def removeIabdCollection()(implicit ec: ExecutionContext): Future[Boolean]
}

class MongoIabdsRepository(implicit mongo: () => DB)
  extends ReactiveRepository[Iabds, BSONObjectID]("iabds", mongo, Iabds.iabdsFormat)
  with IabdsRepository {

  override def findIabdsByNino(nino: String)(implicit ec: ExecutionContext): Future[List[Iabds]] = {
    find("nino" -> nino)
  }

  override def findIabdsByNinoAndType(nino: String, `type`: Int)(implicit ec: ExecutionContext): Future[List[Iabds]] = {
    find("nino" -> nino, "type" -> `type`)
  }

  override def removeAllIabds()(implicit ec: ExecutionContext): Future[Unit] = {
    removeAll(WriteConcern.Acknowledged).map { _ =>}
  }

  override def removeIabdCollection()(implicit ec: ExecutionContext): Future[Boolean] =
    drop(ec)
}
