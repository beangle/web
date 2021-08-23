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

import java.util.regex.{Matcher, Pattern}
import scala.collection.mutable

enum OsCategory(val name: String, versions: String*) {
  private val versionPairs = new mutable.ListBuffer[Tuple2[Pattern, String]]
  for (version <- versions) {
    var matcheTarget = version
    var versionNum = ""
    if (Strings.contains(version, "->")) {
      matcheTarget = "(?i)" + Strings.substringBefore(version, "->")
      versionNum = Strings.substringAfter(version, "->")
    }
    versionPairs += Tuple2(Pattern.compile(matcheTarget), versionNum)
  }

  case Windows extends OsCategory("Windows", "Windows NT 6.1->7", "Windows NT 6->Vista", "Windows NT 5.0->2000",
    "Windows NT 5->XP", "Win98->98", "Windows 98->98", "Windows Phone OS 7->Mobile 7", "Windows CE->Mobile",
    "Windows")

  case Android extends OsCategory("Android", "Android-4->4.x", "Android 4->4.x", "Xoom->4.x Tablet", "Transformer->4.x Tablet",
    "Android 3->3.x Tablet", "Android 2->2.x", "Kindle Fire->2.x Tablet", "GT-P1000->2.x Tablet", "SCH-I800->2.x Tablet",
    "Android 1->1.x", "GoogleTV->(Google TV)", "Android")

  case Linux extends OsCategory("Linux", "Fedora/(\\S*)\\.fc(\\S*)->Fedora fc$2", "Ubuntu/(\\S*)->Ubuntu $1",
    "Fedora", "Ubuntu", "Linux", "CamelHttpStream")

  case Webos extends OsCategory("WebOS", "webOS")

  case Palm extends OsCategory("PalmOS", "Palm")

  case Ios extends OsCategory("iOS", "iPhone OS(\\S*)->$1 (iPhone)", "like Mac OS X", "iOS")

  case MacOs extends OsCategory("Mac OS", "iPad->(iPad)", "iPhone->(iPhone)", "iPod->(iPod)", "Mac OS X->X",
    "CFNetwork->X", "Mac")

  case Maemo extends OsCategory("Maemo", "Maemo")

  case Bada extends OsCategory("Bada", "Bada")

  case Kindle extends OsCategory("Kindle", "Kindle/(\\S*)->(Kindle $1)", "Kindle")

  case Symbian extends OsCategory("Symbian OS", "SymbianOS/(\\S*)->$1", "Series60/3->9.x", "Series60/2.6->8.x",
    "Series60/2.8->8.x", "Symbian", "Series60")

  case Series40 extends OsCategory("Series 40", "Nokia6300")

  case SonyEricsson extends OsCategory("Sony Ericsson", "SonyEricsson")

  case SunOs extends OsCategory("SunOS", "SunOS")

  case Psp extends OsCategory("Sony Playstation", "Playstation")

  case Wii extends OsCategory("Nintendo Wii", "Wii")

  case BlackBerry extends OsCategory("BlackBerryOS", "(BB|BlackBerry|PlayBook)(.*?)Version/(\\S*)->$3",
    "BlackBerry")

  case Roku extends OsCategory("Roku OS", "Roku")

  case Unknown extends OsCategory("Unknown")


  def matches(agentString: String): String = {
    val entryItor = versionPairs.iterator
    while (entryItor.hasNext) {
      val entry = entryItor.next()
      val m = entry._1.matcher(agentString)
      if (m.find()) {
        val sb = new StringBuffer()
        m.appendReplacement(sb, entry._2)
        sb.delete(0, m.start())
        return sb.toString
      }
    }
    null
  }

}
