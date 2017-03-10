package pl.dawid.calculator

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, logRequestResult, pathPrefix, post}
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config
import pl.dawid.calculator.model.{EvaluationResult, ExpressionString, JsonMarshalling}
import pl.dawid.calculator.services.{EvaluationService, ExpressionParser}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


trait Api extends JsonMarshalling {

  val config: Config
  val parser: ExpressionParser
  val evaluationService: EvaluationService
  implicit val executionContext: ExecutionContext

  val routes: Route = {
    logRequestResult("akka-http-test-app") {
      pathPrefix("evaluate") {
        post {
          entity(as[ExpressionString]) { expressionStr =>
            complete {
              parser.parseExpression(expressionStr.expression) match {
                case Success(expr) => evaluationService.evaluateInParallel(expr).map(EvaluationResult)
                case Failure(thr) =>
                  HttpResponse(StatusCodes.BadRequest, entity = "Expression parsing error: " + thr.getMessage)
              }
            }
          }
        }
      }
    }
  }

}
