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

package org.beangle.web.servlet.http.agent

enum Engine(var name: String,var categories: List[BrowserCategory]=List.empty) {

  case Trident extends Engine("Trident")

  case Gecko extends Engine("Gecko")

  case WebKit extends Engine("WebKit")

  case Presto extends Engine("Presto")

  case Mozilla extends Engine("Mozilla")

  case Khtml extends Engine("KHTML")

  case Word extends Engine("Microsoft Office Word")

  case Other extends Engine("Other")

  def addCategory(category: BrowserCategory): Unit =
    categories = categories ::: List(category)
}
