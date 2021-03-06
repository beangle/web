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

enum BrowserCategory(val name: String, val engine: Engine, versions: String*) {
  private val versionPairs = build(versions)

  case Firefox extends BrowserCategory("Firefox", Gecko, "Firefox/(\\S*)->$1", "Firefox")

  case Thunderbird extends BrowserCategory("Thunderbird", Gecko, "Thunderbird/(\\S*)->$1", "Thunderbird")

  case Camino extends BrowserCategory("Camino", Gecko, "Camino/(\\S*)->$1", "Camino")

  case Flock extends BrowserCategory("Flock", Gecko, "Flock/(\\S*)->$1")

  case FirefoxMobile extends BrowserCategory("Firefox Mobile", Gecko, "Firefox/3.5 Maemo->3")

  case SeaMonkey extends BrowserCategory("SeaMonkey", Gecko, "SeaMonkey")

  case Tencent extends BrowserCategory("Tencent Traveler", Trident, "TencentTraveler (\\S*);->$1")

  case Sogo extends BrowserCategory("Sogo", Trident, "SE(.*)MetaSr")

  case TheWorld extends BrowserCategory("The World", Trident, "theworld")

  case IE360 extends BrowserCategory("Internet Explorer 360", Trident, "360SE")

  case IeMobile extends BrowserCategory("IE Mobile", Trident, "IEMobile (\\S*)->$1")

  case IE extends BrowserCategory("Internet Explorer", Trident, "MSIE (\\S*);->$1", "MSIE")

  case OutlookExpress extends BrowserCategory("Windows Live Mail", Trident, "Outlook-Express/7.0->7.0")

  case Maxthon extends BrowserCategory("Maxthon", WebKit, "Maxthon/(\\S*)->$1", "Maxthon")

  case Chrome extends BrowserCategory("Chrome", WebKit, "Chrome/(\\S*)->$1", "Chrome")

  case Safari extends BrowserCategory("Safari", WebKit, "Version/(\\S*) Safari->$1", "Safari")

  case Omniweb extends BrowserCategory("Omniweb", WebKit, "OmniWeb")

  case AppleMail extends BrowserCategory("Apple Mail", WebKit, "AppleWebKit")

  case ChromeMobile extends BrowserCategory("Chrome Mobile", WebKit, "CrMo/(\\S*)->$1")

  case SafariMobile extends BrowserCategory("Mobile Safari", WebKit, "Mobile Safari", "Mobile/5A347 Safari",
    "Mobile/3A101a Safari", "Mobile/7B367 Safari")

  case Silk extends BrowserCategory("Silk", WebKit, "Silk/(\\S*)->$1")

  case Dolfin extends BrowserCategory("Samsung Dolphin", WebKit, "Dolfin/(\\S*)->$1")

  case Opera extends BrowserCategory("Opera", Presto, "Opera/(.*?)Version/(\\S*)->$2", "Opera Mini->Mini",
    "Opera")

  case Konqueror extends BrowserCategory("Konqueror", Khtml, "Konqueror")

  case Outlook extends BrowserCategory("Outlook", Word, "MSOffice 12->2007", "MSOffice 14->2010", "MSOffice")

  case LotusNotes extends BrowserCategory("Lotus Notes", Other, "Lotus-Notes")

  case Bot extends BrowserCategory("Robot/Spider", Other, "Googlebot", "bot", "spider", "crawler", "Feedfetcher",
    "Slurp", "Twiceler", "Nutch", "BecomeBot")

  case Mozilla extends BrowserCategory("Mozilla", Other, "Mozilla", "Moozilla")

  case CFNetwork extends BrowserCategory("CFNetwork", Other, "CFNetwork")

  case Eudora extends BrowserCategory("Eudora", Other, "Eudora", "EUDORA")

  case PocoMail extends BrowserCategory("PocoMail", Other, "PocoMail")

  case TheBat extends BrowserCategory("The Bat!", Other, "The Bat")

  case NetFront extends BrowserCategory("NetFront", Other, "NetFront")

  case Evolution extends BrowserCategory("Evolution", Other, "CamelHttpStream")

  case Lynx extends BrowserCategory("Lynx", Other, "Lynx")

  case UC extends BrowserCategory("UC", Other, "UCWEB")

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
    for (pair <- versionPairs) {
      val m = pair._1.matcher(agentString)
      if (m.find()) {
        val sb = new StringBuffer()
        m.appendReplacement(sb, pair._2)
        sb.delete(0, m.start())
        return sb.toString
      }
    }
    null
  }

}
