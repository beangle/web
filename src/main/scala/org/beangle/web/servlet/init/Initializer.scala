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

import jakarta.servlet.{ServletContext, ServletContextListener, ServletException}

/** Web初始化接口
 *
 * @see BootstrapListener.InitFile
 */
trait Initializer {
  var boss: BootstrapInitializer = _

  /** 配置web上下文
   *
   * @param context servletContext
   */
  def onConfig(context: ServletContext): Unit = {

  }

  /** 启动上下文
   * Configure the given {@link ServletContext} with any servlets, filters, listeners
   * context-params and attributes necessary for initializing this web application.
   *
   * @param context servletContext
   */
  @throws(classOf[ServletException])
  def onStartup(context: ServletContext): Unit

  final def addListener(other: ServletContextListener): Unit = {
    boss.addListener(other)
  }

  def order: Int = 1024

}
