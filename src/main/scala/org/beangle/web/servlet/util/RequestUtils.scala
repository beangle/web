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
import org.beangle.commons.lang.{Charsets, Numbers, Strings}
import org.beangle.web.servlet.http.agent.*

import java.net.URLEncoder

object RequestUtils {

  private val XForwardedFor = "x-forwarded-for"
  private val XForwardedProto = "X-Forwarded-Proto"
  private val XForwardedHost = "X-Forwarded-Host"
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

  /** 请求自身的 origin，形如 `scheme://host[:port]`，默认端口（http 80 / https 443）省略。
   *
   * 与浏览器 `Origin` 头同构，可直接与之比较，用于同源/CORS 判定。
   *
   * 反向代理场景取浏览器看到的那一侧：协议与端口交给 `isHttps` / `getServerPort`（已处理
   * `X-Forwarded-Proto` / `X-Forwarded-Port`），主机优先 `X-Forwarded-Host`——主机名没有等价的
   * `getServerName` 处理，而容器默认不解析 `X-Forwarded-Host`，不能用 `req.getServerName` 代替。
   * `X-Forwarded-Host` 是否自带端口取决于代理配置（nginx `$host` 不带、`$http_host` 带），
   * 自带端口时优先于 `X-Forwarded-Port`，与 Undertow `ProxyPeerAddressHandler` 保持一致。
   */
  def getOrigin(req: HttpServletRequest): String = {
    val scheme = if (isHttps(req)) "https" else "http"
    val forwardedHost = req.getHeader(XForwardedHost)
    val hostPort = if (Strings.isNotBlank(forwardedHost)) forwardedHost.trim else req.getServerName
    val (host, hostPortValue) = splitHostPort(hostPort)
    // 端口优先级：Host 自带端口 > X-Forwarded-Port > getServerPort()（后者已处理 X-Forwarded-Port）
    val port = if (hostPortValue > 0) hostPortValue else getServerPort(req)
    val sb = new StringBuilder
    sb.append(scheme).append("://").append(host)
    if (port > 0 && port != (if (scheme == "https") 443 else 80)) sb.append(':').append(port)
    sb.toString
  }

  /** 拆分 `host[:port]`，IPv6 字面量（`[::1]:8080`）保留方括号；无端口时返回 -1。 */
  private def splitHostPort(hostPort: String): (String, Int) = {
    val value = hostPort.trim
    if (value.startsWith("[")) {
      val close = value.indexOf(']')
      if (close < 0) (value, -1)
      else {
        val portText = value.substring(close + 1).stripPrefix(":")
        (value.substring(0, close + 1), if (portText.isEmpty) -1 else Numbers.toInt(portText, -1))
      }
    } else {
      val colon = value.lastIndexOf(':')
      if (colon < 0 || colon != value.indexOf(':')) (value, -1)
      else (value.substring(0, colon), Numbers.toInt(value.substring(colon + 1), -1))
    }
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
