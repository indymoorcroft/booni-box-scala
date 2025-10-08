package product

import play.api.libs.json._
import java.time.LocalDateTime

case class ProductResponse(id: Long, name: String, slug: String, description: Option[String], price: BigDecimal, currency: String, stock: Int, active: Boolean, createdAt: LocalDateTime, updatedAt: LocalDateTime)

object ProductResponse {
  implicit val format: OFormat[ProductResponse] = Json.format[ProductResponse]

  def fromModel(model: Product): ProductResponse = {
    ProductResponse(id = model.id.getOrElse(0L), name = model.name, slug = model.slug, description = model.description, price = model.price, currency = model.currency, stock = model.stock, active = model.active, createdAt = model.createdAt.toLocalDateTime, updatedAt = model.updatedAt.toLocalDateTime)
  }
}