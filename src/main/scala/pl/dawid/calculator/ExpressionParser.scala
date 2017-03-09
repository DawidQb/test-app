package pl.dawid.calculator

class ExpressionParser {





}

sealed trait Expr
sealed trait Op
case class Constant(value: Double)
case class Expression(ex1: Expr, ex2: Expr, op: Op) extends Expr

case class Add() extends Op