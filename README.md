Publish with headers SBT plugin
===============================

# This is not a fork

This project is a fork of https://github.com/project-ncl/sbt-publish-with-headers

Why forking?     
Because the initial plugin doesn't seem to be publish to Maven Central

# Documentation

This is an SBT plugin to add http headers when publishing Maven artifacts.

It's mostly based on the: https://github.com/k8ty-app/sbt-publish and https://github.com/sbt/sbt/issues/4382#issuecomment-469734888

## Installation
Enable the plugin in the `<project-root>/project/plugins.sbt` or `$HOME/.sbt/1.0/plugins/build.sbt` for a global configuration:

```
addSbtPlugin("com.guizmaii" % "sbt-publish-with-headers" % "0.0.2")
```

## Configuration

Configure the plugin in the `build.sbt` (or `$HOME/.sbt/1.0/global.sbt` for a global configuration):
```sbt
headersToPublishWith := Seq("header-key" -> "header-value", "header-key-2" -> "header-value-2")
publishTo := Some("MavenRepo" at s"https://maven-repo-host/path")
//in case you are still using an endpoit without TLS
publishTo := Some(("MavenRepo" at s"http://maven-repo-host/path").withAllowInsecureProtocol(true))
```

## Execution
To publish to a Maven repository with http headers run:

`sbt publishWithHeaders`

## Just in case
you need http headers for fetching the dependencies from the Maven repository.

[Coursier](https://get-coursier.io/docs/sbt-coursier) is used as a default dependency resolver since SBT 1.3.x
and it supports [custom http headers](https://github.com/coursier/sbt-coursier/pull/218). 

Configure headers in the `build.sbt` (or `$HOME/.sbt/1.0/global.sbt` for a global configuration):
```sbt
import lmcoursier.definitions.Authentication
import lmcoursier.syntax.CoursierConfigurationOp

lazy val myRepo = ("my-repo-id" at "https://maven-repo-host/path")
csrConfiguration ~= _.addRepositoryAuthentication(myRepo.name, Authentication("", "").withHeaders(Seq("header-key" -> "header-value")))
resolvers += myRepo
```