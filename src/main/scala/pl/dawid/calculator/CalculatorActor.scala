package pl.dawid.calculator

import akka.actor.Actor



class CalculatorActor(parser: ExpressionParser) extends Actor{

  private def generateUniqueId(): String = Math.random().toString //TODO make this really unique

  override def receive: Receive = {
    case ExpressionString(expression) =>
      val id = generateUniqueId()
      Thread.sleep(100)
      sender ! "Response: " + parser.parseExpression(expression).toString
  }
}
