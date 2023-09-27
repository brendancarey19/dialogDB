package endpoints

import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.CirceEntityDecoder._
import utils.SparkManager
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{StructType, StructField, StringType}
import utils.OpenAiClient

object UserEndpoints {

  case class UserRegistration(name: String, age: String)
  case class UserInfo(id: Long, name: String, age: Int)

  val userRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      val spark = SparkManager.spark

      // Create a DataFrame with a single column named "name"
      val schema = StructType(Seq(StructField("name", StringType)))
      val data = Seq(Row(name))
      val nameDataFrame: DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(data), schema)

      // Print the schema and show the DataFrame
      nameDataFrame.printSchema()
      nameDataFrame.show()

      Ok(s"Hello, $name!")

    case GET -> Root / "user" / userId =>
      val client = new OpenAiClient()
      client.run("Show the oldest person in my table").flatMap { response =>
        Ok(UserRegistration("Show the oldest person in my table", response))
      }

    case req @ POST -> Root / "register" =>
      req.decode[UserRegistration] { userData =>
        Ok(s"Registered user ${userData.name} with age ${userData.age}")
      }

    case GET -> Root / "user" / LongVar(userId) if userId <= 0 =>
      BadRequest("User ID should be positive")
  }
}
