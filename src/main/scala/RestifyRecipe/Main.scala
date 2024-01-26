package RestifyRecipe

import io.circe._
import io.circe.yaml.parser
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.collection.JavaConverters._
import scala.io.Source
import scala.collection.JavaConverters._

import org.yaml.snakeyaml.Yaml

import java.io.InputStream

import sttp.client4.quick._
import sttp.client4.Response

import cats.implicits._

case class Format(
    `type`: String,
    delimiter: Option[String],
    headers: Option[Boolean],
    query: String
)
case class Stream(`type`: String, path: String, format: Option[Format])

object RestifyRecipe {
  def main(args: Array[String]) = {
    val optArg = args.headOption

    optArg match
      case None => println("Need to enter a recipe path as an arg")

      case Some(arg) => {
        val yamlString = Source.fromFile(arg).getLines.mkString("\n")
        val yamlJson: Either[ParsingFailure, Json] = parser.parse(yamlString)

        val result = yamlJson
          .flatMap(_.hcursor.downField("ingestStreams").as[List[Json]])
          .flatMap(_.traverse(json => decode[Stream](json.noSpaces)))

        result match
          case Left(error) => {
            println("An error occured")
            println(error)
          }
          case Right(ingestStreams) =>
            ingestStreams.zipWithIndex.foreach({ case (stream, index) =>
              println(index + 1)
              println(stream)
              println("-----")
              val response = quickRequest
                .post(
                  uri"http://localhost:8080/api/v1/ingest/INGEST-${index + 1}"
                )
                .header("Content-Type", "application/json; charset=utf-8")
                .body(stream.asJson.noSpaces)
                .send()
            })
      }
  }
}
