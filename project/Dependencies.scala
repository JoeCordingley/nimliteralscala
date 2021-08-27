import sbt._

object Dependencies {
  lazy val all = Seq(
    "org.typelevel" %% "cats-effect" % "3.2.3",
    "org.typelevel" %% "cats-core" % "2.6.1"
  )

}
