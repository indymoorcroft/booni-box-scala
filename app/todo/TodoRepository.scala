package todo
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// 1. Handles DB operations for todos
// 2. Uses Play's Dependency Injection to get a DB connection and execution context
// 3. Method findAllWithCategoryNames
// 4. query - Slick joinLeft on todos and categories or None

@Singleton
class TodoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile] // retrieves the DB configuration from Play
  import dbConfig._
  import category.Table.categories // imports the table definitions
  import Table.todos

  def findAllWithCategoryNames(): Future[Seq[(Todo, Option[String])]] = { // Returns all Todo records joined with their category names. Wraps the result in a Future (as it is async)

    val query = for {
      (todo, categoryOpt) <- todos
        .joinLeft(categories) // If todo has no category, categoryOpt will be None
        .on(_.categoryId === _.id) // performs a left outer join between todos and categories matching on categoryId
    } yield (todo, categoryOpt.map(_.name)) // for each result return: the todo row & the category name if it exists
    db.run(query.result) // result: a Seq of tuples (Todo, Option[String])
  }

  def findById(id: Long): Future[Option[(Todo, Option[String])]] = {
    val query = for {
      (todo, categoryOpt) <- todos
        .joinLeft(categories)
        .on(_.categoryId === _.id)
      if todo.id === id
    } yield (todo, categoryOpt.map(_.name))

    db.run(query.result.headOption)
  }

  def create(todo: Todo): Future[Todo] = {
    val insertQuery = todos returning todos.map(_.id) into ((todo, id) => todo.copy(id = Some(id)))
    db.run(insertQuery += todo)
  }

  def update(todo: Todo): Future[Todo] = {
    val query = todos.filter(_.id === todo.id.get)
      .map(t => (t.title, t.complete, t.categoryId, t.updatedAt))
      .update((todo.title, todo.complete, todo.categoryId, todo.updatedAt))

    db.run(query).map(_ => todo)
  }

  def delete(id: Long): Future[Int] = {
    db.run(todos.filter(_.id === id).delete)
  }
}