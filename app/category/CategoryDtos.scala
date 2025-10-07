package category

import play.api.libs.json._
import java.time.LocalDateTime

case class CategoryResponse(id: Long, name: String, description: Option[String], createdAt: LocalDateTime, updatedAt: LocalDateTime)
case class CreateCategoryDto(name: String, description: Option[String])
case class UpdateCategoryDto(name: Option[String], description: Option[String])

object CategoryResponse {
    implicit val format: OFormat[CategoryResponse] = Json.format[CategoryResponse]

    def fromModel(model: Category): CategoryResponse = {
      CategoryResponse(id = model.id.getOrElse(0L), name = model.name, description = model.description, createdAt = model.createdAt.toLocalDateTime, updatedAt = model.updatedAt.toLocalDateTime)
    }
}

object CreateCategoryDto {
  implicit val reads: Reads[CreateCategoryDto] = Json.reads[CreateCategoryDto]
}

object UpdateCategoryDto {
  implicit val reads: Reads[UpdateCategoryDto] = Json.reads[UpdateCategoryDto]
}