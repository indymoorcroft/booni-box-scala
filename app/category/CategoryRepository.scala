package category

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import category.Table.categories

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._

  def findAll(): Future[Seq[Category]] = {
    db.run(categories.result)
  }


  def findById(id: Long): Future[Option[Category]] = {
    db.run(categories.filter(_.id === id).result.headOption)
  }

  def create(category: Category): Future[Category] = {
    val insertQuery = categories returning categories.map(_.id) into ((category, id) => category.copy(id = Some(id)))
    db.run(insertQuery += category)
  }

  def update(category: Category): Future[Category] = {
    val query = categories.filter(_.id === category.id.get)
      .map(c => (c.name, c.description, c.updatedAt))
      .update((category.name, category.description, category.updatedAt))

    db.run(query).map(_ => category)
  }

  def delete(id: Long): Future[Int] = {
    db.run(categories.filter(_.id === id).delete)
  }

}