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

import java.net.URL

class ProcessContext(val uri: String, paths: Seq[String], urls: Seq[URL]) {

  val resources: List[Resource] = {
    val resources = new collection.mutable.ArrayBuffer[Resource]
    for (i <- 0 until paths.size)
      resources += new Resource(paths(i), urls(i))
    resources.toList
  }
}

class Resource(val path: String, val url: URL) {
}
