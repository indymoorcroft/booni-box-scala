package product

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductService @Inject()(productRepository: ProductRepository)(implicit ec: ExecutionContext){

  def getAllProducts(): Future[Seq[ProductResponse]] = {
    productRepository.findAll().map(_.map(ProductResponse.fromModel))
  }

}
