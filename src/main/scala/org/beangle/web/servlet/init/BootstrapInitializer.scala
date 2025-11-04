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

import jakarta.servlet.DispatcherType.REQUEST
import jakarta.servlet.{ServletContainerInitializer, ServletContext, ServletContextListener}
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.ClassLoaders
import org.beangle.commons.lang.Strings.{split, substringAfter, substringBefore}
import org.beangle.commons.lang.reflect.Reflections
import org.beangle.web.servlet.context.ServletContextHolder

import java.net.URL
import java.util as ju

/**
 * Web BootstrapListener
 */
class BootstrapInitializer extends ServletContainerInitializer {

  //这里做个代理,可以在context listener一旦调用初始化后，不能注册的情况下（BootstrapLister->Bootstrap）集中调用。
  val listeners = new collection.mutable.ListBuffer[ServletContextListener]

  /** 是否要注册ServletContextListener */
  var register: Boolean = true

  def this(r: Boolean) = {
    this()
    register = r
  }

  override def onStartup(clazzes: ju.Set[Class[_]], ctx: ServletContext): Unit = {
    if (null != ServletContextHolder.context) {
      ctx.log("Bootstrap has executed,aborted")
    } else {
      ServletContextHolder.store(ctx)
      val initializers = new ju.LinkedList[Initializer]
      ClassLoaders.getResources("beangle.xml") foreach { url =>
        fromXml(url) foreach { clazzName =>
          initializers.add(Reflections.newInstance[Initializer](clazzName))
        }
      }

      if (initializers.isEmpty)
        ctx.log("None beangle initializer was detected on classpath.")
      else {
        import scala.jdk.CollectionConverters.*
        val inits = initializers.asScala
        for (initializer <- inits) {
          initializer.boss = this
          ctx.log(s"${initializer.getClass.getName} initializing ...")
          initializer.onConfig(ctx)
        }
        inits foreach (x => x.onStartup(ctx))
        //process filter order
        val filterOrders = ctx.getInitParameter("filter-orders")
        if (null != filterOrders) {
          val orders = split(filterOrders, ";")
          orders foreach { order =>
            val pattern = substringBefore(order, "=")
            split(substringAfter(order, "="), ",") foreach { filterName =>
              val fr = ctx.getFilterRegistration(filterName)
              if (null == fr) sys.error(s"Cannot find filter $filterName")
              fr.addMappingForUrlPatterns(ju.EnumSet.of(REQUEST), true, pattern)
            }
          }
        }
        if register then listeners foreach (l => ctx.addListener(l))
      }
    }
  }

  def addListener(other: ServletContextListener): Unit = {
    listeners += other
  }

  private def fromXml(url: URL): Seq[String] = {
    val is = url.openStream()
    val initializers = (scala.xml.XML.load(is) \ "web" \ "initializer") map { i => (i \ "@class").text }
    IOs.close(is)
    initializers
  }

}
