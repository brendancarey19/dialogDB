name := "ScalaBackend"

version := "0.1"

scalaVersion := "2.13.8"

val http4sVersion = "1.0.0-M27"
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.apache.spark" %% "spark-core" % "3.2.0",
  "org.apache.spark" %% "spark-sql" % "3.2.0",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)