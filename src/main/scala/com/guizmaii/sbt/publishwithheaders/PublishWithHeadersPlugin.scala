package com.guizmaii.sbt.publishwithheaders

import lmcoursier.CoursierConfiguration
import lmcoursier.definitions.Authentication
import lmcoursier.syntax.CoursierConfigurationOp
import org.apache.ivy.util.url.*
import sbt.Keys.*
import sbt.{Def, *}

object PublishWithHeadersPlugin extends AutoPlugin with PublishWithHeadersKeys {
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    publishToWithHeaders      := Option.empty,
    headersToPublishWith      := Seq.empty,
    publishMavenStyle         := true,
    configureRepositoryPlugin := {
      val log                  = streams.value.log
      log.debug(s"Updating urlHandlerDispatcher to use PublishWithHeadersPlugin")
      val _headers             = headersToPublishWith.value // eager evaluation
      log.debug(s"Headers to publish with: ${_headers.map { case (k, v) => s"$k -> ****${v.takeRight(4)}" }.mkString("(", ", ", ")")}")
      val urlHandlerDispatcher = new URLHandlerDispatcher {
        super.setDownloader("http", new WithHeadersURLHandler(_headers, log.debug(_)))
        super.setDownloader("https", new WithHeadersURLHandler(_headers, log.debug(_)))
        override def setDownloader(protocol: String, downloader: URLHandler): Unit = {}
      }
      URLHandlerRegistry.setDefault(urlHandlerDispatcher)
    },
    resolvers ++= publishToWithHeaders.value.toList,
    publishTo                 := publishToWithHeaders.value,
    publishWithHeaders        :=
      Def
        .sequential(
          configureRepositoryPlugin,
          publish,
        )
        .value,

    // Inspired by: https://github.com/gilcloud/sbt-gitlab/blob/v0.1.2/src/main/scala/com/gilcloud/sbt/gitlab/GitlabPlugin.scala#L75-L79
    // Comes from: https://github.com/sbt/sbt/issues/4382#issuecomment-662670858
    csrConfiguration                        :=
      withHeaders(headersToPublishWith.value, publishToWithHeaders.value)(csrConfiguration.value),
    updateClassifiers / csrConfiguration    :=
      withHeaders(headersToPublishWith.value, publishToWithHeaders.value)((updateClassifiers / csrConfiguration).value),
    updateSbtClassifiers / csrConfiguration :=
      withHeaders(headersToPublishWith.value, publishToWithHeaders.value)((updateSbtClassifiers / csrConfiguration).value),
  )

  private def withHeaders(headers: Seq[(String, String)], repo: Option[MavenRepository])(
    coursierConfiguration: CoursierConfiguration
  ): CoursierConfiguration =
    repo match {
      case None       => coursierConfiguration
      case Some(repo) => coursierConfiguration.addRepositoryAuthentication(repo.name, Authentication("", "").withHeaders(headers))
    }

}
