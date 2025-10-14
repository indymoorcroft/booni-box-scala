package product

import play.api.db.slick.DatabaseConfigProvider
import product.Table.products
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._

  def findAll(): Future[Seq[Product]] = {
    db.run(products.sortBy(_.createdAt.desc).result)
  }

  def findById(id: Long): Future[Option[Product]] = {
    db.run(products.filter(_.id === id).result.headOption)
  }
}
