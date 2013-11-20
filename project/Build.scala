import sbt._
import Keys._

object ChickenBuild extends Build {

  lazy val root =
    Project (id = "chicken",
      base = file("."))

}
