package RestifyRecipe

import org.yaml.snakeyaml.Yaml
import scala.io.Source
import java.io.InputStream
import scala.collection.JavaConverters._

import scala.io.Source
import java.io.InputStream
import scala.collection.JavaConverters._
import io.circe._
import io.circe.yaml.parser
import io.circe.parser._
import sttp.client4.quick._
import sttp.client4.Response

import io.circe.generic.auto._

import io.circe.generic.auto._
import io.circe.syntax._

case class Format(
    `type`: String,
    delimiter: String,
    headers: Boolean,
    query: String
)
case class Stream(`type`: String, path: String, format: Format)

object RestifyRecipe {
  def main(args: Array[String]) = {
    if (args.length > 0) {
      val yamlString = Source.fromFile(args(0)).getLines.mkString("\n")
      val yamlJson: Either[ParsingFailure, Json] = parser.parse(yamlString)

      yamlJson.foreach({ json =>
        json.hcursor
          .downField("ingestStreams")
          .as[List[Json]]
          .foreach(_.zipWithIndex.foreach({ case (streamJson, index) =>
            decode[Stream](streamJson.noSpaces).foreach({ stream =>
              println(index + 1)
              println(stream)
              println("-----")
              // val response = quickRequest
              //   .post(uri"http://localhost:8080/api/v1/ingest")
              //   .header("Content-Type", "application/json")
              //   .body()
              //   .send()
              val response = quickRequest
                .post(
                  uri"http://localhost:8080/api/v1/ingest/INGEST-${index + 1}"
                )
                .header("Content-Type", "application/json; charset=utf-8")
                .body(stream.asJson.noSpaces)
                .send()
            })
          }))
      })
    } else {
      println("Please provide a YAML file path as an argument.")
    }
  }
}
