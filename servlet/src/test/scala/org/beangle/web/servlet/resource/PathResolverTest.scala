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

package org.beangle.web.servlet.resource

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PathResolverTest extends AnyFunSpec with Matchers {
  describe("PathResolver") {
    it("resolve") {
      val res = PathResolver.resolve("/static/scripts/jquery/jquery,beangle.js")
      assert(res.size == 2)
      assert(res == List("/static/scripts/jquery/jquery.js", "/static/scripts/jquery/beangle.js"))

      val res2 = PathResolver.resolve("/static/scripts/jquery/jquery,beangle")
      assert(res2.size == 2)
      assert(res2 == List("/static/scripts/jquery/jquery", "/static/scripts/jquery/beangle"))

      val res3 = PathResolver.resolve("jquery,beangle")
      assert(res3.size == 2)
      assert(res3 == List("jquery", "beangle"))
    }
  }
}
