package product

import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductService @Inject()(productRepository: ProductRepository)(implicit ec: ExecutionContext){

  def getAllProducts(): Future[Seq[ProductResponse]] = {
    productRepository.findAll().map(_.map(ProductResponse.fromModel))
  }

  def getProductById(id: Long): Future[Either[ApiError, ProductResponse]] = {
    productRepository.findById(id).map {
      case Some(product) => Right(ProductResponse.fromModel(product))
      case None => Left(ApiError.NotFound(s"Product with id $id not found"))
    }
  }
}
