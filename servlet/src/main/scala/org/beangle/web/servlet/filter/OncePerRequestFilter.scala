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

package org.beangle.web.servlet.filter

import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest

/**
 * Once per request filter.
 *
 * @author chaostone
 * @since 3.0.0
 */
abstract class OncePerRequestFilter extends GenericHttpFilter {

  var attributeName: String = _

  def isFirstEnter(request: ServletRequest): Boolean =
    if (null != request.getAttribute(attributeName)) false else {
      request.setAttribute(attributeName, true)
      true
    }

  override def init(): Unit =
    if (null == attributeName) attributeName = getClass.getName + this.hashCode() + ".FILTED"
}
