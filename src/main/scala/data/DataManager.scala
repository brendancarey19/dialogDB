package data

import org.apache.spark.sql.DataFrame
import utils.SQLConnector.{get_data, get_schema}
import utils.OpenAiClient
import scala.util.control.Exception
import cats.effect.IO
import scala.annotation.tailrec
import org.http4s.circe.CirceEntityCodec._

object DataManager {
    val client = new OpenAiClient
    val maxRetries = 3

    def run(file: String, query: String): DataFrame = {
        val f = file.dropRight(4)

        val dataSchema = get_schema(f)

        fetchData(f, query, dataSchema)
    }

    private def fetchData(file: String, query: String, schema: String): DataFrame = {
        var retriesLeft = maxRetries
        var retry: Boolean = false

        while (retriesLeft > 0) {
        try {
            val tableQuery = client.run(file, query, schema, retry).unsafeRunSync()
            return get_data(file, tableQuery)
        } catch {
            case ex: Exception =>
            retriesLeft -= 1
            retry = true
            println(s"Error encountered: ${ex.getMessage}. Retrying... ($retriesLeft retries left)")
        }
        }
        throw new RuntimeException("Exceeded maximum number of retries.")
    }
}