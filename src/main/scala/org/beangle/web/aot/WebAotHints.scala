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

package org.beangle.web.aot

import jakarta.servlet.{FilterConfig, ServletContext, ServletRequest}
import jakarta.servlet.http.HttpServletRequest
import org.beangle.commons.aot.AotHintRegistrar
import org.beangle.web.servlet.*

/** beangle-web 的 GraalVM native-image 反射提示。 */
class WebAotHints extends AotHintRegistrar {
  override def registering(): Unit = {
    hints.registerType(
      classOf[filter.GenericHttpFilter],
      classOf[http.accept.ContentNegotiationManager],
      classOf[http.accept.ContentNegotiationManagerFactory],
      classOf[intercept.Interceptor],
      classOf[security.RequestConvertor],
      classOf[util.CookieGenerator])

    // Jakarta Servlet API：运行期反射探测
    hints.registerType(
      classOf[FilterConfig],
      classOf[ServletContext],
      classOf[ServletRequest],
      classOf[HttpServletRequest])
  }
}
