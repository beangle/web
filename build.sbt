import Dependencies._
import BuildSettings._
import sbt.url

ThisBuild / organization := "org.beangle.web"
ThisBuild / version := "0.0.1"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/web"),
    "scm:git@github.com:beangle/web.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "The Beangle Web Library"
ThisBuild / homepage := Some(url("http://beangle.github.io/web/index.html"))

lazy val root = (project in file("."))
  .settings()
  .aggregate(servlet,action)

lazy val servlet = (project in file("servlet"))
  .settings(
    name := "beangle-web-servlet",
    commonSettings,
    libraryDependencies ++= (webDeps)
  )

lazy val action = (project in file("action"))
  .settings(
    name := "beangle-web-action",
    commonSettings,
    libraryDependencies ++= webDeps
  ).dependsOn(servlet)

publish / skip := true