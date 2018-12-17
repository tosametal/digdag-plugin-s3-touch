package io.digdag.plugin.s3_touch

import java.io.{File, FileInputStream}

import com.amazonaws.services.s3.model.{CannedAccessControlList, ObjectMetadata, PutObjectRequest}
import com.amazonaws.services.s3.AmazonS3
import scala.sys.process.Process

object S3Uploader {

  def upload(client: AmazonS3, bucketName: String, fileName: String, acl: String): Either[Throwable, Unit] = {
    val tmpFileName = "/tmp/" + fileNameWithoutDirectory(fileName)
    Process(s"touch $tmpFileName") run ()
    val file = new File(tmpFileName)
    val fileInputStream = new FileInputStream(file)
    val objectMetadata = new ObjectMetadata()
    objectMetadata.setContentLength(file.length)
    try {
      val putRequest = new PutObjectRequest(bucketName, fileName, fileInputStream, objectMetadata)
      mapAcl(acl) match {
        case Some(a) => putRequest.setCannedAcl(a)
        case None => throw new IllegalArgumentException(s"No such acl exists: $acl")
      }
      client.putObject(putRequest)
      Process(s"rm $tmpFileName") run ()
      Right(())
    } catch {
      case scala.util.control.NonFatal(e) => Left(e)
    } finally {
      fileInputStream.close()
    }
  }

  private def fileNameWithoutDirectory(fileName: String): String = {
    val array = fileName.split("/")
    if (array.length == 1) array(0)
    else array.last
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
}
