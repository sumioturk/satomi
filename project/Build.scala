import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "satomi"
  val appVersion = "1.0-SNAPSHOT"
  val casbah = "org.mongodb" %% "casbah" % "2.5.0"
  val salat = "com.novus" %% "salat" % "1.9.2-SNAPSHOT"
  val specs2 = "org.specs2" %% "specs2" % "1.9" % "test"


  object Resolvers {
    val all = Seq.empty
  }

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    casbah,
    specs2
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers ++= Resolvers.all,
    scalaVersion := "2.10.0"
  )

}
