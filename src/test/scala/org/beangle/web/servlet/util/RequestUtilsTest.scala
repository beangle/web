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

import org.mockito.Mockito.{ mock, when }
import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec
import jakarta.servlet.http.HttpServletRequest

class RequestUtilsTest extends AnyFunSpec with Matchers {

  describe("getOrigin") {
    it("getOrigin 还原请求自身 origin，默认端口省略") {
      RequestUtils.getOrigin(request("https", "learning.example.edu.cn", 443)) should be("https://learning.example.edu.cn")
      RequestUtils.getOrigin(request("http", "learning.example.edu.cn", 80)) should be("http://learning.example.edu.cn")
      RequestUtils.getOrigin(request("https", "learning.example.edu.cn", 8443)) should be("https://learning.example.edu.cn:8443")
    }

    it("getOrigin 优先 X-Forwarded-* 头") {
      RequestUtils.getOrigin(
        request("http", "10.0.0.5", 8080,
          Map("X-Forwarded-Proto" -> "https", "X-Forwarded-Host" -> "learning.example.edu.cn",
            "X-Forwarded-Port" -> "443"))) should be("https://learning.example.edu.cn")
    }

    it("getOrigin 在缺少 X-Forwarded-Port 时使用 X-Forwarded-Host 自带端口") {
      RequestUtils.getOrigin(
        request("http", "10.0.0.5", 8080,
          Map("X-Forwarded-Proto" -> "https", "X-Forwarded-Host" -> "learning.example.edu.cn:8443"))) should be("https://learning.example.edu.cn:8443")
    }

    it("getOrigin 无转发头时退回容器端口") {
      RequestUtils.getOrigin(
        request("http", "10.0.0.5", 8080, Map("X-Forwarded-Proto" -> "https"))) should be("https://10.0.0.5:8080")
    }

    it("getOrigin 优先取 X-Forwarded-Host 自带端口，而不是 X-Forwarded-Port") {
      RequestUtils.getOrigin(
        request("http", "10.0.0.5", 8080,
          Map("X-Forwarded-Host" -> "learning.example.edu.cn:8443", "X-Forwarded-Port" -> "9443"))) should be("http://learning.example.edu.cn:8443")
    }

    it("getOrigin 支持 IPv6 字面量") {
      RequestUtils.getOrigin(request("http", "[::1]", 8080)) should be("http://[::1]:8080")
    }
  }

  describe("RequestUtils") {
    it("testGetServletPath") {
      var request = mock(classOf[HttpServletRequest])
      when(request.getContextPath).thenReturn("/")
      when(request.getRequestURI).thenReturn("/")
      assert("" == RequestUtils.getServletPath(request))

      request = mock(classOf[HttpServletRequest])
      when(request.getContextPath).thenReturn("/")
      when(request.getRequestURI).thenReturn("/demo;jsessoin_id=1")
      assert("/demo" == RequestUtils.getServletPath(request))

      request = mock(classOf[HttpServletRequest])
      when(request.getContextPath).thenReturn("")
      when(request.getRequestURI).thenReturn("/demo")
      assert("/demo" == RequestUtils.getServletPath(request))
    }
  }

  private def request(scheme: String, serverName: String, serverPort: Int,
                      headers: Map[String, String] = Map.empty): HttpServletRequest = {
    val req = mock(classOf[HttpServletRequest])
    when(req.getScheme).thenReturn(scheme)
    when(req.getServerName).thenReturn(serverName)
    when(req.getServerPort).thenReturn(serverPort)
    headers.foreach { case (name, value) => when(req.getHeader(name)).thenReturn(value) }
    req
  }
}
