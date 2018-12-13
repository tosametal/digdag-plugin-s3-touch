package io.digdag.plugin.s3_touch

import java.io.{File, FileInputStream}

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.model.{CannedAccessControlList, ObjectMetadata, PutObjectRequest}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

import scala.util.Try

class S3Uploader(
  bucketName: String,
  accessKey: String,
  secretKey: String,
  maybeProxyHost: Option[String] = None,
  maybeProxyPort: Option[Int] = None,
  serviceEndpoint: String,
  region: String
) {
  private def buildS3Client(): AmazonS3 = {
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val clientConf = new ClientConfiguration()
    for {
      proxyHost <- maybeProxyHost
      proxyPort <- maybeProxyPort
    } yield {
      clientConf.setProxyHost(proxyHost)
      clientConf.setProxyPort(proxyPort)
    }
    val endpointConf = new EndpointConfiguration(serviceEndpoint, region)
    AmazonS3ClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withClientConfiguration(clientConf)
      .withEndpointConfiguration(endpointConf)
      .build()
  }

  def upload(flagFile: String): Either[Throwable, Unit] = {
    Try {
      val client = buildS3Client()
      val file = new File(flagFile)
      val fileInputStream = new FileInputStream(file)
      val objectMetadata = new ObjectMetadata()
      objectMetadata.setContentLength(file.length)
      val putRequest = new PutObjectRequest(bucketName, file.getName, fileInputStream, objectMetadata)
      putRequest.setCannedAcl(CannedAccessControlList.PublicRead)
      // アップロード
      client.putObject(putRequest)
      fileInputStream.close()
    } match {
      case scala.util.Success(_) => Right()
      case scala.util.Failure(e) => Left(e)
    }
  }
}
