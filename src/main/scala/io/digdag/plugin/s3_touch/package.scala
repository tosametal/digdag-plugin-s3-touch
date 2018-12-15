package io.digdag.plugin

import com.google.common.base.Optional
import io.digdag.spi.OperatorContext
import io.digdag.util.UserSecretTemplate

package object s3_touch {
  implicit class RichOptional[T](val optional: Optional[T]) {

    def toOption: Option[T] = optional match {
      case o if o.isPresent => Option(o.get())
      case _ => None
    }
  }

  implicit class RichString(string: String) {

    def formatSecret(implicit context: OperatorContext): String = {
      UserSecretTemplate.of(string).format(context.getSecrets)
    }
  }
}
