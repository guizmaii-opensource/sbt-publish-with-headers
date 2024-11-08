organization  := "com.guizmaii"
name          := "sbt-publish-with-headers"
homepage      := Some(url("https://github.com/guizmaii-opensource/sbt-publish-with-headers"))
licenses      := Seq("Apache 2.0" -> url("https://opensource.org/license/apache-2.0"))
versionScheme := Some("semver-spec")
developers    :=
  List(
    Developer(
      "matejonnet",
      "Matej Lazar",
      "unknown",
      url("https://github.com/matejonnet"),
    ),
    Developer(
      "robobario",
      "Robert Young",
      "unknown",
      url("https://github.com/robobario"),
    ),
    Developer(
      "guizmaii",
      "Jules Ivanic",
      "jules.ivanic@gmail.com",
      url("https://x.com/guizmaii"),
    ),
  )

Global / onChangedBuildSource := ReloadOnSourceChanges

scalafmtCheck     := true
scalafmtSbtCheck  := true
scalafmtOnCompile := true

sbtPlugin    := true
scalaVersion := "2.12.20"

libraryDependencies += "com.softwaremill.sttp.client3" %% "core"     % "3.10.1"
libraryDependencies += "dev.zio"                       %% "zio-test" % "2.1.12" % Test
