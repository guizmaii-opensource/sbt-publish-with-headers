organization  := "com.guizmaii"
name          := "sbt-publish-with-headers"
homepage      := Some(url("https://github.com/guizmaii-opensource/sbt-publish-with-headers"))
licenses      := Seq("Apache 2.0" -> url("https://opensource.org/license/apache-2.0"))
versionScheme := Some("semver-spec")

Global / onChangedBuildSource := ReloadOnSourceChanges

sbtPlugin    := true
scalaVersion := "2.12.19"

libraryDependencies += "dev.zio" %% "zio-test" % "2.1.6" % Test
