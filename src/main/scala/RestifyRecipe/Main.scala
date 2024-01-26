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
import scala.util.Try
import scala.util.Failure
import scala.util.Success

case class Format(
    `type`: String,
    delimiter: Option[String],
    headers: Option[Boolean],
    query: String
)
case class Stream(`type`: String, path: String, format: Option[Format])

object RestifyRecipe {
  def main(args: Array[String]) = {
    args.headOption match
      case None => {
        println("-----")
        println("Need to enter a recipe path as an arg")
        println("-----")
      }

      case Some(arg) => {
        Try(Source.fromFile(arg)) match
          case Failure(exception) => {
            println("-----")
            println("An error occured")
            println(exception)
            println("-----")
          }
          case Success(source) => {
            val maybeIngestStreams = parser
              .parse(source.getLines.mkString("\n"))
              .flatMap(_.hcursor.downField("ingestStreams").as[List[Json]])
              .flatMap(_.traverse(json => decode[Stream](json.noSpaces)))

            maybeIngestStreams match
              case Left(error) => {
                println("-----")
                println("An error occured")
                println(error)
                println("-----")
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
}
