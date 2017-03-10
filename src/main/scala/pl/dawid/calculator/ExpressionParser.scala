package pl.dawid.calculator

import scala.util.parsing.combinator._

class ExpressionParser extends JavaTokenParsers{

  private def expr: Parser[Expr] = term~rep("+"~term | "-"~term) ^^ {
    case t ~ ts => ts.foldLeft(t) {
      case (t1, "+" ~ t2) => Expression(t1, t2, _+_, getGreaterDepth(t1, t2)+1)
      case (t1, "-" ~ t2) => Expression(t1, t2, _-_, getGreaterDepth(t1, t2)+1)
    }
  }

  private def term: Parser[Expr] = factor~rep("*"~factor | "/"~factor) ^^ {
    case t ~ ts => ts.foldLeft(t) {
      case (t1, "*" ~ t2) => Expression(t1, t2, _*_, getGreaterDepth(t1, t2)+1)
      case (t1, "/" ~ t2) => Expression(t1, t2, _/_, getGreaterDepth(t1, t2)+1)
    }
  }

  private def factor: Parser[Expr] = floatingPointNumber ^^ (x => Constant(x.toDouble)) |
    "("~>expr<~")"

  def evaluate(expr: Expr): Double = {
    expr match {
      case Constant(c) => c
      case Expression(ex1, ex2, op, _) => op(evaluate(ex1), evaluate(ex2))
    }
  }

  private def getGreaterDepth(ex1: Expr, ex2: Expr): Int = {
    ex1 match {
      case Constant(_) =>
        ex2 match {
          case Constant(_) => 0
          case Expression(_,_,_,depth2) => depth2
        }
      case Expression(_,_,_,depth1) =>
        ex2 match {
          case Constant(_) => depth1
          case Expression(_,_,_,depth2) => List(depth1, depth2).max
        }
    }
  }


  def parseExpression(input: String): Option[Any] = parseAll(expr, input) match {
    case Success(result, _) => println("!!!! "*5 + evaluate(result)); Some(result)
    case NoSuccess(_, _) => None
  }

}

sealed trait Expr

case class Constant(value: Double) extends Expr
case class Expression(ex1: Expr, ex2: Expr, op: (Double, Double) => Double, depth: Int = 0) extends Expr

