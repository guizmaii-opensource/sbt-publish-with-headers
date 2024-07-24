package com.guizmaii.sbt.publishwithheaders

import org.apache.ivy.Ivy
import org.apache.ivy.util.CopyProgressListener
import org.apache.ivy.util.url.BasicURLHandler
import sbt.{File, URL}
import sttp.client3.{SimpleHttpClient, basicRequest}
import sttp.model.Uri

private[publishwithheaders] final class WithHeadersURLHandler(
  headers: Seq[(String, String)],
  logDebug: String => Unit,
) extends BasicURLHandler {

  override def upload(source: File, dest: URL, l: CopyProgressListener): Unit = {
    if (!("http" == dest.getProtocol) && !("https" == dest.getProtocol)) {
      throw new UnsupportedOperationException("URL repository only support HTTP PUT at the moment")
    }

    logDebug(s"About to upload file ${source.getPath}")

    SimpleHttpClient { client =>
      val url = Uri(normalizeToURL(dest).toURI)

      logDebug(s"Url $url")

      val request =
        basicRequest
          .put(url)
          .followRedirects(true)
          .body(source)
          .headers(
            // The order of the maps concatenation is important.
            // We don't want to allow users to override the headers we manually set.
            headers.toMap ++
              Map(
                "User-Agent" -> s"Apache Ivy/${Ivy.getIvyVersion}",
                "Accept"     -> "application/octet-stream, application/json, application/xml, */*",
              )
          )

      logDebug(s"Request: ${request.toCurl(Set("Authorization"))}")

      val response = client.send(request)

      logDebug(s"Response: $response")

      validatePutStatusCode(dest, response.code.code, s"${response.statusText} - ${response.body.fold(identity, identity)}")
    }
  }

}
