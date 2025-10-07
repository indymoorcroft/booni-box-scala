package todo

import java.sql.Timestamp

case class Todo(
                 id: Option[Long] = None, // None means it's not yet stored in the DB
                 title: String,
                 complete: Boolean = false,
                 //   category is nullable - None means no category
                 categoryId: Option[Long],
                 createdAt: Timestamp,
                 updatedAt: Timestamp
               )