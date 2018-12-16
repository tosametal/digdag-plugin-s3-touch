package io.digdag.plugin.s3_touch

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FileValidatorSpec extends Specification {

  trait Common extends Scope {
    val validFileName = "ok.txt"
    val validFileNameWithDirectory = "path/to/file/ok.txt"
    val inValidFileName = "path/../ng.txt"
  }

  "#validate" should {
    "return Right" in new Common {
      FileValidator.validate(validFileName) should beRight(validFileName)
      FileValidator.validate(validFileNameWithDirectory) should beRight(validFileNameWithDirectory)
    }
    "return Left" in new Common {
      FileValidator.validate(inValidFileName) should beLeft
    }
  }
}
