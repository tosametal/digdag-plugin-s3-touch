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
    override def runTask(): TaskResult = {
      println("s3_touch start")

      val params = request.getConfig.mergeDefault(request.getConfig.getNestedOrGetEmpty("s3_touch"))

      val s3TouchConfig = params.get("s3_touch", classOf[Config])

      val bucketName = formatSecret(s3TouchConfig.get("bucket_name", classOf[String]))
      val accessKey = formatSecret(s3TouchConfig.get("access_key", classOf[String]))
      val secretKey = formatSecret(s3TouchConfig.get("secret_key", classOf[String]))
      val defaultRegion = formatSecret(s3TouchConfig.get("default_region", classOf[String]))
      val flagFile = params.get("_command", classOf[String])
      val maybeProxyHost = params.getOptional("proxy_host", classOf[String]).toOption
      val maybeProxyPort = params.getOptional("proxy_port", classOf[Int]).toOption

      println(
        s"""
          |bucketName = $bucketName
          |accessKey = $accessKey
          |secretKey = $secretKey
          |defaultRegion = $defaultRegion
          |flagFile = $flagFile
          |maybeProxyHost = $maybeProxyHost
          |maybeProxyPort = $maybeProxyPort
        """.stripMargin)

      TaskResult.empty(request)
    }


    private def formatSecret(str: String): String = {
      UserSecretTemplate.of(str).format(context.getSecrets)
    }
  }
}