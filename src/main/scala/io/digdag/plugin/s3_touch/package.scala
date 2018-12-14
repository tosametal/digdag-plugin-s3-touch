package io.digdag.plugin

import com.google.common.base.Optional

package object s3_touch {
  implicit class RichOptional[T](val optional: Optional[T]) {
    def toOption: Option[T] = optional match {
      case o if o.isPresent => Option(o.get())
      case _ => None
    }
  }
}
