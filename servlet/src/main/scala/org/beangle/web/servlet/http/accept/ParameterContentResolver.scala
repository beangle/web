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

package org.beangle.web.servlet.http.accept

import jakarta.servlet.http.HttpServletRequest
import org.beangle.commons.activation.{ MediaType, MediaTypes }

class ParameterContentResolver(val parameterName: String) extends ContentTypeResolver {

  def resolve(request: HttpServletRequest): Seq[MediaType] = {
    val ext = request.getParameter(parameterName)
    if (null == ext)
      Seq.empty
    else
      MediaTypes.get(ext) match {
        case Some(mimeType) => List(mimeType)
        case None => Seq.empty
      }
  }
}
