package endpoints

import scala.collection.mutable.ListBuffer
import java.nio.file.{Files, Path => NPath, Paths => NPaths}

import java.util.stream.Collectors
import scala.jdk.CollectionConverters._
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import io.circe._
import io.circe.syntax._
import org.http4s.circe._
import scala.util.Using


object MetadataEndpoints {

  def get_files(rawDir: NPath, parquetDir: NPath): ListBuffer[String] = {

    val rawList = ListBuffer.empty[String]
    val parquetList = ListBuffer.empty[String]


    parquetList ++= Files.list(parquetDir)
      .filter(path => Files.isDirectory(path))
      .map(_.toString)
      .collect(Collectors.toList())
      .asScala

    val stream = Files.list(rawDir)

    Using(Files.list(rawDir)) { stream =>
      rawList ++= stream
        .filter(Files.isRegularFile(_))
        .map(_.toString)
        .collect(Collectors.toList[String]())
        .asScala
    }.getOrElse(ListBuffer.empty) 


    val baseCSV = rawList.map(path => path.split("/").last.dropRight(4))
    val baseParquet = parquetList.map(path => path.split("/").last)

    println(baseCSV)
    println(baseParquet)

    val intersection = baseCSV.intersect(baseParquet)
    intersection.map(_ + ".csv")
  }


  val metadataRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "metadata" / "get-files" =>
      val response = get_files(NPaths.get("./data/", "temp_csv"), NPaths.get("./data/", "parquet"))
      Ok(response.toList.asJson)
  }

}
