import sbt.Keys._
import sbt._

object BuildSettings {
  val buildScalaVersion = "3.0.1"

  val commonSettings = Seq(
    organizationName := "The Beangle Software",
    licenses += ("GNU Lesser General Public License version 3", new URL("http://www.gnu.org/licenses/lgpl-3.0.txt")),
    startYear := Some(2005),
    scalaVersion := buildScalaVersion,
    crossPaths := true,

    publishMavenStyle := true,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishM2Configuration := publishM2Configuration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),

    versionScheme := Some("early-semver"),
    pomIncludeRepository := { _ => false }, // Remove all additional repository other than Maven Central from POM
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    })
}

object Dependencies {
  val mockitoVer = "3.11.1"
  val logbackVer = "1.2.4"
  val scalatestVer = "3.2.9"
  val servletapiVer = "5.0.0"
  val commonsVer = "5.2.4"
//  val javassistVer = "3.27.0-GA"
//  val templateFreemarkerVer = "0.0.33"
//  val cdiVer = "0.3.1-SNAPSHOT"

  val scalatest = "org.scalatest" %% "scalatest" % scalatestVer % "test"
  val mockito = "org.mockito" % "mockito-core" % mockitoVer % "test"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVer % "test"
  val logbackCore = "ch.qos.logback" % "logback-core" % logbackVer % "test"

  val servletapi = "jakarta.servlet" % "jakarta.servlet-api" % servletapiVer
  val commonsCore = "org.beangle.commons" %% "beangle-commons-core" % commonsVer
//  val javassist = "org.javassist" % "javassist" % javassistVer
//  val templateFreemarker = "org.beangle.template" %% "beangle-template-freemarker" % templateFreemarkerVer
//  val cdiApi = "org.beangle.cdi" %% "beangle-cdi-api" % cdiVer

  var webDeps = Seq(commonsCore, logbackClassic, logbackCore, scalatest, servletapi, mockito)
}

