package io.digdag.plugin.s3_touch

import java.util.{ Arrays => JArrays, List => JList }

import io.digdag.spi.{OperatorFactory, OperatorProvider, Plugin}

object S3TouchPlugin {
  class S3TouchOperatorProvider extends OperatorProvider {
    override def get(): JList[OperatorFactory] = {
      JArrays.asList(new S3TouchOperatorFactory)
    }
  }

}

class S3TouchPlugin extends Plugin {
  override def getServiceProvider[T](aClass: Class[T]): Class[_ <: T] = {
    if (aClass ne classOf[OperatorProvider]) {
      null
    } else {
      classOf[S3TouchPlugin.S3TouchOperatorProvider].asSubclass(aClass)
    }
  }
}
