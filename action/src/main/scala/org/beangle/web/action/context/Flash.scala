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

package org.beangle.web.action.context

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.beangle.commons.lang.Strings
import org.beangle.web.servlet.util.CookieUtils

import java.util as ju

object Flash {
  private val CookieName = "beangle_flash"
}

@SerialVersionUID(-5292283953338410228L)
class Flash(request: HttpServletRequest, response: HttpServletResponse) extends Serializable {

  /**
   * current request
   */
  val now: ju.Map[String, String] = new ju.HashMap()

  /**
   * next request
   */
  private val next: ju.Map[String, String] = new ju.HashMap()

  moveCookieToNow()

  private def moveCookieToNow(): Unit = {
    val cv = CookieUtils.getCookieValue(request, Flash.CookieName)
    if (null != cv) {
      Strings.split(cv, ",") foreach { pair =>
        val key = Strings.substringBefore(pair, "=")
        val v = Strings.substringAfter(pair, "=")
        now.put(key, v)
      }
      CookieUtils.deleteCookieByName(request, response, Flash.CookieName)
    }
  }

  def writeNextToCookie(): Unit = {
    if (next.isEmpty) return
    val sb = new StringBuilder
    val i = next.entrySet().iterator()
    while (i.hasNext) {
      val e = i.next()
      val kv = e.getKey + "=" + e.getValue
      sb.append(kv).append(",")
    }
    if (sb.nonEmpty) {
      sb.deleteCharAt(sb.length - 1)
      CookieUtils.addCookie(request, response, Flash.CookieName, sb.toString(), 1)
    } else {
      CookieUtils.deleteCookieByName(request, response, Flash.CookieName)
    }
  }

  def get(key: Object): String = now.get(key)

  def put(key: String, value: String): String = {
    next.put(key, value)
    value
  }

  def putAll(values: ju.Map[_ <: String, _ <: String]): Unit = {
    next.putAll(values)
  }

  def keep(key: String): Unit = {
    next.put(key, now.get(key))
  }

  def keep(): Unit = {
    next.putAll(now)
  }

  def clear(): Unit = {
    now.clear()
  }

  def append(key: String, content: String): Unit = {
    val exist = next.get(key)
    next.put(key, if (null == exist) content else exist + ";" + content)
  }

  def appendNow(key: String, content: String): Unit = {
    val exist = now.get(key)
    now.put(key, if (null == exist) content else exist + ";" + content)
  }

}
