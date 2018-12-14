package io.digdag.plugin.s3_touch

import io.digdag.client.config.Config
import io.digdag.spi.{Operator, OperatorContext, OperatorFactory, TaskResult}
import io.digdag.util.BaseOperator
import io.digdag.util.UserSecretTemplate

class S3TouchOperatorFactory extends OperatorFactory {
  override def getType: String = "s3_touch"

  override def newOperator(context: OperatorContext): Operator = {
    import S3TouchOperatorFactory._
    new S3TouchOperator(context)
  }
}

object S3TouchOperatorFactory {

  private class S3TouchOperator(context: OperatorContext) extends BaseOperator(context) {
    private def formatSecret(str: String): String = UserSecretTemplate.of(str).format(context.getSecrets)

    override def runTask(): TaskResult = {
      val params = request.getConfig.mergeDefault(request.getConfig.getNestedOrGetEmpty("s3_touch"))
      val s3TouchConfig = params.get("s3_touch", classOf[Config])

      val bucketName = formatSecret(s3TouchConfig.get("bucket_name", classOf[String]))
      val accessKey = formatSecret(s3TouchConfig.get("access_key", classOf[String]))
      val secretKey = formatSecret(s3TouchConfig.get("secret_key", classOf[String]))
      val serviceEndpoint = formatSecret(s3TouchConfig.get("service_endpoint", classOf[String]))
      val defaultRegion = formatSecret(s3TouchConfig.get("default_region", classOf[String]))
      val flagFile = params.get("_command", classOf[String])
      val maybeProxyHost = params.getOptional("proxy_host", classOf[String]).toOption
      val maybeProxyPort = params.getOptional("proxy_port", classOf[Int]).toOption
      val accessControlList = formatSecret(params.get("access_control_list", classOf[String]))

      val uploader =
        new S3Uploader(bucketName, accessKey, secretKey, maybeProxyHost, maybeProxyPort, serviceEndpoint, defaultRegion, accessControlList)

      uploader.upload(flagFile) match {
        case Right(_) => println("success to upload")
        case Left(e) => e.printStackTrace()
      }

      TaskResult.empty(request)
    }
  }
}
