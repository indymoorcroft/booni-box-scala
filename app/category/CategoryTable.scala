package category


import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp

class Categories(tag: Tag) extends Table[Category](tag, "categories") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def description = column[Option[String]]("description")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def * = (id.?, name, description, createdAt, updatedAt) <> (Category.tupled, Category.unapply)
}

object Table {
  val categories = TableQuery[Categories]
}