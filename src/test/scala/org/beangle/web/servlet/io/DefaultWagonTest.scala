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

package org.beangle.web.servlet.io

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import jakarta.servlet.{ServletOutputStream, WriteListener}
import org.beangle.commons.codec.net.BCoder
import org.beangle.commons.lang.{Charsets, ClassLoaders}
import org.mockito.Mockito.{mock, when}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, OutputStream}
import java.net.{URLDecoder, URLEncoder}

class DefaultWagonTest extends AnyFunSpec with Matchers {

  val wagon: Wagon = new DefaultWagon

  describe("DefaultStreamDownloader") {
    it("download") {
      val request = mock(classOf[HttpServletRequest])
      val response = mock(classOf[HttpServletResponse])
      when(response.getOutputStream).thenReturn(new ServletOutputStream() {
        val outputStream: OutputStream = new ByteArrayOutputStream()

        def write(b: Int): Unit = {
          outputStream.write(b)
        }

        def isReady(): Boolean = false

        def setWriteListener(writeListener: WriteListener): Unit = {}
      })
      val testDoc = ClassLoaders.getResource("download.txt").get
      wagon.copy(testDoc, request, response)
    }

    it("encode/decode") {
      val value = "汉字-english and .;"
      val ecodedValue = URLEncoder.encode(value, Charsets.UTF_8)
      URLDecoder.decode(ecodedValue, Charsets.UTF_8) should equal(value)
    }

    it("Bcoder encode/decode") {
      val value = "汉字-english and .;"
      val encodedValue = new BCoder().encode(value)
      new BCoder().decode(encodedValue) should equal(value)
    }
  }
}
