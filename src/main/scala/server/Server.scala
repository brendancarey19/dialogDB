package com.dialogdb

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.CirceEntityDecoder._
import cats.implicits._

object Server extends IOApp {

  case class UserRegistration(name: String, age: Int)
  case class UserInfo(id: Long, name: String, age: Int)

  val userRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name!")

    case GET -> Root / "user" / LongVar(userId) =>
      Ok(UserInfo(userId, "John", 25))

    case req @ POST -> Root / "register" =>
      req.decode[UserRegistration] { userData =>
        Ok(s"Registered user ${userData.name} with age ${userData.age}")
      }

    case GET -> Root / "user" / LongVar(userId) if userId <= 0 =>
      BadRequest("User ID should be positive")
  }

  val productRoutes = HttpRoutes.empty[IO]

  val allRoutes = userRoutes <+> productRoutes

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(Router("/" -> allRoutes).orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
