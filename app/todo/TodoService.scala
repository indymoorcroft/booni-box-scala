package todo

import utils.ApiError
import utils.ApiError.NotFound

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// 1. Business logic layer for todos
// 2. Pulls todos with optional category names from DB
// 3. Returns data suitable for API response. Is typically passed to a controller, then returned as a JSON response

@Singleton
class TodoService@Inject()(todoRepository: TodoRepository)(implicit ec: ExecutionContext)  { // a service class encapsulating business logic for "todo" items. Constructor takes a TodoRepository (to access the DB layer) and an implicit ExecutionContext (to handle async operations)

  def getAllTodos(): Future[Seq[TodoResponse]] = { // method returns all todos in the system, formatted as TodoResponse objects
    todoRepository.findAllWithCategoryNames().map { results =>
      results.map { case (todo, maybeCategoryName) =>
        TodoResponse.fromModelWithCategory(todo, maybeCategoryName) // transforms each (Todo, Option[String]) into a TodoResponse
      }
    }
  }

  def getTodoById(id: Long): Future[Either[ApiError, TodoResponse]] = {
    todoRepository.findById(id).map {
      case Some((todo, maybeCategoryName)) =>
        Right(TodoResponse.fromModelWithCategory(todo, maybeCategoryName))
      case None =>
        Left(NotFound(s"Todo with ID $id not found"))
    }
  }

  def createTodo(data: CreateTodoDto): Future[Either[ApiError, TodoResponse]] = {
    val errors = TodoValidator.validateCreate(data)
    if(errors.nonEmpty){
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      val now = Timestamp.from(Instant.now())

      val todo = Todo(
        id = None,
        title = data.title,
        complete = data.complete,
        categoryId = data.categoryId,
        createdAt = now,
        updatedAt = now
      )

      todoRepository.create(todo).map { created =>
        Right(TodoResponse.fromModelWithCategory(created, None))
      }
    }
  }

  def updateTodoById(id: Long, data: UpdateTodoDto): Future[Either[ApiError, TodoResponse]] = {
    val errors = TodoValidator.validatePatch(data)
    if (errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      todoRepository.findById(id).flatMap {
        case None => Future.successful(Left(ApiError.NotFound(s"Todo with ID $id not found")))
        case Some((existing, _)) =>
          val updates = Map(
            "title" -> data.title.map(_.trim),
            "complete" -> data.complete.map(_.toString),
            "categoryId" -> data.categoryId.map(_.toString)
          ).collect { case (k, Some(v)) => k -> v }

          val updated = existing.copy(
            title = updates.getOrElse("title", existing.title),
            complete = updates.get("complete").map(_.toBoolean).getOrElse(existing.complete),
            categoryId = data.categoryId.orElse(existing.categoryId),
            updatedAt = Timestamp.from(Instant.now())
          )

          todoRepository.update(updated).flatMap { _ =>
            todoRepository.findById(id).map {
              case Some((todo, maybeCategoryName)) =>
                Right(TodoResponse.fromModelWithCategory(todo, maybeCategoryName))
              case None =>
                Left(ApiError.InternalServerError("Failed to fetch updated Todo after update"))
            }
          }
      }
    }
  }

  def deleteTodoById(id: Long): Future[Either[ApiError, Unit]] = {
    todoRepository.delete(id).map { rowsAffected =>
      if (rowsAffected > 0) Right(())
      else Left(ApiError.NotFound(s"Todo with id $id not found"))
    }
  }
}