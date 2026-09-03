import org.beangle.parent.Dependencies.*
import org.beangle.parent.Settings.*

organization := "org.beangle.web"
version := "0.7.10-SNAPSHOT"

scmInfo := Some(
  ScmInfo(
    uri("https://github.com/beangle/web"),
    "scm:git@github.com:beangle/web.git"
  )
)

developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = uri("http://github.com/duantihua")
  )
)

description := "The Beangle Web Library"
homepage := Some(uri("http://beangle.github.io/web/index.html"))

val beangle_commons = "org.beangle.commons" % "beangle-commons" % "6.3.2"

lazy val root = (project in file("."))
  .settings(
    name := "beangle-web",
    common,
    libraryDependencies ++= Seq(servletapi, beangle_commons),
    libraryDependencies ++= Seq(mockito, scalatest, logback_classic % "test"),
    libraryDependencies ++= Seq(websocketapi % "optional", websocket_client_api % "optional")
  )
