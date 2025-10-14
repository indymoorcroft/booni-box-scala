package utils

trait Validator {
  def isNotEmpty(fieldName: String, value: String): Option[(String, String)] =
    if (value.trim.isEmpty) Some(fieldName -> s"$fieldName cannot be empty")
    else None

  def isNonBlankIfDefined(fieldName: String, value: Option[String]): Option[(String, String)] =
    value match {
      case Some(v) if v.trim.isEmpty => Some(fieldName -> s"$fieldName cannot be blank if provided")
      case _ => None
    }

  def isValidPrice(fieldName: String, value: BigDecimal): Option[(String, String)] =
    if(value <= BigDecimal(0)) Some(fieldName -> "Price must be greater than 0")
    else None
}
