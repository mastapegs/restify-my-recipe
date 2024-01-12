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

object RestifyRecipe {
  def main(args: Array[String]) = {
    if (args.length > 0) {
      val yamlString = Source.fromFile(args(0)).getLines.mkString("\n")
      val yamlJson: Either[ParsingFailure, Json] = parser.parse(yamlString)

      yamlJson.foreach({ json =>
        json.hcursor
          .downField("ingestStreams")
          .as[List[Json]]
          .foreach(_.foreach({ stream =>
            println(stream)
          }))
      })
    } else {
      println("Please provide a YAML file path as an argument.")
    }
  }
}
