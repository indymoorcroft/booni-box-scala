package product

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.libs.json.Json
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ProductController @Inject()(cc: ControllerComponents, productService: ProductService)(implicit ec: ExecutionContext) extends AbstractController(cc){

  def getAllProducts: Action[AnyContent] = Action.async {
    productService.getAllProducts().map { products =>
      Ok(Json.toJson(products))
    }
  }

  def getProductById(id: Long): Action[AnyContent] = Action.async {
    productService.getProductById(id).map {
      case Right(product) => Ok(Json.toJson(product))
      case Left(error) => error.toResult
    }
  }

  def getProductBySlug(slug: String): Action[AnyContent] = Action.async {
    productService.getProductBySlug(slug).map {
      case Right(product) => Ok(Json.toJson(product))
      case Left(error) => error.toResult
    }
  }
}
