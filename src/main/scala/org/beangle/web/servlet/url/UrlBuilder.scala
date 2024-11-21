/*
 * Copyright (C) 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.beangle.web.servlet.url

import jakarta.servlet.http.HttpServletRequest
import org.beangle.commons.lang.{Charsets, Strings}
import org.beangle.web.servlet.util.RequestUtils

import java.net.URLEncoder

object UrlBuilder {
  val separator = "&"

  def apply(req: HttpServletRequest): UrlBuilder = {
    val builder = new UrlBuilder(req.getContextPath)
    val scheme = if (RequestUtils.isHttps(req)) "https" else "http"
    val port = RequestUtils.getServerPort(req)
    builder.setScheme(scheme).setServerName(req.getServerName).setPort(port)
      .setRequestURI(req.getRequestURI).setQueryString(req.getQueryString)
    builder
  }

  def url(req: HttpServletRequest): String = {
    apply(req).buildUrl()
  }

  def encodeParams(params: collection.Map[String, String]): String = {
    val sb = new StringBuilder()
    for ((key, value) <- params) {
      sb.append(URLEncoder.encode(key, Charsets.UTF_8))
        .append('=')
        .append(URLEncoder.encode(value, Charsets.UTF_8))
        .append(separator)
    }
    sb.delete(sb.length - separator.length, sb.length)
    sb.toString()
  }

  def encodeURI(uri: String): String = {
    val idx = uri.indexOf("?")
    if (idx > 0) {
      val queryString = uri.substring(idx + 1)
      val pairs = Strings.split(queryString, "&")
      val sb = new StringBuilder(uri.substring(0, idx + 1))
      pairs foreach { pair =>
        val k = pair.substring(0, pair.indexOf("="));
        val v = pair.substring(pair.indexOf("=") + 1);
        sb.append(URLEncoder.encode(k, "utf-8")).append("=").append(URLEncoder.encode(v, "utf-8").replace("+", "%20")).append("&");
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.toString()
    } else {
      uri
    }
  }
}

/**
 * @author chaostone
 */
class UrlBuilder(cxtPath: String) {

  var scheme: String = _

  var serverName: String = _

  var port: Int = _

  var contextPath: String = if (cxtPath == "/") "" else cxtPath

  var servletPath: String = _

  var requestURI: String = _

  var pathInfo: String = _

  var queryString: String = _

  /**
   * Returns servetPath without contextPath
   */
  private def buildServletPath(): String = {
    var uri = servletPath
    if (uri == null && null != requestURI) {
      uri = requestURI
      if (contextPath != "") uri = uri.substring(contextPath.length)
    }
    if ((null == uri)) "" else uri
  }

  def buildOrigin(): String = {
    val sb = new StringBuilder
    sb.append(scheme).append("://")
    sb.append(serverName)
    val includePort = port != (if (scheme == "http") 80 else 443)
    if (includePort && port > 0)
      sb.append(':').append(port)
    sb.toString
  }

  /**
   * Returns request Url contain pathinfo and queryString but without contextPath.
   */
  def buildRequestUrl(): String = {
    val sb = new StringBuilder()
    sb.append(buildServletPath())
    if (null != pathInfo) sb.append(pathInfo)
    if (null != queryString) sb.append('?').append(queryString)
    sb.toString
  }

  /**
   * Returns full url
   */
  def buildUrl(): String = {
    val sb = new StringBuilder()
    var includePort = true
    if (null != scheme) {
      sb.append(scheme).append("://")
      includePort = (port != (if (scheme == "http") 80 else 443))
    }
    if (null != serverName) {
      sb.append(serverName)
      if (includePort && port > 0)
        sb.append(':').append(port)
    }
    sb.append(contextPath)
    sb.append(buildRequestUrl())
    sb.toString
  }

  def setScheme(scheme: String): this.type = {
    this.scheme = scheme
    this
  }

  def setServerName(serverName: String): this.type = {
    this.serverName = serverName
    this
  }

  def setPort(port: Int): this.type = {
    this.port = port
    this
  }

  /**
   * ContextPath should start with / but not ended with /
   */
  def setContextPath(contextPath: String): this.type = {
    this.contextPath = contextPath
    this
  }

  /**
   * Set servletPath ,start with /
   */
  def setServletPath(servletPath: String): this.type = {
    this.servletPath = servletPath
    this
  }

  /**
   * Set requestURI ,it should start with /
   */
  def setRequestURI(requestURI: String): this.type = {
    this.requestURI = requestURI
    this
  }

  def setPathInfo(pathInfo: String): this.type = {
    this.pathInfo = pathInfo
    this
  }

  def setQueryString(queryString: String): this.type = {
    this.queryString = queryString
    this
  }
}
