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

  def findBySlug(slug: String): Future[Option[Product]] = {
    db.run(products.filter(_.slug === slug).result.headOption)
  }

  def create(product: Product): Future[Product] = {
    val insertQuery = products returning products.map(_.id) into ((product, id) => product.copy(id = Some(id)))
    db.run(insertQuery += product)
  }

  def update(product: Product): Future[Product] = {
    val query = products.filter(_.id === product.id.get)
      .map(p => (p.name, p.slug, p.description, p.price, p.currency, p.stock, p.active))
      .update((product.name, product.slug, product.description, product.price, product.currency, product.stock, product.active))

    db.run(query).map(_ => product)
  }
}
