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

import java.io.{ IOException, InputStream }

import org.beangle.commons.io.IOs
import org.beangle.commons.lang.Strings
import org.beangle.commons.lang.time.Stopwatch

import jakarta.servlet.http.{ HttpServletRequest, HttpServletResponse }

/**
 * RangedWagon
 * <p>
 * Split download senario like this:
 * <li>Server first response:200</li>
 *
 * <pre>
 * Content-Length=106786028
 * Accept-Ranges=bytes
 * </pre>
 *
 * <li>Client send request :</li>
 *
 * <pre>
 * Range: bytes=2000070-106786027
 * </pre>
 *
 * <li>Server send next response:206</li>
 *
 * <pre>
 * Content-Length=106786028
 * Content-Range=bytes 2000070-106786027/106786028
 * </pre>
 *
 * @author chaostone
 * @since 2.4
 */
class RangedWagon extends DefaultWagon {

  override def copy(input: InputStream, req: HttpServletRequest, res: HttpServletResponse): Unit = {
    res.setHeader("Accept-Ranges", "bytes")
    res.setHeader("connection", "Keep-Alive")
    var length = 0
    var start = 0L
    var begin = 0L
    var stop = 0L
    try {
      length = input.available()
      stop = length - 1
      res.setContentLength(length)
      val rangestr = req.getHeader("Range")
      if (null != rangestr) {
        val readlength = Strings.substringAfter(rangestr, "bytes=").split("-")
        start = java.lang.Long.parseLong(readlength(0))
        if (readlength.length > 1 && Strings.isNotEmpty(readlength(1)))
          stop = java.lang.Long.parseLong(readlength(1))
        if (start != 0) {
          res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT)
          val crange = "bytes " + start + "-" + stop + "/" + length
          res.setHeader("Content-Range", crange)
        }
      }
      val output = res.getOutputStream
      input.skip(start)
      begin = start
      val size = 4 * 1024
      val buffer = Array.ofDim[Byte](size)
      var step = maxStep(start, stop, size)
      while (step > 0) {
        val readed = input.read(buffer, 0, step)
        if (readed == -1)
          step = 0
        else {
          output.write(buffer, 0, readed)
          start += readed
          step = maxStep(start, stop, size)
        }
      }
    } finally
      IOs.close(input)
  }

  def maxStep(start: Long, stop: Long, bufferSize: Int): Int =
    if (stop - start + 1 >= bufferSize) bufferSize
    else (stop - start + 1).toInt
}
