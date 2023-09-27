package endpoints

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import cats.effect.IO
import java.nio.file.{Files, Paths, StandardOpenOption}
import fs2.Stream
import org.http4s.multipart._
import org.http4s.dsl.io._
import fs2.io.file._
import cats.effect._
import cats.implicits._
import cats.effect.Blocker
import org.http4s.circe._
import io.circe.syntax._
import converter.ParquetConverter.toParquet

object UpDownEndpoints {

  val csvDir = Paths.get("./data/temp_csv")
  val parquetDir = Paths.get("./data/parquet")

  if (!Files.exists(csvDir)) {
    Files.createDirectories(csvDir)
  }

  if (!Files.exists(parquetDir)) {
    Files.createDirectories(parquetDir)
  }

  def updownRoutes(implicit cs: ContextShift[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {

    case req @ POST -> Root / "upload-csv" => 
      req.decode[Multipart[IO]] { m =>
          m.parts.find(_.name.contains("csv")).fold(IO.pure(Response[IO](Status.BadRequest))) { part =>
              Blocker[IO].use { blocker => 
                val filePath = csvDir.resolve(part.filename.getOrElse("uploaded.csv"))
                part.body.through(fs2.io.file.writeAll(filePath, blocker, List(StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
                    .compile
                    .drain
                    .flatMap { _ => 
                      val targetDir = Paths.get(parquetDir.toString(), part.filename.getOrElse("uploaded").replace(".csv", ""))
                      toParquet(filePath, targetDir)
                    }
                    .map(_ => Response[IO](Status.Ok))
              }
          }
      }
    }
}
