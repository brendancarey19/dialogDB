package server

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import cats.data.Kleisli
import cats.data.OptionT
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import endpoints.UserEndpoints._
import endpoints.UpDownEndpoints._
import endpoints.QueryEndpoints._
import endpoints.MetadataEndpoints._


object Server extends IOApp {

  val allRoutes = userRoutes <+> updownRoutes <+> queryRoutes <+> metadataRoutes

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(Kleisli((req: Request[IO]) => 
        allRoutes(req).getOrElseF(IO.pure(Response[IO](Status.NotFound)))
      ))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}