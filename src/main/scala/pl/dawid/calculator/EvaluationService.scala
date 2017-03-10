package pl.dawid.calculator

import scala.concurrent.{ExecutionContext, Future}

class EvaluationService(private val parser: ExpressionParser)
                       (implicit val ec: ExecutionContext){

  private val rootId = Id("root")

  private def getUniqueId: Id = Id(java.util.UUID.randomUUID.toString)

  private def evaluate(expr: Expression): Double = {
    expr match {
      case Constant(c) => c
      case BinaryExpression(ex1, ex2, op, _) => op(evaluate(ex1), evaluate(ex2))
    }
  }

  private def splitByDepth(expression: Expression, maxDepth: Int) = {
    require(maxDepth > 0)

    def split(expression: Expression, myId: Id, splitInfo: SplitInfo): SplitInfo = {
      expression match {
        case Constant(_) => splitInfo.updateExpressions(Map(myId -> expression))
        case BinaryExpression(_, _, _, depth) if depth <= maxDepth =>
          splitInfo.updateExpressions(Map(myId -> expression))
        case BinaryExpression(ex1, ex2, op, depth) =>
          val childId1 = getUniqueId
          val childId2 = getUniqueId
          val updatedSplitInfo = splitInfo.updateChildren(Map(myId->(childId1, childId2))).
            updateOperators(Map(myId->op))
          split(ex1, childId1, updatedSplitInfo).merge(split(ex2, childId2, updatedSplitInfo))
      }
    }

    split(expression, rootId, SplitInfo(Map(), Map(), Map()))
  }

  private def evaluateMapInParallel(expressionMap: Map[Id, Expression]): Map[Id, Future[Double]] = {
    expressionMap map {case (id, ex) => (id, Future(evaluate(ex)))}
  }

  private def mergeValues(splitInfo: SplitInfo, valuesMap: Map[Id, Future[Double]]): Future[Double] = {
    def mergeAndGetValue(id: Id): Future[Double] = {
      if (valuesMap.keys.exists(_ == id)) {
        valuesMap(id)
      } else {
        val (childId1, childId2) = splitInfo.childrenMap(id)
        val op = splitInfo.operatorsMap(id)
        val childVal1 = valuesMap.getOrElse(childId1, mergeAndGetValue(childId1))
        val childVal2 = valuesMap.getOrElse(childId2, mergeAndGetValue(childId2))
        for {
          val1 <- childVal1
          val2 <- childVal2
        } yield op(val1, val2)
      }
    }

    mergeAndGetValue(rootId)
  }

  def evaluateInParallel(expression: Expression, maxDepth: Int): Future[Double] = {
    val splitResult = splitByDepth(expression, maxDepth)
    val valuesMap = evaluateMapInParallel(splitResult.expressionsMap)
    mergeValues(splitResult, valuesMap)
  }

}
