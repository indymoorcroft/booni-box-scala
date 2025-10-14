package product

import utils.Validator

object ProductValidator extends Validator {
  def validateCreate(dto: CreateProductDto): Map[String, String] = {
    List(
      isNotEmpty("name", dto.name),
      isNonBlankIfDefined("description", dto.description),
      isValidPrice("price", dto.price),
      isNonBlankIfDefined("currency", dto.currency),
    ).flatten.toMap
  }
}
