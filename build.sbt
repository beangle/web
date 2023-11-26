import org.beangle.parent.Dependencies._
import org.beangle.parent.Settings._
import sbt.url

ThisBuild / organization := "org.beangle.web"
ThisBuild / version := "0.4.8"

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

val beangle_commons_core = "org.beangle.commons" %% "beangle-commons-core" % "5.6.5"
val beangle_commons_text = "org.beangle.commons" %% "beangle-commons-text" % "5.6.5"
val webDeps = Seq(beangle_commons_core, logback_classic, logback_core, scalatest, servletapi, mockito)


lazy val root = (project in file("."))
  .settings()
  .aggregate(servlet,action)

lazy val servlet = (project in file("servlet"))
  .settings(
    name := "beangle-web-servlet",
    common,
    libraryDependencies ++= webDeps
  )

lazy val action = (project in file("action"))
  .settings(
    name := "beangle-web-action",
    common,
    libraryDependencies ++= (webDeps ++ Seq(beangle_commons_text,scalaxml))
  ).dependsOn(servlet)

publish / skip := true
