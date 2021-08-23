scalaVersion := "2.13.6"
version      := "0.1.0-SNAPSHOT"
organization := "io.github.manuzhang"
name         := "MyOvercast"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "upickle" % "1.4.0",
  "com.lihaoyi" %% "requests" % "0.6.9",
  "com.lihaoyi" %% "os-lib" % "0.7.8",
  "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
)
