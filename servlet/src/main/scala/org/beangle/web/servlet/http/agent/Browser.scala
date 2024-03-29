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

import org.beangle.commons.lang.Strings
import scala.collection.mutable

object Browser {

  var browsers = new mutable.HashMap[String, Browser]

  val Unknown = new Browser(BrowserCategory.Unknown, null)

  /**
   * Iterates over all Browsers to compare the browser signature with the user
   * agent string. If no match can be found Browser.Unknown will be returned.
   *
   * @param agentString
   * @return Browser
   */
  def parse(agentString: String): Browser = {
    if (Strings.isEmpty(agentString)) return Browser.Unknown

    val engineItor = Engine.values.iterator
    while (engineItor.hasNext) {
      val engine = engineItor.next()
      val engineName = engine.name
      if (agentString.contains(engineName)) {
        val categoryItor = engine.categories.iterator
        while (categoryItor.hasNext) {
          val category = categoryItor.next()
          val version = category.matches(agentString)
          if (version != null) {
            val key = category.name + "/" + version
            var browser = browsers.get(key).orNull
            if (null == browser) {
              browser = new Browser(category, version)
              browsers.put(key, browser)
            }
            return browser
          }
        }
      }
    }

    val categoryItor = BrowserCategory.values.iterator
    while (categoryItor.hasNext) {
      val category = categoryItor.next()
      val version = category.matches(agentString)
      if (version != null) {
        val key = category.name + "/" + version
        var browser = browsers.get(key).orNull
        if (null == browser) {
          browser = new Browser(category, version)
          browsers.put(key, browser)
        }
        return browser
      }
    }
    Browser.Unknown
  }
}

import Browser._
/**
 * Web browser
 *
 * @author chaostone
 */
@SerialVersionUID(-6200607575108416928L)
class Browser(val category: BrowserCategory, val version: String) extends Serializable with Ordered[Browser] {

  override def toString: String = {
    category.name + " " + (if (version == null) "" else version)
  }

  def compare(o: Browser): Int = category.ordinal - o.category.ordinal
}
