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

import org.beangle.commons.io.ClasspathResourceLoader
import org.beangle.commons.lang.Strings
import org.beangle.web.servlet.resource.filter.HeaderFilter

import jakarta.servlet.ServletConfig
import jakarta.servlet.http.{ HttpServlet, HttpServletRequest, HttpServletResponse }

class StaticResourceServlet extends HttpServlet {

  var processor: ResourceProcessor = _

  override def init(config: ServletConfig): Unit =
    buildProcessor()

  @throws(classOf[Exception])
  override protected def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val contextPath = request.getContextPath
    val uri =
      if (!(contextPath.equals("") || contextPath.equals("/")))
        Strings.substringAfter(request.getRequestURI, contextPath)
      else request.getRequestURI
    processor.process(uri, request, response)
  }

  protected def buildProcessor(): Unit =
    if (null == processor) {
      val p = new ResourceProcessor(new ClasspathResourceLoader)
      p.filters = List(new HeaderFilter)
      processor = p
    }
}
