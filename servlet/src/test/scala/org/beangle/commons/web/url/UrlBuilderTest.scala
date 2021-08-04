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

import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec

/**
 * @author chaostone
 * @version
 */

class UrlBuilderTest extends AnyFunSpec with Matchers {

  describe("UrlBuilder") {
    it("build full url") {
      val builder = new UrlBuilder("/")
      builder.setScheme("http").setServerName("localhost").setPort(80)
      builder.setRequestURI("/demo/security/user")
      builder.setQueryString("name=1&fullname=join")
      builder.buildUrl() should be equals ("http://localhost/demo/security/user?name=1&fullname=join")
      builder.setRequestURI(null).setPort(8080).setServletPath("/security")
      builder.buildUrl() should be equals ("http://localhost:8080/security?name=1&fullname=join")
    }

    it("build simple url") {
      val builder = new UrlBuilder("/")
      builder.setServletPath("/security/user")
      builder.setQueryString("name=1&fullname=join")
      builder.buildRequestUrl() should be equals ("/security/user?name=1&fullname=join")
      builder.setRequestURI("/demo/security/user")
      builder.buildRequestUrl() should be equals ("/security/user?name=1&fullname=join")
    }
  }
}
