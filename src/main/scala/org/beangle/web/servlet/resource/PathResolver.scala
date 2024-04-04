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

import org.beangle.commons.lang.Strings

import scala.collection.immutable.ArraySeq
import scala.collection.mutable

trait PathResolver {
  def resolve(name: String): Seq[String]
}

object PathResolver {
  def resolve(path: String): Seq[String] = {
    val lastDotIndex = path.lastIndexOf('.')
    val postfix = if lastDotIndex == -1 then "" else path.substring(lastDotIndex)
    val names = Strings.split(path, ",")
    val rs = new mutable.ArrayBuffer[String]()
    var pathDir: String = null
    names foreach { name =>
      var iname = name
      if iname.startsWith("/") then pathDir = Strings.substringBeforeLast(name, "/")
      else if null != pathDir then iname = pathDir + "/" + iname

      if (postfix.nonEmpty && !iname.endsWith(postfix)) iname = iname + postfix
      rs += iname
    }
    ArraySeq.from(rs)
  }
}
