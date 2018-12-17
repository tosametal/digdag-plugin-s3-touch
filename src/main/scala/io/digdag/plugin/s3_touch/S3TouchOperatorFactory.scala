package io.digdag.plugin.s3_touch

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
      val config = request.getConfig.mergeDefault(request.getConfig.getNestedOrGetEmpty("s3_touch"))

      (for {
        p <- ParamParser.parse(config)(context)
        fileName <- FileValidator.validate(p.fileName)
        s3Client <- S3ClientBuilder.build(p.accessKey, p.secretKey, p.maybeProxyHost, p.maybeProxyPort, p.serviceEndpoint, p.defaultRegion)
        result <- S3Uploader.upload(s3Client, p.bucketName, fileName, p.acl)
      } yield result) match {
        case Right(_) =>
          println("Success to upload.")
        case Left(e) =>
          e.printStackTrace()
          scala.sys.exit(1)
      }

      TaskResult.empty(request)
    }
  }
}
