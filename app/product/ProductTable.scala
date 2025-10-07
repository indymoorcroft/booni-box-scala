package product

import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp

class Products(tag: Tag) extends Table[Product](tag, "products") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def slug = column[String]("slug")
  def description = column[Option[String]]("description")
  def price = column[BigDecimal]("price")
  def currency = column[String]("currency")
  def stock = column[Int]("stock")
  def active = column[Boolean]("active")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def * = (id.?, name, slug, description, price, currency, stock, active, createdAt, updatedAt) <> (Product.tupled, Product.unapply)
}

object Table {
  val products = TableQuery[Products]
}
