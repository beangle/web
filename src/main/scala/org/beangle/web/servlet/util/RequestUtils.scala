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

package org.beangle.web.servlet.util

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.{Charsets, Strings}
import org.beangle.web.servlet.http.agent.*

import java.net.URLEncoder

object RequestUtils {

  private val XForwardedFor = "x-forwarded-for"
  private val XForwardedProto = "X-Forwarded-Proto"
  private val XForwardedPort = "X-Forwarded-Port"
  private val XRequestedWith = "x-requested-with"

  /**
   * Returns remote ip address.
   * <ul>
   * <li>First,it lookup request header("x-forwarded-for"->"Proxy-Client-IP"->"WL-Proxy-Client-IP")
   * <li>Second,invoke request.getRemoteAddr()
   * </ul>
   *
   * @param request http request
   */
  def getIpAddr(request: HttpServletRequest): String = {
    val ip = request.getHeader(XForwardedFor)
    if (null == ip) request.getRemoteAddr else ip
  }

  def getProxies(request: HttpServletRequest): List[String] = {
    val headers = request.getHeaders(XForwardedFor)
    if (headers.hasMoreElements) {
      val client = headers.nextElement
      val proxies = Collections.newBuffer[String]
      while (headers.hasMoreElements)
        proxies += headers.nextElement
      proxies += request.getRemoteAddr
      proxies.toList
    } else
      List.empty
  }

  /**
   * Return the true servlet path.
   * When servletPath provided by container is empty,It will return requestURI-contextpath'
   * <p>
   * 查找当前调用的action对应的.do<br>
   * 例如http://localhost/myapp/dd.do 返回/dd.do<br>
   * http://localhost/myapp/dir/to/dd.do 返回/dir/to/dd.do
   */
  def getServletPath(request: HttpServletRequest): String = {
    var servletPath = request.getServletPath
    if (Strings.isNotEmpty(servletPath))
      servletPath
    else {
      val uri = request.getRequestURI
      if (uri.length == 1) return ""
      var context = request.getContextPath
      val length = context.length
      if (length > 2) {
        if ('/' == context.charAt(length - 1)) context = context.substring(0, length - 1)
        servletPath = uri.substring(context.length)
        val semicolonIdx = servletPath.indexOf(';')
        if semicolonIdx > 0 then servletPath.substring(0, semicolonIdx) else servletPath
      } else {
        val semicolonIdx = uri.indexOf(';')
        if semicolonIdx > 0 then uri.substring(0, semicolonIdx) else uri
      }
    }
  }

  /**
   * Set Content-Disposition header
   *
   * @see http://tools.ietf.org/html/rfc6266
   * @see http://tools.ietf.org/html/rfc5987
   * @see https://blog.robotshell.org/2012/deal-with-http-header-encoding-for-file-download/
   */
  def setContentDisposition(response: HttpServletResponse, attachName: String): Unit = {
    val value = new StringBuilder("attachment;")
    value ++= " filename*=utf-8''" + URLEncoder.encode(attachName, Charsets.UTF_8).replaceAll("\\+", "%20")
    response.setHeader("Content-Disposition", value.mkString)
  }

  /**
   * Return {@code Useragent} of request.
   *
   * @param request
   */
  def getUserAgent(request: HttpServletRequest): Useragent = {
    val head = request.getHeader("USER-AGENT")
    Useragent(getIpAddr(request), Browser.parse(head), Os.parse(head))
  }

  def isHttps(req: HttpServletRequest): Boolean = {
    req.getScheme.toLowerCase == "https" || "https".equalsIgnoreCase(req.getHeader(XForwardedProto))
  }

  def getServerPort(req: HttpServletRequest): Int = {
    val headPort = req.getHeader(XForwardedPort)
    if Strings.isEmpty(headPort) then req.getServerPort else Integer.parseInt(headPort)
  }

  def isAjax(request: HttpServletRequest): Boolean = {
    val headers = request.getHeaders(XRequestedWith)
    while (headers.hasMoreElements) {
      val header = headers.nextElement()
      if (header == "XMLHttpRequest") return true
    }
    false
  }
}
