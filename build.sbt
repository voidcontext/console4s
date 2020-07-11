import xerial.sbt.Sonatype._

ThisBuild / name := "console4s"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / organization := "com.gaborpihaj"
ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.3.1-RC2"

ThisBuild / publishTo := sonatypePublishToBundle.value

val catsVersion = "2.1.1"
val catsEffectVersion = "2.1.2"
val jlineVersion = "3.14.0"
val scalatestVersion = "3.1.1"

lazy val publishSettings = List(
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  publishMavenStyle := true,
  sonatypeProjectHosting := Some(GitHubHosting("voidcontext", "console4s", "gabor.pihaj@gmail.com"))
)

lazy val defaultSettings = Seq(
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
  addCompilerPlugin(scalafixSemanticdb),
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
)

lazy val console4s = (project in file("console4s"))
  .settings(
    name := "console4s",
    publishSettings,
    defaultSettings,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"           % catsVersion,
      "org.typelevel" %% "cats-effect"         % catsEffectVersion,
      "org.jline"     % "jline-terminal"       % jlineVersion,
      "org.jline"     % "jline-terminal-jansi" % jlineVersion,
      "org.jline"     % "jline-reader"         % jlineVersion,
      "org.scalatest" %% "scalatest"           % scalatestVersion % Test
    )
  )

lazy val examples = (project in file("examples"))
  .settings(
    name := "examples",
    publishSettings,
    defaultSettings,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion
    )
  )
  .dependsOn(console4s)

lazy val root = (project in file("."))
  .settings(
    skip in publish := true
  )
  .aggregate(
    console4s,
    examples
  )

addCommandAlias("fmt", ";scalafix ;test:scalafix ;scalafmtAll ;scalafmtSbt")
addCommandAlias("prePush", ";fmt ;clean ;reload ;test")
