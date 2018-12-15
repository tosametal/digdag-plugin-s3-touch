package io.digdag.plugin.s3_touch

import io.digdag.client.config.Config
import io.digdag.spi.{Operator, OperatorContext, OperatorFactory, TaskResult}
import io.digdag.util.BaseOperator

class S3TouchOperatorFactory extends OperatorFactory {
  override def getType: String = "s3_touch"

  override def newOperator(context: OperatorContext): Operator = {
    new S3TouchOperatorFactory.S3TouchOperator(context)
  }
}

object S3TouchOperatorFactory {

  private class S3TouchOperator(context: OperatorContext) extends BaseOperator(context) {
    override def runTask(): TaskResult = {
      val params = request.getConfig.mergeDefault(request.getConfig.getNestedOrGetEmpty("s3_touch"))
      val s3TouchConfig = params.get("s3_touch", classOf[Config])
      val fileName = params.get("_command", classOf[String])

      (for {
        s3Client <- S3ClientBuilder.build(s3TouchConfig)(context)
        s3uploader <- S3Uploader.apply(s3TouchConfig)(context)
        fileName <- FileValidator.validate(fileName)
        result <- s3uploader.upload(s3TouchConfig, s3Client, fileName)(context)
      } yield result) match {
        case Right(_) =>
          println("Success to upload")
        case Left(e) =>
          e.printStackTrace()
          scala.sys.exit(1)
      }

      TaskResult.empty(request)
    }
  }
}
