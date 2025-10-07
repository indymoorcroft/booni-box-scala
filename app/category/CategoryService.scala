package category

import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Timestamp
import java.time.Instant

@Singleton
class CategoryService @Inject()(categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {

  def getAllCategories(): Future[Seq[CategoryResponse]] = {
    categoryRepository.findAll().map(_.map(CategoryResponse.fromModel))
  }

  def getCategoryById(id: Long): Future[Either[ApiError, CategoryResponse]] = {
    categoryRepository.findById(id).map {
      case Some(category) => Right(CategoryResponse.fromModel(category))
      case None => Left(ApiError.NotFound(s"Category with id $id not found"))
    }
  }

  def createCategory(data: CreateCategoryDto): Future[Either[ApiError, CategoryResponse]] = {
    val errors = CategoryValidator.validateCreate(data);
    if (errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      val now = Timestamp.from(Instant.now())
      val preSaved = Category(
        id = None,
        name = data.name.trim,
        description = data.description.map(_.trim).filter(_.nonEmpty),
        createdAt = now,
        updatedAt = now
      )
      categoryRepository.create(preSaved).map(saved => Right(CategoryResponse.fromModel(saved)))
    }
  }

  def updateCategoryById(id: Long, data: UpdateCategoryDto): Future[Either[ApiError, CategoryResponse]] = {
    val errors = CategoryValidator.validatePatch(data)
    if (errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      categoryRepository.findById(id).flatMap {
        case None => Future.successful(Left(ApiError.NotFound(s"Category with id $id not found")))
        case Some(existing) =>
          val updates = Map(
            "name" -> data.name.map(_.trim),
            "description" -> data.description.map(_.trim).filter(_.nonEmpty)
          ).collect { case (k, Some(v)) => k -> v }

          val updated = existing.copy(
            name = updates.getOrElse("name", existing.name),
            description = updates.get("description").orElse(existing.description),
            updatedAt = Timestamp.from(Instant.now())
          )

          categoryRepository.update(updated).map(c => Right(CategoryResponse.fromModel(c)))
      }
    }
  }

  def deleteCategoryById(id: Long): Future[Either[ApiError, Unit]] = {
    categoryRepository.delete(id).map { rowsAffected =>
      if (rowsAffected > 0) Right(())
      else Left(ApiError.NotFound(s"Category with id $id not found"))
    }
  }

}
