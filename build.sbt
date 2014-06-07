name := "xwp-template"

organization := "com.earldouglas"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

seq(webSettings :_*)

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container"

libraryDependencies += "org.eclipse.jetty" % "jetty-plus" % "9.1.0.v20131115" % "container"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"

ScoverageSbtPlugin.instrumentSettings

CoverallsPlugin.coverallsSettings

val linkWar = taskKey[Unit]("Symlink the packaged .war file")

linkWar := {
  val (art, pkg) = packagedArtifact.in(Compile, packageWar).value
  import java.nio.file.Files
  val link = (target.value / (art.name + "." + art.extension))
  link.delete
  Files.createSymbolicLink(link.toPath, pkg.toPath)
}
