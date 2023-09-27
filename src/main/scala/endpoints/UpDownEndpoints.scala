package endpoints

import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

object UpDownEndpoints {

  val updownRoutes = HttpRoutes.empty[IO]

}
