package todo

import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp
import category.Categories

// 1. Defines the schema mapping of the todos table to the Todo case class
// 2. Column types, constraints, and foreign keys
// 3. A table query handle for use

class Todos(tag: Tag) extends Table[Todo](tag, "todos") { // maps a DB table "todos" to the scala case class Todo
  def id         = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title      = column[String]("title")
  def complete   = column[Boolean]("complete")
  def categoryId = column[Option[Long]]("category_id")
  def createdAt  = column[Timestamp]("created_at")
  def updatedAt  = column[Timestamp]("updated_at")

  // "fk_category" = name of the constraint, categoryId = source column, TableQuery[Categories] = target table | onDelete = if the category is deleted, set category_id to null
  def categoryFk = foreignKey("fk_category", categoryId, TableQuery[Categories])(_.id.?, onDelete = ForeignKeyAction.SetNull)

  // Table projection - how to convert a database row into a Todo and vice-versa
  // <> = Slick operator to map between a tuple and a case class, Todo.tupled = turns a tuple into a Todo, Todo.unapply extracts values form a Todo
  def * = (id.?, title, complete, categoryId, createdAt, updatedAt) <> (Todo.tupled, Todo.unapply)
}

// Defines a singleton object Table containing the table query handle. This is imported where database access is needed
object Table {
  val todos = TableQuery[Todos] // instantiates the table, which you can use for querying(filter, map, += etc)
}
