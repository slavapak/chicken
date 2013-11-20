import sbt._
import Keys._

object ChickenBuild extends Build {

  lazy val root =
    Project (id = "chicken", base = file(".")).
      dependsOn(uri("git://github.com/sbt/sbt-assembly.git#0.10.1"))

}
