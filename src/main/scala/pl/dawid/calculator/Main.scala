package pl.dawid.calculator

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import pl.dawid.calculator.services.{EvaluationService, ExpressionParser}


object Main extends App with Api{

  val config = ConfigFactory.load()

  implicit val system = ActorSystem("test-app")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val parser = new ExpressionParser
  val evaluationService = new EvaluationService(parser, config)

  Http().bindAndHandle(routes, config.getString("app.host"), config.getInt("app.port"))

}
