package product

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

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

  def create: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateProductDto] match {
      case JsSuccess(dto, _) =>
        productService.createProduct(dto).map {
          case Right(response) => Created(Json.toJson(response))
          case Left(error) => error.toResult
        }
      case e: JsError =>
        Future.successful(ApiError.InvalidJson(e).toResult)
    }
  }

  def updateById(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateProductDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => productService.updateProductById(id, dto).map {
        case Right(response) => Ok(Json.toJson(response))
        case Left(error) => error.toResult
      }
    )
  }

  def deleteById(id: Long): Action[AnyContent] = Action.async {
    productService.deleteProductById(id).map {
      case Right(_) => NoContent
      case Left(error) => error.toResult
    }
  }
}
