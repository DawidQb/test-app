package pl.dawid.calculator.model

import pl.dawid.calculator.services.Expression


case class SplitInfo(
                      childrenMap: Map[Id, (Id, Id)],
                      operatorsMap: Map[Id, (Double, Double) => Double],
                      expressionsMap: Map[Id, Expression]
                    ){
  def updateChildren(newChildren: Map[Id, (Id, Id)]): SplitInfo = copy(childrenMap = childrenMap ++ newChildren)
  def updateOperators(newOperators: Map[Id, (Double, Double) => Double]): SplitInfo =
    copy(operatorsMap = operatorsMap ++ newOperators)
  def updateExpressions(newExpressions: Map[Id, Expression]): SplitInfo =
    copy(expressionsMap = expressionsMap ++ newExpressions)
  def merge(that: SplitInfo) = SplitInfo(
    childrenMap = this.childrenMap ++ that.childrenMap,
    operatorsMap = this.operatorsMap ++ that.operatorsMap,
    expressionsMap = this.expressionsMap ++ that.expressionsMap
  )
}