package endpoints

import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import org.http4s.circe._
import io.circe.generic.semiauto._
import org.http4s.circe.CirceEntityCodec._
import io.circe.{Decoder, Encoder}

import utils.OpenAiClient

object QueryEndpoints {

  case class Query(file: String, query: String)

  implicit val queryDecoder: Decoder[Query] = deriveDecoder[Query]
  implicit val queryEncoder: Encoder[Query] = deriveEncoder[Query]

  val queryRoutes = HttpRoutes.of[IO] { 

    case req @ POST -> Root / "data" =>
      req.decode[Query] { queryData =>
        val client = new OpenAiClient
        client.run(queryData.file, queryData.query).flatMap { resultString =>
          Ok(resultString)
        }
      } 
    } 
}
