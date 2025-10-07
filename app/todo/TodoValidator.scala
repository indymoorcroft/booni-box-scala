package todo

import utils.validation.Validator

object TodoValidator extends Validator{
  def validateCreate(dto: CreateTodoDto): Map[String, String] = {
    List(
      isNotEmpty("title", dto.title)
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateTodoDto): Map[String, String] = {
    List(
      isNonBlankIfDefined("title", dto.title)
    ).flatten.toMap
  }
}
