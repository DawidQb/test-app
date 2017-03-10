package pl.dawid.calculator.test

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{Matchers, WordSpec}
import pl.dawid.calculator.Api
import pl.dawid.calculator.model.{EvaluationResult, ExpressionString}
import pl.dawid.calculator.services.{EvaluationService, ExpressionParser}

import scala.concurrent.ExecutionContext


class EvaluationApiTest extends WordSpec with Matchers with ScalatestRouteTest with Api {
  override val config: Config = ConfigFactory.load()
  override val parser: ExpressionParser = new ExpressionParser
  override val evaluationService: EvaluationService = new EvaluationService(parser, config)
  override val executionContext: ExecutionContext = executor


  "Calculator service " should {
    "return correct value for adding" in {
      Post(s"/evaluate", ExpressionString("1.5+2.5")) ~> routes ~> check {
        status shouldBe OK
        responseAs[EvaluationResult] shouldBe EvaluationResult(4)
      }
    }

    "return correct value for more complex expression" in {
      Post(s"/evaluate", ExpressionString("(1-1)*2+3*(1-3+4)+10/2")) ~> routes ~> check {
        status shouldBe OK
        responseAs[EvaluationResult] shouldBe EvaluationResult(11)
      }
    }

    "return correct value if expression is a number" in {
      Post(s"/evaluate", ExpressionString("42")) ~> routes ~> check {
        status shouldBe OK
        responseAs[EvaluationResult] shouldBe EvaluationResult(42)
      }
    }

    "return error if expression cannot be parsed " in {
      Post(s"/evaluate", ExpressionString("12+one")) ~> routes ~> check {
        status shouldBe BadRequest
      }
    }

    "return infinity when dividing by zero" in {
      Post(s"/evaluate", ExpressionString("2/0")) ~> routes ~> check {
        status shouldBe OK
        responseAs[EvaluationResult] shouldBe EvaluationResult(Double.PositiveInfinity)
      }
    }
  }

}