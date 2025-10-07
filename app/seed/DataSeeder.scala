package seed

import play.api.db.slick.DatabaseConfigProvider
import product.Product
import product.Table.products
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataSeeder @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  def seed(): Future[Unit] = {
    val now = Timestamp.from(Instant.now())
    val initialProducts = Seq(
      Product(
        id = None,
        name = "Artisan Coffee Beans",
        slug = "artisan-coffee-beans",
        description = Some("Freshly roasted Arabica beans sourced from small farms in Colombia. Perfect for espresso or pour-over brewing."),
        price = BigDecimal(14.99),
        stock = 120,
        createdAt = now,
        updatedAt = now
      ),
      Product(
        id = None,
        name = "Handmade Ceramic Mug",
        slug = "handmade-ceramic-mug",
        description = Some("A 350ml handmade ceramic mug with a smooth matte finish, perfect for coffee or tea lovers."),
        price = BigDecimal(22.50),
        stock = 60,
        createdAt = now,
        updatedAt = now
      ),
      Product(
        id = None,
        name = "Gourmet Dark Chocolate Bar",
        slug = "gourmet-dark-chocolate-bar",
        description = Some("Rich 70% cocoa dark chocolate infused with sea salt and caramel flakes. A perfect indulgence or gift."),
        price = BigDecimal(6.75),
        stock = 200,
        createdAt = now,
        updatedAt = now
      )
    )

    val insertIfEmpty = for {
      exists <- products.exists.result
      _ <- if(!exists) products ++= initialProducts else DBIO.successful(())
    } yield ()

    db.run(insertIfEmpty.transactionally)
  }
}
