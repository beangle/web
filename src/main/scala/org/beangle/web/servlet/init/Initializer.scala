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

import jakarta.servlet.ServletContext
import jakarta.servlet.ServletException
import jakarta.servlet.ServletContextListener

/**
 * @see BootstrapListener.InitFile
 */
trait Initializer {
  var boss: BootstrapInitializer = _

  /**
   * config servletContext
   * @param servletContext
   */
  def onConfig(servletContext: ServletContext): Unit = {

  }

  /**
   * Configure the given {@link ServletContext} with any servlets, filters, listeners
   * context-params and attributes necessary for initializing this web application.
   * @param servletContext the {@code ServletContext} to initialize
   */
  @throws(classOf[ServletException])
  def onStartup(servletContext: ServletContext): Unit

  final def addListener(other: ServletContextListener): Unit = {
    boss.addListener(other)
  }
}
