package product

import utils.ApiError

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductService @Inject()(productRepository: ProductRepository)(implicit ec: ExecutionContext){

  private def nowTs(): Timestamp = Timestamp.from(Instant.now())

  private def slugify(name: String): String =
    name.toLowerCase.trim.replaceAll("[^a-z0-9]+", "-")

  def getAllProducts(): Future[Seq[ProductResponse]] = {
    productRepository.findAll().map(_.map(ProductResponse.fromModel))
  }

  def getProductById(id: Long): Future[Either[ApiError, ProductResponse]] = {
    productRepository.findById(id).map {
      case Some(product) => Right(ProductResponse.fromModel(product))
      case None => Left(ApiError.NotFound(s"Product with id $id not found"))
    }
  }

  def getProductBySlug(slug: String): Future[Either[ApiError, ProductResponse]] = {
    productRepository.findBySlug(slug).map {
      case Some(product) => Right(ProductResponse.fromModel(product))
      case None => Left(ApiError.NotFound(s"Product $slug not found"))
    }
  }

  def createProduct(data: CreateProductDto): Future[Either[ApiError, ProductResponse]] = {
    val errors = ProductValidator.validateCreate(data)
    if(errors.nonEmpty){
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      val now = nowTs()
      val slug = slugify(data.name)

      val product = Product(
        id = None,
        name = data.name.trim(),
        slug = slug,
        description = data.description.filter(_.trim.nonEmpty),
        price = data.price,
        currency = data.currency.getOrElse("GBP"),
        stock = data.stock.getOrElse(0),
        active = data.active.getOrElse(true),
        createdAt = now,
        updatedAt = now
      )

      productRepository.create(product).map(saved => Right(ProductResponse.fromModel(saved)))
    }
  }
}
