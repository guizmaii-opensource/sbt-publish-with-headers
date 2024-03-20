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

import org.apache.ivy.Ivy
import org.apache.ivy.util.url.{BasicURLHandler, IvyAuthenticator}
import org.apache.ivy.util.{CopyProgressListener, FileUtil, Message}
import sbt.{File, URL}

import java.io.{ByteArrayOutputStream, FileInputStream, IOException, InputStream}
import java.net.{HttpURLConnection, URLConnection}
import java.util

class WithHeadersURLHandler(headers: java.util.Map[String,String]) extends BasicURLHandler() {

  private val BUFFER_SIZE = 64 * 1024
  private val ERROR_BODY_TRUNCATE_LEN = 512
  override def upload(source: File, dest: URL, l: CopyProgressListener): Unit = {

    if (!("http" == dest.getProtocol) && !("https" == dest.getProtocol)) throw new UnsupportedOperationException("URL repository only support HTTP PUT at the moment")

    // Install the IvyAuthenticator// Install the IvyAuthenticator
    IvyAuthenticator.install()

    var conn: HttpURLConnection = null
    try {
      val normalDest = normalizeToURL(dest)
      conn = normalDest.openConnection.asInstanceOf[HttpURLConnection]
      conn.setDoOutput(true)
      conn.setRequestMethod("PUT")
      conn.setRequestProperty("User-Agent", "Apache Ivy/" + Ivy.getIvyVersion)
      conn.setRequestProperty("Accept", "application/octet-stream, application/json, application/xml, */*")
      conn.setRequestProperty("Content-type", "application/octet-stream")
      conn.setRequestProperty("Content-length", source.length.toString)
      headers.entrySet().forEach(entry => conn.setRequestProperty(entry.getKey, entry.getValue))
      conn.setInstanceFollowRedirects(true)
      val in = new FileInputStream(source)
      try {
        val os = conn.getOutputStream
        FileUtil.copy(in, os, l)
      } finally try in.close()
      catch {
        case _: IOException =>
      }
      // initiate the connection
      val responseCode = conn.getResponseCode
      var extra = ""
      val errorStream = conn.getErrorStream
      val responseStream = conn.getInputStream
      if (errorStream != null) extra = "; Response Body: " + readTruncated(errorStream, ERROR_BODY_TRUNCATE_LEN, conn.getContentType, conn.getContentEncoding)
      else if (responseStream != null) {
        val decodingStream = getDecodingInputStream(conn.getContentEncoding, responseStream)
        extra = "; Response Body: " + readTruncated(responseStream, ERROR_BODY_TRUNCATE_LEN, conn.getContentType, conn.getContentEncoding)
      }
      Message.debug("Response Headers:" + getHeadersAsDebugString(conn.getHeaderFields))
      validatePutStatusCode(dest, responseCode, conn.getResponseMessage + extra)
    } finally disconnect(conn)
  }

  @throws[IOException]
  private def getHeadersAsDebugString(headers: util.Map[String, util.List[String]]): String = {
    val builder: StringBuilder = new StringBuilder("")
    if (headers != null) {
      import scala.collection.JavaConversions.*
      for (header <- headers.entrySet) {
        val key: String = header.getKey
        if (key != null) {
          builder.append(header.getKey)
          builder.append(": ")
        }
        builder.append(String.join("\n    ", header.getValue))
        builder.append("\n")
      }
    }
    builder.toString
  }
  /**
   * Extract the charset from the Content-Type header string, or default to ISO-8859-1 as per
   * rfc2616-sec3.html#sec3.7.1 .
   *
   * @param contentType
   * the Content-Type header string
   * @return the charset as specified in the content type, or ISO-8859-1 if unspecified.
   */
  private def getCharSetFromContentType(contentType: String): String = {
    var charSet: String = null
    if (contentType != null) {
      val elements = contentType.split(";")
      for (i <- 0 until elements.length) {
        val element = elements(i).trim
        if (element.toLowerCase.startsWith("charset=")) charSet = element.substring("charset=".length)
      }
    }
    if (charSet == null || charSet.isEmpty) {
      // default to ISO-8859-1 as per rfc2616-sec3.html#sec3.7.1
      charSet = "ISO-8859-1"
    }
    charSet
  }

  @throws[IOException]
  private def readTruncated(is: InputStream, maxLen: Int, contentType: String, contentEncoding: String) = {
    val decodingStream = getDecodingInputStream(contentEncoding, is)
    val charSet = getCharSetFromContentType(contentType)
    val os = new ByteArrayOutputStream(maxLen)
    try {
      var count = 0
      var b = decodingStream.read
      while (count < maxLen && b >= 0) {
        os.write(b)
        count += 1
        b = decodingStream.read
      }
      new String(os.toByteArray, charSet)
    } finally try is.close()
    catch {
      case _: IOException =>
    }
  }
  private def disconnect(con: URLConnection): Unit = {
    con match {
      case connection: HttpURLConnection =>
        if (!("HEAD" == connection.getRequestMethod)) {
          // We must read the response body before disconnecting!
          // Cfr. http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html
          // [quote]Do not abandon a connection by ignoring the response body. Doing
          // so may results in idle TCP connections.[/quote]
          readResponseBody(connection)
        }
        connection.disconnect()
      case _ => if (con != null) try con.getInputStream.close()
      catch {
        case _: IOException =>
      }
    }
  }

  private def readResponseBody(conn: HttpURLConnection): Unit = {
    val buffer = new Array[Byte](BUFFER_SIZE)
    var inStream: InputStream = null
    try {
      inStream = conn.getInputStream
      while (inStream.read(buffer) > 0) {
      }
    } catch {
      case _: IOException =>
    } finally if (inStream != null) try inStream.close()
    catch {
      case _: IOException =>
    }
    val errStream = conn.getErrorStream
    if (errStream != null) try while (errStream.read(buffer) > 0) {
    }
    catch {
      case _: IOException =>
    } finally try errStream.close()
    catch {
      case _: IOException =>
    }
  }
}

