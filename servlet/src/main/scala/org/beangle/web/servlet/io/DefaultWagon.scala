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

import java.io.{ File, FileInputStream, InputStream }
import java.net.URL

import org.beangle.commons.io.IOs

import jakarta.servlet.http.{ HttpServletRequest, HttpServletResponse }

/**
 * Default DefaultWagon
 *
 * @author chaostone
 * @since 4.4
 */
class DefaultWagon extends Wagon {

  override def copy(url: URL, req: HttpServletRequest, res: HttpServletResponse): Unit =
    try
      copy(url.openStream, req, res)
    catch {
      case e: Exception => log(req, s"download file error=$url", e)
    }

  override def copy(file: File, req: HttpServletRequest, res: HttpServletResponse): Unit =
    if (file.exists())
      try
        copy(new FileInputStream(file), req, res)
      catch {
        case e: Exception => log(req, s"download file error=${file.getName}", e)
      }

  override def copy(is: InputStream, req: HttpServletRequest, res: HttpServletResponse): Unit =
    try
      IOs.copy(is, res.getOutputStream)
    catch {
      case e: Exception => log(req, "download file error ", e)
    } finally
      IOs.close(is)

  protected def log(req: HttpServletRequest, msg: String, e: Throwable): Unit =
    req.getServletContext.log(msg, e)
}
