package endpoints

import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

object QueryEndpoints {

  val queryRoutes = HttpRoutes.empty[IO]

}
