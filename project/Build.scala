import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "satomi"
  val appVersion = "1.0-SNAPSHOT"
  val casbah = "org.mongodb" %% "casbah" % "2.5.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    casbah
  )

  scalaVersion := "2.9.1"

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
