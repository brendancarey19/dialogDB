package utils

import scala.collection.mutable.ListBuffer
import cats.effect.IO
import scalaj.http.{Http, HttpOptions}
import io.circe.generic.auto._
import io.circe.parser._
import models.Messages.{Message, Choice, Usage, Response, prepareMessages, storeMessages, retrieveMessages, initMessages}

class OpenAiClient {

    def run(file: String, query: String, schema: String, failure_rerun: Boolean = false): IO[String] = IO {
        val f = file

        initMessages(f, schema)

        val prompt_msg = retrieveMessages(f)

        if (failure_rerun) {
            prompt_msg += createErrorPrompt()
        } else {
            prompt_msg += createPrompt(query)
        }

        storeMessages(prompt_msg, f)

        val complete_messages = prepareMessages(prompt_msg)

        println(complete_messages)

        val key = System.getenv("OAI_KEY")

        val response = Http("https://api.openai.com/v1/chat/completions").postData(s"""{"messages": $complete_messages, "model": "gpt-3.5-turbo"}""")
            .header("Content-Type", "application/json")
            .header("Authorization", s"Bearer $key")
            .header("Charset", "UTF-8")
            .option(HttpOptions.readTimeout(10000)).asString

        println(response.body)
        extractMessage(response.body)
        
    }

    def createPrompt(prompt: String): Message = {
        val full_prompt = s"""Respond with nothing but a Spark SQL query for the previously specified table, corresponding to this statement: $prompt"""
        new Message(role = "user", content=full_prompt)
    }

    def createErrorPrompt(): Message = {
        val error_prompt = s"""The Spark SQL statement you responded to the last prompt was invalid. Please try again, responding with nothing other than a Spark SQL statement."""
        new Message(role = "user", content=error_prompt)
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