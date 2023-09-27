package utils

import scala.collection.mutable.ListBuffer
import cats.effect.IO
import scalaj.http.{Http, HttpOptions}
import io.circe.generic.auto._
import io.circe.parser._

class OpenAiClient {

    case class Message(content: String, role: String)
    case class Choice(finish_reason: String, index: Int, message: Message)
    case class Usage(prompt_tokens: Int, completion_tokens: Int, total_tokens: Int)
    case class Response(choices: List[Choice], usage: Usage)

    val messages = ListBuffer[String]()

    val sys_message = """{"role": "system", "content": "You are a helpful assistant who only responds with Spark SQL compliant queries."}"""
    val user_instr = """{"role": "user", "content": "You are a helpful assistant who only responds with Spark SQL compliant queries. When the user asks to show them something in their table, respond with nothing but a Spark SQL query."}"""
    val schema_instr = """{"role": "user", "content": "The schema of my Spark SQL table (table name = test_table) is as follows: name(str), age(int) "}"""

    messages ++= Seq(sys_message, user_instr, schema_instr)

    def run(userId: String): IO[String] = IO {
        
        val prompt_msg = messages
        prompt_msg += createPrompt(userId)

        val complete_messages = unpackListBuffer(prompt_msg)

        println(complete_messages)

        val response = Http("https://api.openai.com/v1/chat/completions").postData(s"""{"messages": $complete_messages, "model": "gpt-3.5-turbo"}""")
            .header("Content-Type", "application/json")
            .header("Authorization", s"Bearer $System.getenv("OAI_KEY")")
            .header("Charset", "UTF-8")
            .option(HttpOptions.readTimeout(10000)).asString

        println(response.body)
        extractMessage(response.body)
        
    }

    def createPrompt(prompt: String): String = {
        s"""{"role": "user", "content": "Respond with nothing but a Spark SQL query for the previously specified table, corresponding to this statement: $prompt "} """
    }

    def unpackListBuffer(buf: ListBuffer[String]): String = {
        buf.mkString("[", ", ", "]")
    }

    def extractMessage(jsonStr: String): String = {
        decode[Response](jsonStr) match {
            case Right(response) => 
                response.choices.headOption.map(choice => choice.message.content).getOrElse("Failed to extract message")
            case Left(error) => 
                println(s"Decoding error: ${error.getMessage}")
                "Failed to decode response"
        }
    }

}