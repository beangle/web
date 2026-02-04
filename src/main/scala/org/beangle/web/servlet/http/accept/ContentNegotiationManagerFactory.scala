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

import org.beangle.commons.bean.{Factory, Initializing}

class ContentNegotiationManagerFactory extends Factory[ContentNegotiationManager], Initializing {

  var favorParameter: Boolean = _
  var favorPathExtension: Boolean = _
  var ignoreAcceptHeader: Boolean = _
  var parameterName: String = _
  private var result: ContentNegotiationManager = null

  override def init(): Unit = {
    val resolvers = new collection.mutable.ListBuffer[ContentTypeResolver]
    if (this.favorPathExtension) resolvers += new PathExtensionContentResolver()
    if (this.favorParameter) resolvers += new ParameterContentResolver(parameterName)
    if (!this.ignoreAcceptHeader) resolvers += new HeaderContentTypeResolver()
    result = new ContentNegotiationManager(resolvers.toList)
  }

  override def getObject: ContentNegotiationManager = result
}
