package io.digdag.plugin.s3_touch

import io.digdag.spi.{Operator, OperatorContext, OperatorFactory, TaskResult}
import io.digdag.util.BaseOperator

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
      TaskResult.empty(request)
    }
  }
}