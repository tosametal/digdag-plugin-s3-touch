package io.digdag.plugin.s3_touch

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

object S3ClientBuilder {

  def build(
    accessKey: String,
    secretKey: String,
    maybeProxyHost: Option[String],
    maybeProxyPort: Option[Int],
    serviceEndpoint: String,
    defaultRegion: String
  ): Either[Throwable, AmazonS3] = {

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
