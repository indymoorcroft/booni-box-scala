package category

import utils.validation.Validator

object  CategoryValidator extends Validator {
  def validateCreate(dto: CreateCategoryDto): Map[String, String] = {
    List(
      isNotEmpty("name", dto.name),
      isNonBlankIfDefined("description", dto.description)
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateCategoryDto): Map[String, String] = {
    List(
      isNonBlankIfDefined("name", dto.name),
      isNonBlankIfDefined("description", dto.description)
    ).flatten.toMap
  }
}