libraryDependencies += "org.scala-sbt"                 %% "scripted-plugin" % sbtVersion.value
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"              % "2.5.2")
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.3.1")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"               % "0.6.4")
addSbtPlugin("org.typelevel"    % "sbt-tpolecat"              % "0.5.2")
addSbtPlugin("com.github.sbt"   % "sbt-ci-release"            % "1.7.0")
