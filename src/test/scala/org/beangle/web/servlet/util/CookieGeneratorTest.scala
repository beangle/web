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

import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec

class CookieGeneratorTest extends AnyFunSpec with Matchers {

  describe("Cookieg") {
    it("set base with all part") {
      val g = new CookieGenerator("sid")
      g.base = "localhost:9080/myApp"
      g.domain should be("localhost")
      g.port should be(9080)
      g.base should be("http://localhost:9080")
    }

    it("set simple base") {
      val g = new CookieGenerator("sid")
      g.base = "jwxt.openurp.edu.cn"
      g.domain should be("jwxt.openurp.edu.cn")
      g.port should be(80)
      g.base should be("http://jwxt.openurp.edu.cn")
    }

    it("set simple https base with path") {
      val g = new CookieGenerator("sid")
      g.base = "https://localhost/a"
      g.domain should be("localhost")
      g.port should be(443)
      g.base should be("https://localhost")
    }

    it("set simple https base with path and port") {
      val g = new CookieGenerator("sid")
      g.base = "https://localhost:9443/"
      g.domain should be("localhost")
      g.port should be(9443)
      g.base should be("https://localhost:9443")
    }
  }

  describe("base with somedomain.org") {
    case class BaseCase(input: String, domain: String, secure: Boolean, port: Int, output: String)

    val cases = Seq(
      BaseCase("http://somedomain.org/aa", "somedomain.org", secure = false, 80, "http://somedomain.org"),
      BaseCase("http://somedomain.org", "somedomain.org", secure = false, 80, "http://somedomain.org"),
      BaseCase("somedomain.org", "somedomain.org", secure = false, 80, "http://somedomain.org"),
      BaseCase("https://somedomain.org/aa", "somedomain.org", secure = true, 443, "https://somedomain.org"),
      BaseCase("https://somedomain.org", "somedomain.org", secure = true, 443, "https://somedomain.org"),
    )

    cases.foreach { c =>
      it(s"set base from ${c.input}") {
        val g = new CookieGenerator("sid")
        g.base = c.input
        g.domain should be(c.domain)
        g.secure should be(c.secure)
        g.port should be(c.port)
        g.base should be(c.output)
      }
    }
  }
}
