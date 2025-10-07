package utils

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import category.Table.categories
import category.Category
import todo.Todo
import todo.Table.todos
import java.sql.Timestamp
import java.time.LocalDateTime

// 1. Check if tables are empty
// 2. If empty, inserts default entries
// 3. Safely maps foreign key relationships
// 4. Runs the whole process transactionally

@Singleton
class DataSeeder @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile] // Gets the Slick JdbcProfile (MySQL)
  import dbConfig._
  import profile.api._

  def seed(): Future[Unit] = { // seeds the DB with sample data
    val now = Timestamp.valueOf(LocalDateTime.now())
    val initialCategories = Seq( // Creates 3 sample categories
      Category(None, "Personal", Some("Personal tasks"), now, now),
      Category(None, "Work", Some("Work stuff"), now, now),
      Category(None, "Shopping", Some("Groceries and things"), now, now)
    )


    val setup = for { // DBIO action block using a for-comprehension, chaining actions together transactionally
      categoriesExist <- categories.exists.result // checks if there is any data in either table
      todosExist <- todos.exists.result

      categoryIds <- if (!categoriesExist) { // Insert categories if empty
        val insertQuery = categories returning categories.map(_.id) into ((cat, id) => cat.copy(id = Some(id))) // Uses Slick's returning ... into syntax to insert and retrieve the generated IDs
        insertQuery ++= initialCategories
      } else {
        categories.result // If categories already exists just fetches all categories
      }


      catNameMap: Map[String, Long] = categoryIds.map(cat => cat.name -> cat.id.get).toMap // Build a map with categories as the key and ID as the value

      _ <- if (!todosExist) { // Defines initial Todo records if table is empty
        val initialTodos = Seq(
          Todo(None, "Buy groceries", false, catNameMap.get("Shopping"), now, now),
          Todo(None, "Finish code examples", false, catNameMap.get("Work"), now, now),
          Todo(None, "Call my brother", false, catNameMap.get("Personal"), now, now),
          Todo(None, "Uncategorized task", false, None, now, now)
        )
        todos ++= initialTodos // Bulk insert
      } else {
        DBIO.successful(()) // Do nothing
      }
    } yield ()

    db.run(setup.transactionally) // Executes the entire setup block as a single database transaction
  }
}