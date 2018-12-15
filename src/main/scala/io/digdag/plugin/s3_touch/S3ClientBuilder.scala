package io.digdag.plugin.s3_touch

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import io.digdag.client.config.Config
import io.digdag.spi.OperatorContext

object S3ClientBuilder {

  def build(s3TouchConfig: Config)(implicit context: OperatorContext): Either[Throwable, AmazonS3] = {

    val accessKey = s3TouchConfig.get("access_key", classOf[String]).formatSecret
    val secretKey = s3TouchConfig.get("secret_key", classOf[String]).formatSecret
    val serviceEndpoint = s3TouchConfig.get("service_endpoint", classOf[String]).formatSecret
    val defaultRegion = s3TouchConfig.get("default_region", classOf[String]).formatSecret
    val maybeProxyHost = s3TouchConfig.getOptional("proxy_host", classOf[String]).toOption.map(_.formatSecret)
    val maybeProxyPort = s3TouchConfig.getOptional("proxy_port", classOf[Int]).toOption

    try {
      val credentials = new BasicAWSCredentials(accessKey, secretKey)
      val clientConf = new ClientConfiguration()
      for {
        proxyHost <- maybeProxyHost
        proxyPort <- maybeProxyPort
      } yield {
        clientConf.setProxyHost(proxyHost)
        clientConf.setProxyPort(proxyPort)
      }
      val endpointConf = new EndpointConfiguration(serviceEndpoint, defaultRegion)

      val client = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withClientConfiguration(clientConf)
        .withEndpointConfiguration(endpointConf)
        .build()
      Right(client)
    } catch {
      case scala.util.control.NonFatal(e) => Left(e)
    }
  }
}
