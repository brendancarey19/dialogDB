package endpoints

import cats.effect.IO
import org.http4s.circe._
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import data.DataManager
import org.apache.spark.sql.DataFrame
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._

object QueryEndpoints {

  case class Query(file: String, query: String)
  case class ResponseData(result: String) // Adjust as per DataFrame conversion
  
  implicit val queryDecoder: Decoder[Query] = deriveDecoder[Query]
  implicit val queryEncoder: Encoder[Query] = deriveEncoder[Query]
  implicit val responseDataEncoder: Encoder[ResponseData] = deriveEncoder[ResponseData]

  val queryRoutes = HttpRoutes.of[IO] { 

    case req @ POST -> Root / "data" =>
      req.decode[Query] { queryData =>
        // Using DataManager to retrieve data
        val df: DataFrame = DataManager.run(queryData.file, queryData.query)
        
        // Convert the DataFrame to a String (or process as needed)
        val result: String = df.collect().mkString("\n")
        
        // Responding with the result
        Ok(ResponseData(result).asJson)
      }
  } 
}

    // case req @ POST -> Root / "data" =>
    //   req.decode[Query] { queryData =>
    //     val client = new OpenAiClient
    //     client.run(queryData.file, queryData.query).flatMap { resultString =>
    //       println(resultString)
    //       Ok(resultString)
    //     }
    //   }