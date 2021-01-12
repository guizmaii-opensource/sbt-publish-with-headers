/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.sbt.plugins

import okhttp3.Headers
import sbt.Keys._
import sbt.{Def, _}
import org.apache.ivy.util.url._


object PublishWithHeadersPlugin extends AutoPlugin with PublishWithHeadersKeys {
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    headers := "",
    publishMavenStyle := true,
    configureRepositoryPlugin := {
      val log =  streams.value.log
      log.info(s"Updating urlHandlerDispatcher to use PublishWithHeadersPlugin")
      val urlHandlerDispatcher = new URLHandlerDispatcher {
        super.setDownloader("http", new WithHeadersURLHandler(
          Headers.of(Strings.toMap(headers.value))))
        super.setDownloader("https", new WithHeadersURLHandler(
          Headers.of(Strings.toMap(headers.value))))
        override def setDownloader(protocol: String, downloader: URLHandler): Unit = {}
      }
      URLHandlerRegistry.setDefault(urlHandlerDispatcher)
    },

    whPublish := Def.sequential(
      configureRepositoryPlugin,
      publish
    ).value,
  )
}
