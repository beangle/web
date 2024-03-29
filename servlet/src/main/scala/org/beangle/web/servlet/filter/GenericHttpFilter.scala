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

package org.beangle.web.servlet.filter

import jakarta.servlet.{Filter, FilterConfig, ServletException}
import org.beangle.commons.bean.{Initializing, Properties}
import org.beangle.commons.lang.Strings

import scala.collection.mutable

/**
 * @author chaostone
 */
abstract class GenericHttpFilter extends Filter with Initializing {

  private[this] var filterConfig: FilterConfig = _

  /**
   * Standard way of initializing this filter. Map config parameters onto bean
   * properties of this filter, and invoke subclass initialization.
   */
  override def init(filterConfig: FilterConfig): Unit = {
    this.filterConfig = filterConfig
    initParams(filterConfig, requiredProperties)
    init()
  }

  private final def initParams(config: FilterConfig, requiredProperties: Set[String]): Unit = {
    val missingProps = new mutable.HashSet[String]
    if (requiredProperties != null && requiredProperties.nonEmpty) missingProps ++= requiredProperties
    val en = config.getInitParameterNames
    while (en.hasMoreElements) {
      val property = en.nextElement()
      val value = config.getInitParameter(property)
      Properties.copy(this, property, value)
      missingProps.remove(property)
    }
    if (missingProps.nonEmpty)
      throw new ServletException("Initialization from FilterConfig for filter '" + config.getFilterName +
        "' failed; the following required properties were missing: " +
        Strings.join(missingProps, ", "))
  }

  /**
   * Make the name of this filter available to subclasses.
   */
  protected def filterName: String = if (filterConfig != null) filterConfig.getFilterName else "None"

  override def init(): Unit = {
  }

  def config: FilterConfig = this.filterConfig

  /**
   * Set of required properties (Strings) that must be supplied as config
   * parameters to this filter.
   */
  def requiredProperties: Set[String] = Set.empty

  override def destroy(): Unit = {
  }
}
