package io.digdag.plugin.s3_touch

import io.digdag.client.config.Config
import io.digdag.spi.OperatorContext

object ParamParser {

  def parse(config: Config)(implicit context: OperatorContext): Either[Throwable, Params] = {
    try {
      val s3TouchConfig = config.get("s3_touch", classOf[Config])
      val bucketName = s3TouchConfig.get("bucket_name", classOf[String]).formatSecret
      val accessKey = s3TouchConfig.get("access_key", classOf[String]).formatSecret
      val secretKey = s3TouchConfig.get("secret_key", classOf[String]).formatSecret
      val serviceEndpoint = s3TouchConfig.get("service_endpoint", classOf[String]).formatSecret
      val defaultRegion = s3TouchConfig.get("default_region", classOf[String]).formatSecret
      val maybeProxyHost = s3TouchConfig.getOptional("proxy_host", classOf[String]).toOption.map(_.formatSecret)
      val maybeProxyPort = s3TouchConfig.getOptional("proxy_port", classOf[Int]).toOption
      val acl = s3TouchConfig.get("access_control_list", classOf[String]).formatSecret
      val fileName = config.get("_command", classOf[String])
      Right(Params(bucketName, accessKey, secretKey, acl, serviceEndpoint, defaultRegion, maybeProxyHost, maybeProxyPort, fileName))
    } catch {
      case scala.util.control.NonFatal(e) => Left(e)
    }
  }

  case class Params(
    bucketName: String,
    accessKey: String,
    secretKey: String,
    acl: String,
    serviceEndpoint: String,
    defaultRegion: String,
    maybeProxyHost: Option[String],
    maybeProxyPort: Option[Int],
    fileName: String
  )
}
