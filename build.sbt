import sbt.Keys.version
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.webpack

import java.nio.file.Files

ThisBuild / scalaVersion := "2.13.10"

lazy val myovercast = crossProject(JVMPlatform, JSPlatform)
  .settings(
    name := "MyOvercast",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "1.5.0"
    )
  )

lazy val backend = project.in(file("jvm"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.8.1",
      "com.lihaoyi" %% "requests" % "0.7.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0")
  ).dependsOn(myovercast.jvm)

lazy val build = TaskKey[Unit]("build")
lazy val frontend = project.in(file("js"))
  .settings(
    scalacOptions += "-Ymacro-annotations",
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "@types/react" -> "16.9.42",
      "@types/react-dom" -> "16.9.8",
      "react-proxy" -> "1.1.8",
      // "@types/prop-types" -> "15.7.3", 
      "antd" -> "4.9.4"),
    Compile / npmDevDependencies ++= Seq(
      "file-loader" -> "6.2.0",
      "style-loader" -> "2.0.0",
      "css-loader" -> "5.2.6",
      "html-webpack-plugin" -> "4.5.1",
      "copy-webpack-plugin" -> "6.4.0",
      "webpack-merge" -> "5.8.0"),
    stIgnore += "react-proxy",
    stFlavour := Flavour.Slinky,
    libraryDependencies ++= Seq(
      "me.shadaj" %%% "slinky-web" % "0.7.2",
      "me.shadaj" %%% "slinky-hot" % "0.7.2",
      "io.github.cquiroz" %%% "scala-java-time" % "2.3.0"
    ),
    webpack / version := "4.44.2",
    startWebpackDevServer / version := "3.11.2",
    fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"),
    webpackNodeArgs := Seq("--openssl-legacy-provider"),
    webpackConfigFile := Some((ThisBuild / baseDirectory).value / "custom.webpack.config.js"),
    useYarn := true,
    scalaJSUseMainModuleInitializer := true,
    Test / requireJsDomEnv := true,
 
    build := {
      val artifacts = (Compile / fullOptJS / webpack).value
      val artifactFolder = (Compile / fullOptJS / crossTarget).value

      val indexFrom = baseDirectory.value / "src/main/js/index.html"
      val indexTo = artifactFolder / "index.html"

      val indexPatchedContent = {
        import collection.JavaConverters._
        Files
          .readAllLines(indexFrom.toPath, IO.utf8)
          .asScala
          .map(_.replaceAllLiterally("-fastopt-", "-opt-"))
          .mkString("\n")
      }

      Files.write(indexTo.toPath, indexPatchedContent.getBytes(IO.utf8))
      artifacts
    },
    addCommandAlias("dev", ";fastOptJS::startWebpackDevServer;~fastOptJS"),
  ).enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin, TzdbPlugin)
  .dependsOn(myovercast.js)
