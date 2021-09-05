import sbt.Keys.version
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.webpack
ThisBuild / scalaVersion := "2.13.6"

lazy val myovercast = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .settings(
    name := "MyOvercast",
    version := "0.1.0-SNAPSHOT"
  )

lazy val backend = project.in(file("jvm"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "1.4.0",
      "com.lihaoyi" %% "requests" % "0.6.9",
      "com.lihaoyi" %% "os-lib" % "0.7.8",
      "org.scala-lang.modules" %% "scala-xml" % "2.0.1")
  ).dependsOn(myovercast.jvm)

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
    libraryDependencies ++= Seq(
      "me.shadaj" %%% "slinky-web" % "0.6.7",
      "me.shadaj" %%% "slinky-hot" % "0.6.7"),
    webpack / version := "4.44.2",
    startWebpackDevServer / version := "3.11.2",

    fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"),
    fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),

    Test / requireJsDomEnv := true,

    addCommandAlias("dev", ";fastOptJS::startWebpackDevServer;~fastOptJS"),

    addCommandAlias("build", "fullOptJS::webpack") 
  ).enablePlugins(ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)
  .dependsOn(myovercast.js)


