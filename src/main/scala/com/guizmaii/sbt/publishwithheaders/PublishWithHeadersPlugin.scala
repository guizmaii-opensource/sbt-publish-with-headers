package com.guizmaii.sbt.publishwithheaders

import org.apache.ivy.util.url.*
import sbt.Keys.*
import sbt.{Def, *}

object PublishWithHeadersPlugin extends AutoPlugin with PublishWithHeadersKeys {
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    headersToPublishWith      := Seq.empty,
    publishMavenStyle         := true,
    configureRepositoryPlugin := {
      val log                  = streams.value.log
      log.info(s"Updating urlHandlerDispatcher to use PublishWithHeadersPlugin")
      val _headers             = headersToPublishWith.value // eager evaluation
      log.info(s"Headers to publish with: ${_headers.map { case (k, v) => s"$k -> ****${v.takeRight(4)}" }.mkString("(", ", ", ")")}")
      val urlHandlerDispatcher = new URLHandlerDispatcher {
        super.setDownloader("http", new WithHeadersURLHandler(_headers, log.debug(_)))
        super.setDownloader("https", new WithHeadersURLHandler(_headers, log.debug(_)))
        override def setDownloader(protocol: String, downloader: URLHandler): Unit = {}
      }
      URLHandlerRegistry.setDefault(urlHandlerDispatcher)
    },
    publishWithHeaders        :=
      Def
        .sequential(
          configureRepositoryPlugin,
          publish,
        )
        .value,
  )
}
