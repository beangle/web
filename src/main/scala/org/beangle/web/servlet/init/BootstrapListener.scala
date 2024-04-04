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

package org.beangle.web.servlet.init

import jakarta.servlet.{ ServletContextEvent, ServletContextListener }
import org.beangle.web.servlet.context.ServletContextHolder

/** Web BootstrapListener
 *
 * {{{
 * <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
 *        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
 *                    https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
 *       version="5.0" metadata-complete="true">
 *
 * <absolute-ordering/>
 *
 * <listener>
 *    <listener-class>org.beangle.web.servlet.init.BootstrapListener</listener-class>
 * </listener>
 *
 * </web-app>
 * }}}
 */
class BootstrapListener extends ServletContextListener {

  var bootstrap: BootstrapInitializer = _

  override def contextInitialized(sce: ServletContextEvent): Unit =
    if (null == ServletContextHolder.context) {
      bootstrap = new BootstrapInitializer(false)
      bootstrap.onStartup(null, sce.getServletContext)
      bootstrap.listeners foreach { l =>
        l.contextInitialized(sce)
      }
    }

  override def contextDestroyed(sce: ServletContextEvent): Unit =
    if (null != bootstrap)
      bootstrap.listeners foreach { l =>
        l.contextDestroyed(sce)
      }
}
