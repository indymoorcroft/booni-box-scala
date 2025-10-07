package category

import play.api.libs.json._
import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp


case class Category(
                     id: Option[Long] = None,
                     name: String,
                     description: Option[String] = None,
                     createdAt: Timestamp,
                     updatedAt: Timestamp
                   )