import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*
import sbt.url

ThisBuild / organization := "org.beangle.web"
ThisBuild / version := "0.6.2-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/web"),
    "scm:git@github.com:beangle/web.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "The Beangle Web Library"
ThisBuild / homepage := Some(url("http://beangle.github.io/web/index.html"))

val beangle_commons = "org.beangle.commons" % "beangle-commons" % "5.6.22"

lazy val root = (project in file("."))
  .settings(
    name := "beangle-web",
    common,
    libraryDependencies ++= Seq(beangle_commons, servletapi, scalaxml),
    libraryDependencies ++= Seq(mockito, scalatest, logback_classic % "test"),
    libraryDependencies ++= Seq(websocketapi % "optional", websocket_client_api % "optional")
  )
