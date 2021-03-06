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
}
