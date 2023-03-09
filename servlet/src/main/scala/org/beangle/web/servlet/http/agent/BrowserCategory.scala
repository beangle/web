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
import org.beangle.web.servlet.http.agent.Engine
import org.beangle.web.servlet.http.agent.Engine.*

import java.util.regex.{Matcher, Pattern}

/**
 * 浏览器种类
 * 不要轻易改变这里的顺序
 */
enum BrowserCategory(val name: String, val engine: Engine, versions: String*) {
  private val versionPairs = build(versions)

  case Firefox extends BrowserCategory("Firefox", Gecko, "Firefox/(\\S*)->$1", "Firefox")

  case Thunderbird extends BrowserCategory("Thunderbird", Gecko, "Thunderbird/(\\S*)->$1", "Thunderbird")

  case FirefoxMobile extends BrowserCategory("Firefox Mobile", Gecko, "Firefox/3.5 Maemo->3")

  case SeaMonkey extends BrowserCategory("SeaMonkey", Gecko, "SeaMonkey")

  case IE360 extends BrowserCategory("Internet Explorer 360", Trident, "360SE")

  case IE extends BrowserCategory("Internet Explorer", Trident, "MSIE (\\S*);->$1", "MSIE")

  case Edge extends BrowserCategory("Edge", WebKit, "Edg/(\\S*)->$1", "Edg")

  case WeChat extends BrowserCategory("WeChat", WebKit, "MicroMessenger/([\\d.]*)->$1")

  case AliPay extends BrowserCategory("AliPay", WebKit, "AlipayClient/([\\d.]*)->$1")

  case Sogo extends BrowserCategory("Sogo", WebKit, "SE(.*)MetaSr")

  case Maxthon extends BrowserCategory("Maxthon", WebKit, "Maxthon/(\\S*)->$1", "Maxthon")

  case UC extends BrowserCategory("UC", WebKit, "UBrowser/([\\d.]*)->$1")

  case Chrome extends BrowserCategory("Chrome", WebKit, "Chrome/(\\S*)->$1", "Chrome")

  case Safari extends BrowserCategory("Safari", WebKit, "Version/(\\S*) Safari->$1", "Safari")

  case AppleMail extends BrowserCategory("Apple Mail", WebKit, "AppleWebKit")

  case ChromeMobile extends BrowserCategory("Chrome Mobile", WebKit, "CrMo/(\\S*)->$1")

  case SafariMobile extends BrowserCategory("Mobile Safari", WebKit, "Mobile Safari", "Mobile/5A347 Safari",
    "Mobile/3A101a Safari", "Mobile/7B367 Safari")

  case Opera extends BrowserCategory("Opera", WebKit, "Opera/(.*?)Version/(\\S*)->$2", "Opera Mini->Mini", "Opera")

  case QQ extends BrowserCategory("QQ", WebKit, "QQBrowser/([\\d.]*)->$1")

  case Konqueror extends BrowserCategory("Konqueror", Khtml, "Konqueror")

  case Outlook extends BrowserCategory("Outlook", Word, "MSOffice 12->2007", "MSOffice 14->2010", "MSOffice")

  case Mozilla extends BrowserCategory("Mozilla", Other, "Mozilla", "Moozilla")

  case Lynx extends BrowserCategory("Lynx", Other, "Lynx")

  case Download extends BrowserCategory("Downloading Tool", Other, "cURL", "wget")

  case Unknown extends BrowserCategory("Unknown", Other)

  engine.addCategory(this)

  private def build(versions: Seq[String]): List[Tuple2[Pattern, String]] = {
    val pairs = new collection.mutable.ListBuffer[Tuple2[Pattern, String]]
    for (version <- versions) {
      var matcheTarget = version
      var versionNum = ""
      if (Strings.contains(version, "->")) {
        matcheTarget = "(?i)" + Strings.substringBefore(version, "->")
        versionNum = Strings.substringAfter(version, "->")
      }
      pairs += Tuple2(Pattern.compile(matcheTarget), versionNum)
    }
    pairs.toList
  }

  def matches(agentString: String): String = {
    var matched: String = null
    val pairIter = versionPairs.iterator
    while (pairIter.hasNext && matched == null) {
      val pair = pairIter.next()
      val m = pair._1.matcher(agentString)
      if (m.find()) {
        val sb = new StringBuffer()
        m.appendReplacement(sb, pair._2)
        sb.delete(0, m.start())
        matched = sb.toString
      }
    }
    matched
  }

}
