Publish with headers SBT plugin
===============================

This is an SBT plugin to add http headers when publishing Maven artifacts.

This is mostly based on the: https://github.com/k8ty-app/sbt-publish and https://github.com/sbt/sbt/issues/4382#issuecomment-469734888

## Installation
Clone this repo and run `sbt publishLocal`

## Configuration
Enable the plugin in the `<project-root>/project/plugins.sbt` or `$HOME/.sbt/1.0/plugins/build.sbt` for a global configuration.
```
addSbtPlugin("org.jboss.pnc.sbt.plugins" % "publish-with-headers" % "0.0.1")
```

Configure the plugin in the `<project-root>/build.sbt` or `$HOME/.sbt/1.0/global.sbt` for a global configuration.
```
PublishWithHeadersPlugin.headers := "header-key:header-value|header-key-2:header-value-2"
publishTo := Some("MavenRepo" at s"https://maven-repo-host/path"),
//in case you are still using an endpoit without TLS
publishTo := Some(("MavenRepo" at s"https://maven-repo-host/path").withAllowInsecureProtocol(true)),
```

## Execution
To publish to a Maven repository with http headers run
`sbt whPublish`