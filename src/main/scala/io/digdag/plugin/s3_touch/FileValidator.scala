package io.digdag.plugin.s3_touch

import java.security.InvalidParameterException

object FileValidator {

  def validate(fileName: String): Either[Throwable, String] = {
    val array = fileName.split("/")
    if (array.length == 1) Right(fileName)
    else {
      if (array.contains("..")) Left(new InvalidParameterException(s"Invalid fileName: $fileName"))
      else Right(fileName)
    }
  }
}
