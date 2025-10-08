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
}
