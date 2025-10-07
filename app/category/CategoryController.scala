package category

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(cc: ControllerComponents, categoryService: CategoryService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllCategories: Action[AnyContent] = Action.async {
    categoryService.getAllCategories().map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def getCategoryById(id: Long): Action[AnyContent] = Action.async {
    categoryService.getCategoryById(id).map {
      case Right(category) => Ok(Json.toJson(category))
      case Left(error) => error.toResult
    }
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[CreateCategoryDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => categoryService.createCategory(dto).map {
        case Right(response) => Created(Json.toJson(response))
        case Left(error)     => error.toResult
      }
    )
  }

  def updateById(id: Long) = Action.async(parse.json) { request =>
    request.body.validate[UpdateCategoryDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => categoryService.updateCategoryById(id, dto).map {
        case Right(response) => Ok(Json.toJson(response))
        case Left(error)     => error.toResult
      }
    )
  }

  def deleteById(id: Long) = Action.async {
    categoryService.deleteCategoryById(id).map {
      case Right(_)     => NoContent
      case Left(error)  => error.toResult
    }
  }

}