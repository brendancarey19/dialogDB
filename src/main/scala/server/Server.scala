package server

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import endpoints.UserEndpoints._
import endpoints.UpDownEndpoints._
import endpoints.QueryEndpoints._


object Server extends IOApp {

  val allRoutes = userRoutes <+> updownRoutes <+> queryRoutes

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(Router("/" -> allRoutes).orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}