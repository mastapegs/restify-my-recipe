val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "restify-my-recipe",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.2",
    libraryDependencies += "io.circe" %% "circe-generic" % "0.14.2",
    libraryDependencies += "io.circe" %% "circe-parser" % "0.14.2",
    libraryDependencies += "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M1",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "application.conf"            => MergeStrategy.concat
      case _                             => MergeStrategy.first
    }
  )
