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

package org.beangle.web.servlet.context

import jakarta.servlet.{ServletContextEvent, ServletContextListener}
import org.beangle.commons.cdi.Container

/** 关闭CDI容器
 *
 * @param container cdi container
 */
class ContainerCleanup(container: Container) extends ServletContextListener {
  override def contextInitialized(sce: ServletContextEvent): Unit = {
  }

  override def contextDestroyed(sce: ServletContextEvent): Unit = {
    if (null != container) {
      container.close()
      Container.unregister(container)
    }
  }
}
