package product

import java.sql.Timestamp

case class ProductModel(
                         id: Option[Long] = None,
                         name: String,
                         slug: String,
                         description: Option[String],
                         price: BigDecimal,
                         currency: String = "GBP",
                         stock: Int = 0,
                         active: Boolean = true,
                         createdAt: Timestamp,
                         updatedAt: Timestamp
                       )
