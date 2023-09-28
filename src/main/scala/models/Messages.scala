package models

import io.circe.syntax._
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets
import io.circe.parser._
import scala.collection.mutable.ListBuffer
import play.api.libs.json._
import io.circe.generic.auto._
import scala.util.Try

object Messages {

    case class Message(role: String, content: String)
    case class Choice(finish_reason: String, index: Int, message: Message)
    case class Usage(prompt_tokens: Int, completion_tokens: Int, total_tokens: Int)
    case class Response(choices: List[Choice], usage: Usage)

    implicit val messageFormat: Format[Message] = Json.format[Message]

    def prepareMessages(messages: ListBuffer[Message]): String = {
        Json.toJson(messages).toString()
    }

    def initMessages(file: String, schema: String): Unit = {
        val dirPath = s"./data/$file"
        val directory = Paths.get(dirPath)
        if (!Files.exists(directory)) {
            Files.createDirectories(directory)
            val messages = ListBuffer[Message]()

            val sys_message = new Message(role = "system", content="You are a helpful assistant who only responds with Spark SQL compliant queries.")
            val user_instr = new Message(role = "user", content="You are a helpful assistant who only responds with Spark SQL compliant queries. When the user asks to show them something in their table, respond with nothing but a Spark SQL query.")
            val schema_instr = new Message(role = "user", content=s"The schema of my Spark SQL table (table name = $file) is as follows: $schema")

            messages ++= Seq(sys_message, user_instr, schema_instr)

            storeMessages(messages, file)
        }

    }

    def storeMessages(messages: ListBuffer[Message], file: String): Unit = {
        val filePath = s"./data/$file/logs.txt"
        val jsonString = messages.toList.asJson.noSpaces
        Files.write(Paths.get(filePath), jsonString.getBytes(StandardCharsets.UTF_8))
    }


    def retrieveMessages(file: String): ListBuffer[Message] = {
        val dirPath = s"./data/$file"
        val directory = Paths.get(dirPath)
        if (!Files.exists(directory)) {
            Files.createDirectories(directory)
        }

        val filePath = s"$dirPath/logs.txt"
        val path = Paths.get(filePath)

        if (Files.exists(path) && Files.size(path) > 0) {
            val jsonString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
            decode[ListBuffer[Message]](jsonString) match {
                case Right(messages) => messages
                case Left(_) => ListBuffer.empty[Message]
            }
        } else {
            ListBuffer.empty[Message]
        }
    }

}