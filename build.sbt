name := "scala-cd"

organization := "com.earldouglas"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

seq(webSettings :_*)

libraryDependencies ++= Seq( // container
    "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container"
  , "org.eclipse.jetty" % "jetty-plus" % "9.1.0.v20131115" % "container"
  , "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)
 
libraryDependencies ++= Seq( // test
    "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "test"
  , "org.eclipse.jetty" % "jetty-plus" % "9.1.0.v20131115" % "test"
  , "javax.servlet" % "javax.servlet-api" % "3.1.0" % "test"
  , "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

ScoverageSbtPlugin.instrumentSettings

CoverallsPlugin.coverallsSettings

val deploy = taskKey[Unit]("Deploy the packaged .war file via heroku")

deploy := {
  val (_, warFile) = (packagedArtifact in (Compile, packageWar)).value
  ("bash deploy.sh " + warFile.getPath) !
}
