package io.digdag.plugin.s3_touch

import java.io.{File, FileInputStream}

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.model.{CannedAccessControlList, ObjectMetadata, PutObjectRequest}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

class S3Uploader(
  bucketName: String,
  accessKey: String,
  secretKey: String,
  maybeProxyHost: Option[String] = None,
  maybeProxyPort: Option[Int] = None,
  serviceEndpoint: String,
  region: String,
  accessControlList: String
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
    AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withClientConfiguration(clientConf)
      .withEndpointConfiguration(endpointConf)
      .build()
  }

  private def mapAcl(acl: String): Option[CannedAccessControlList] = acl match {
    case "private" => Some(CannedAccessControlList.Private)
    case "public-read" => Some(CannedAccessControlList.PublicRead)
    case "public-read-write" => Some(CannedAccessControlList.PublicReadWrite)
    case "authenticated-read" => Some(CannedAccessControlList.AuthenticatedRead)
    case "log-delivery-write" => Some(CannedAccessControlList.LogDeliveryWrite)
    case "bucket-owner-read" => Some(CannedAccessControlList.BucketOwnerRead)
    case "bucket-owner-full-control" => Some(CannedAccessControlList.BucketOwnerFullControl)
    case "aws-exec-read" => Some(CannedAccessControlList.AwsExecRead)
    case _ => None
  }

  private def getFileName(flagFile: String): String = {
    val array = flagFile.split("/")
    if (array.length == 1) array.head
    else array.last
  }

  def upload(flagFile: String): Either[Throwable, Unit] = {
    import scala.sys.process.Process

    val fileName = getFileName(flagFile)
    val tmpFileName = "/tmp/" + fileName

    Process(s"touch $tmpFileName") run ()

    val file = new File(tmpFileName)
    val fileInputStream = new FileInputStream(file)
    val objectMetadata = new ObjectMetadata()
    objectMetadata.setContentLength(file.length)
    val putRequest = new PutObjectRequest(bucketName, flagFile, fileInputStream, objectMetadata)

    val client = buildS3Client()
    try {
      mapAcl(accessControlList) match {
        case Some(acl) => putRequest.setCannedAcl(acl)
        case None => throw new IllegalArgumentException(s"No such acl exists: $accessControlList")
      }
      // アップロード
      client.putObject(putRequest)
      Process(s"rm $tmpFileName") run ()
      Right(())
    } catch {
      case scala.util.control.NonFatal(e) => Left(e)
    } finally {
      fileInputStream.close()
    }
  }
}
