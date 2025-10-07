package todo
import java.time.LocalDateTime
import play.api.libs.json._ // Play JSON library used for serializing/deserializing case classes to/from JSON

// 1. API-facing DTO for todos
// 2. Converts DB model + category into a clean response object
// 3. Json.format - auto-converts to/from JSON
// 4. Design separation - keeps DB model separate from response format

case class TodoResponse( // This is meant to be used in HTTP responses - unlike the original Todo model, which is tied to database logic, this is tailored for client-facing data
                         id: Long,
                         title: String,
                         complete: Boolean,
                         category: String,
                         createdAt: LocalDateTime,
                         updatedAt: LocalDateTime
                       )
case class CreateTodoDto(title: String, complete: Boolean = false, categoryId: Option[Long])
case class UpdateTodoDto(title: Option[String], complete: Option[Boolean], categoryId: Option[Long])

object TodoResponse {
  implicit val format: OFormat[TodoResponse] = Json.format[TodoResponse] // Provides automatic JSON serialization/deserialization

  def fromModelWithCategory(todo: Todo, maybeCategoryName: Option[String]): TodoResponse =
    TodoResponse(
      id = todo.id.getOrElse(0L), // optional because it may not exist before DB insertion
      title = todo.title,
      complete = todo.complete,
      category = maybeCategoryName.getOrElse("Uncategorized"), // If no category show "Uncategorized"
      createdAt = todo.createdAt.toLocalDateTime,
      updatedAt = todo.updatedAt.toLocalDateTime // convert time used in DB to JSON-friendly time
    )
}

object CreateTodoDto {
  implicit val reads: Reads[CreateTodoDto] = Json.reads[CreateTodoDto]
}

object UpdateTodoDto {
  implicit val reads: Reads[UpdateTodoDto] = Json.reads[UpdateTodoDto]
}