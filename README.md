Publish libraries and fetch dependencies from Maven repositories requiring custom headers
===============================

# This is not a fork

This project is a fork of https://github.com/project-ncl/sbt-publish-with-headers

Why forking?     
1. The initial plugin doesn't seem to be published to Maven Central
2. We brought fixes, improvements and changed how the plugin works.

# Documentation

This plugin allows you to publish artifacts to a Maven repository with custom HTTP headers.     
It also automatically configure your sbt project so that it can fetch dependencies from a Maven repository with custom HTTP headers.

## Installation

Enable the plugin in your `project/plugins.sbt` file:
```sbt
addSbtPlugin("com.guizmaii" % "sbt-publish-with-headers" % "0.0.5")
```

## Configuration

Configure the plugin in your `build.sbt` file:
```sbt
publishToWithHeaders := Some("my-repo-requiring-custom-headers" at "https://maven-repo-host/path")
headersToPublishWith := Seq("header-key" -> "header-value", "header-key-2" -> "header-value-2")
```

## Execution
To publish to a Maven repository with http headers run:

`sbt publishWithHeaders`

## Fetching dependencies from your Maven repository with custom HTTP headers

There's nothing for you to do.    
The plugin will automatically configure your sbt project so that it can fetch dependencies from your Maven repository requiring custom HTTP headers.