import sbt._
import Dependencies._
import BuildConstants._

lazy val commonSettings: Seq[Def.SettingsDefinition] = Seq(
  organization := org,
  scalaVersion := scalaVer,
  version := buildVer,
  libraryDependencies += scalaTest,
  fork := true
)


// ------------------------------------------------------
lazy val tutorial = (project in file("tutorial/")).
  settings(commonSettings: _*).
  settings(name := "tutorial")

lazy val tree = (project in file("tree/")).
  settings(commonSettings: _*).
  settings(name := "tree")

lazy val functional = (project in file("functional/")).
  settings(commonSettings: _*).
  settings(name := "functional")

lazy val calculator = (project in file("calculator/")).
  settings(commonSettings: _*).
  settings(name := "calculator")

lazy val csp = (project in file("csp/")).
  enablePlugins(ProtobufPlugin).
  settings(commonSettings: _*).
  settings(name := "csp",
    javaSource in ProtobufConfig := ((sourceDirectory in Compile).value / "generated"))

// ------------------------------------------------------
// main project
lazy val assignments = (project in file(".")).
  settings(commonSettings: _*).
  settings(name := "assignments").aggregate(tutorial, functional, tree, calculator, csp)