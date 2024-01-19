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

case class Format(
    `type`: String,
    delimiter: String,
    headers: Boolean,
    query: String
)
case class Stream(`type`: String, path: String, format: Format)

object RestifyRecipe {
  def main(args: Array[String]) = {
    args.headOption.foreach({ arg =>
      val yamlString = Source.fromFile(arg).getLines.mkString("\n")
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
    })
  }
}
