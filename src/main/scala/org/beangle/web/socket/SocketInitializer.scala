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

package org.beangle.web.socket

import jakarta.servlet.{ServletContext, ServletContextEvent, ServletContextListener}
import jakarta.websocket.server.{ServerContainer, ServerEndpointConfig}
import org.beangle.web.servlet.init.Initializer

abstract class SocketInitializer extends Initializer, ServletContextListener {

  def getConfigs(): Iterable[ServerEndpointConfig] = Set.empty

  def getPojoEndPointClasses(): Iterable[Class[_]] = Set.empty

  private var registed: Boolean = false

  override def onStartup(servletContext: ServletContext): Unit = {
    if (null == servletContext.getAttribute("jakarta.websocket.server.ServerContainer")) {
      servletContext.addListener(this)
    } else {
      register(servletContext)
    }
  }

  override def contextInitialized(sce: ServletContextEvent): Unit = {
    register(sce.getServletContext)
  }

  private def register(servletContext: ServletContext): Unit = {
    if (!registed) {
      registed = true
      val sc = servletContext.getAttribute("jakarta.websocket.server.ServerContainer").asInstanceOf[ServerContainer]
      if (sc == null) {
        servletContext.log("No websocket server container found")
      } else {
        getConfigs() foreach { config => sc.addEndpoint(config) }
        getPojoEndPointClasses() foreach { clazz => sc.addEndpoint(clazz) }
      }
    }
  }
}
