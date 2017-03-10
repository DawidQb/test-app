package pl.dawid.calculator

import scala.util.Try
import scala.util.parsing.combinator._


class ExpressionParser extends JavaTokenParsers{

  private def expr: Parser[Expression] = term~rep("+"~term | "-"~term) ^^ {
    case t ~ ts => ts.foldLeft(t) {
      case (t1, "+" ~ t2) => BinaryExpression(t1, t2, _+_, getGreaterDepth(t1, t2)+1)
      case (t1, "-" ~ t2) => BinaryExpression(t1, t2, _-_, getGreaterDepth(t1, t2)+1)
    }
  }

  private def term: Parser[Expression] = factor~rep("*"~factor | "/"~factor) ^^ {
    case t ~ ts => ts.foldLeft(t) {
      case (t1, "*" ~ t2) => BinaryExpression(t1, t2, _*_, getGreaterDepth(t1, t2)+1)
      case (t1, "/" ~ t2) => BinaryExpression(t1, t2, _/_, getGreaterDepth(t1, t2)+1)
    }
  }

  private def factor: Parser[Expression] = floatingPointNumber ^^ (x => Constant(x.toDouble)) |
    "("~>expr<~")"

  private def getGreaterDepth(ex1: Expression, ex2: Expression): Int = {
    ex1 match {
      case Constant(_) =>
        ex2 match {
          case Constant(_) => 0
          case BinaryExpression(_,_,_,depth2) => depth2
        }
      case BinaryExpression(_,_,_,depth1) =>
        ex2 match {
          case Constant(_) => depth1
          case BinaryExpression(_,_,_,depth2) => List(depth1, depth2).max
        }
    }
  }

  def parseExpression(input: String): Try[Expression] = parseAll(expr, input) match {
    case Success(result, _) =>  scala.util.Success(result)
    case NoSuccess(msg, _) => scala.util.Failure(ParsingException(msg))
  }

}

sealed trait Expression
case class Constant(value: Double) extends Expression
case class BinaryExpression(ex1: Expression, ex2: Expression, op: (Double, Double) => Double, depth: Int = 0) extends Expression

