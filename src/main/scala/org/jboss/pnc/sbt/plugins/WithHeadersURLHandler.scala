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

import gigahorse.support.okhttp.Gigahorse
import okhttp3.{Headers, MediaType, OkHttpClient, Request, RequestBody, Response}
import org.apache.ivy.util.{CopyProgressEvent, CopyProgressListener, Message}
import org.apache.ivy.util.url.IvyAuthenticator
import sbt.{File, URL}
import sbt.internal.librarymanagement.ivyint.{ErrorMessageAuthenticator, GigahorseUrlHandler}

object URLHandlerHelper {
  lazy val http: OkHttpClient = {
    Gigahorse.http(Gigahorse.config)
      .underlying[OkHttpClient]
      .newBuilder()
      .authenticator(new sbt.internal.librarymanagement.JavaNetAuthenticator)
      .followRedirects(true)
      .followSslRedirects(true)
      .build
  }
}

class WithHeadersURLHandler(headers: Headers) extends GigahorseUrlHandler(URLHandlerHelper.http) {

  private val EmptyBuffer: Array[Byte] = new Array[Byte](0)

  override def upload(source: File, dest0: URL, l: CopyProgressListener): Unit = {

    if (("http" != dest0.getProtocol) && ("https" != dest0.getProtocol)) {
      throw new UnsupportedOperationException("URL repository only support HTTP PUT at the moment")
    }
    Message.debug("Uploading using WithHeadersURLHandler...")

    IvyAuthenticator.install()
    ErrorMessageAuthenticator.install()

    val dest = normalizeToURL(dest0)

    val body = RequestBody.create(MediaType.parse("application/octet-stream"), source)

    val request = new Request.Builder()
      .url(dest)
      .headers(headers)
      .put(body)
      .build()

    if (l != null) {
      l.start(new CopyProgressEvent())
    }
    val response = URLHandlerHelper.http.newCall(request).execute()
    try {
      if (l != null) {
        l.end(new CopyProgressEvent(EmptyBuffer, source.length()))
      }
      validatePutStatusCode(dest, response.code(), response.message())
    } finally {
      response.close()
    }
  }
}

