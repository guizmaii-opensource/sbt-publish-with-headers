package com.guizmaii.sbt.publishwithheaders

import sbt.*

trait PublishWithHeadersKeys {

  lazy val publishToWithHeaders: SettingKey[Option[MavenRepository]] =
    settingKey[Option[MavenRepository]]("""Resolver to publish to with headers.""")

  lazy val headersToPublishWith =
    settingKey[Seq[(String, String)]]("""Headers definition. Example: Seq("Authorization" -> s"Bearer $token")""")

  lazy val configureRepositoryPlugin = taskKey[Unit]("Configures a URLHandlerDispatcher for use with custom headers.")
  lazy val publishWithHeaders        = taskKey[Unit]("Publish a projects artifacts to Maven repository with added request headers.")

}
