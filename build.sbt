name := """GarageBuddy"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.webjars.bower" % "bootstrap-sass" % "3.3.6",
  "org.webjars.bower" % "bootswatch" % "3.3.6",
  "org.webjars" % "autoprefixer" % "5.2.0"
  "com.typesafe.play" %% "play-mailer" % "5.0.0"
)
