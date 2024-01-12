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

      yamlJson match {
        case Left(failure) =>
          println(s"Failed to parse YAML: ${failure.getMessage}")
        case Right(json) => println(s"Parsed YAML: $json")
      }
    } else {
      println("Please provide a YAML file path as an argument.")
    }
  }
}
