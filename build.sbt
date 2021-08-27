ThisBuild / scalaVersion     := "2.13.6"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.joecordingley"
ThisBuild / organizationName := "joecordingley"


lazy val root = (project in file("."))
  .settings(
    name := "nim",
    libraryDependencies ++= Dependencies.all
  )

scaliteratePandocPDFOptions in Compile := Seq (
  "--pdf-engine=xelatex"
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
