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

package org.beangle.web.servlet.url

import org.beangle.commons.lang.{Charsets, Strings}
import org.beangle.web.servlet.url.UrlBuilder.separator

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class UrlRender {

  def render(context: String, referer: String, uri: String, params: Map[String, String]): String = {
    val sb = renderUri(context, referer, uri)
    if sb.contains('?') then sb.append(separator) else sb.append('?')
    sb.append(UrlBuilder.encodeParams(params))
    sb.toString
  }

  def render(context: String, referer: String, uri: String, params: String*): String = {
    val separator = "&"
    val sb = renderUri(context, referer, uri)
    sb.append(separator)
    for (param <- params)
      try {
        sb.append(URLEncoder.encode(param, Charsets.UTF_8))
        sb.append(separator)
      } catch {
        case e: UnsupportedEncodingException => e.printStackTrace()
      }
    sb.delete(sb.length - separator.length, sb.length)
    sb.toString
  }

  def render(context: String, referer: String, uri: String): String =
    renderUri(context, referer, uri).toString

  private def renderUri(context: String, referer: String, uriStr: String): StringBuilder = {
    val sb = new StringBuilder()
    if (Strings.isEmpty(uriStr)) {
      sb ++= referer
      return sb
    }
    if (uriStr.startsWith("http"))
      return new StringBuilder(uriStr)
    var questIndex = uriStr.indexOf('?')
    val queryStr = if (-1 != questIndex) uriStr.substring(questIndex + 1) else null
    val uri = if (-1 == questIndex) uriStr else uriStr.substring(0, questIndex)
    if (-1 == questIndex) questIndex = uriStr.length

    sb ++= context
    if (uri.startsWith("/"))
      sb ++= uri.substring(0, questIndex)
    else {
      val lastslash = referer.lastIndexOf("/") + 1
      val namespace = referer.substring(0, lastslash)
      sb.append(namespace)
      if (uri.startsWith("!")) {
        var dot = referer.indexOf("!", lastslash)
        if (-1 == dot) dot = referer.indexOf(".", lastslash)
        if (-1 == dot) dot = referer.length
        val action = referer.substring(lastslash, dot)
        sb ++= action
        sb ++= uri
      } else
        sb.append(uri)
    }
    if (null != queryStr) sb.append('?').append(queryStr)
    sb
  }
}
