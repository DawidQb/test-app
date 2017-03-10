name := "test-app"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

val akkaHttpVersion = "10.0.4"
val akkaVersion = "2.4.17"
val scalaTestVersion = "3.0.1"
val json4sVersion = "3.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http"  % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "org.scalatest"     %% "scalatest" % scalaTestVersion % "test",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.12.0",
  "org.json4s"        %% "json4s-native" % json4sVersion,
  "org.json4s"        %% "json4s-ext" % json4sVersion
)
