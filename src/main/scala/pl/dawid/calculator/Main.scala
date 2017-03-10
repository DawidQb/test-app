package pl.dawid.calculator

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, entity, logRequestResult, pathPrefix, post}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.duration._


object Main extends App with Json4sSupport{

  val config = ConfigFactory.load()

  implicit val system = ActorSystem("test-app")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  implicit val formats = DefaultFormats
  implicit val jacksonSerialization = jackson.Serialization

  implicit val timeout = Timeout(config.getDuration("app.timeout").toMillis.millis)

  val parser = new ExpressionParser
  val evaluationService = new EvaluationService(parser)

  val routes = {
    logRequestResult("akka-http-test-app") {
      pathPrefix("evaluate") {
        post {
          entity(as[ExpressionString]) { expressionStr =>
            complete {
              val expr = parser.parseExpression(expressionStr.expression).getOrElse(Constant(Double.NaN))
              val maxDepth = config.getInt("app.maxDepth")
              evaluationService.evaluateInParallel(expr, maxDepth).map(EvaluationResult)
            }
          }
        }
      }
    }
  }

  Http().bindAndHandle(routes, config.getString("app.host"), config.getInt("app.port"))

}
