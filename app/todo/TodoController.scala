package todo
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// 1. Entry point for HTTP request related to todos

class TodoController@Inject()( // Responsible for handling HTTP requests related to "todo" items and returning HTTP responses to the client. It connects the web/API layer to the service layer
                               cc: ControllerComponents,  // Required for HTTP controller behaviour e.g. Ok, BadRequest
                               todoService: TodoService // business logic layer for todo items
                             )(implicit ec: ExecutionContext) extends AbstractController(cc) { // EC needed for async computation. AbstractionController provides convenient methods like Action, Ok, NotFound

  def getAllTodos: Action[AnyContent] = Action.async { // Action.async creates an async HTTP action
    todoService.getAllTodos().map { todos => // calls the service layer to fetch all todos
      Ok(Json.toJson(todos)) // once the future completes it converts the list of TodoResponse to JSON and returns HTTP 200 OK with JSON body
    }
  }

  def getTodoById(id: Long): Action[AnyContent] = Action.async {
    todoService.getTodoById(id).map {
      case Right(todoResponse) => Ok(Json.toJson(todoResponse))
      case Left(error) => error.toResult
    }
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[CreateTodoDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => todoService.createTodo(dto).map {
        case Right(response) => Created(Json.toJson(response))
        case Left(error) => error.toResult
      }
    )
  }

  def updateById(id: Long) = Action.async(parse.json) { request =>
    request.body.validate[UpdateTodoDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => {
        todoService.updateTodoById(id, dto).map {
          case Right(todo) => Ok(Json.toJson(todo))
          case Left(err)   => err.toResult
        }
      }
    )
  }

  def deleteById(id: Long) = Action.async {
    todoService.deleteTodoById(id).map {
      case Right(_) => NoContent
      case Left(error) => error.toResult
    }
  }
}