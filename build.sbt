name := "LearningModuleAkka"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "com.typesafe.akka" %% "akka-actor" % "2.5.19",
  "com.typesafe" % "config" % "1.3.3",
  "io.circe" %% "circe-core" % "0.11.1",
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-parser" % "0.11.1",
  "io.circe" %% "circe-config" % "0.6.1",
  "org.apache.olingo" % "odata-client-api" % "4.5.0",
  "org.apache.olingo" % "odata-client-core" % "4.5.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.scala-lang.modules" % "scala-java8-compat_2.11" % "0.9.0",

  "com.iheart" %% "ficus" % "1.4.3",

  "com.github.nscala-time" %% "nscala-time" % "2.22.0",
  "io.spray" %%  "spray-json" % "1.3.5"

)
