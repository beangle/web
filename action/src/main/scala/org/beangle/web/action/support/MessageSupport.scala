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

package org.beangle.web.action.support

import org.beangle.commons.lang.{Chars, Strings}
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.context.{ActionContext, Flash}
import org.beangle.web.action.support.MessageSupport.{ErrorsKey, MessagesKey}

object MessageSupport {
  val MessagesKey = "messages"
  val ErrorsKey = "errors"
}

trait MessageSupport {

  protected final def getText(aTextName: String): String = {
    ActionContext.current.textProvider.apply(aTextName, aTextName)
  }

  protected final def getText(key: String, defaultValue: String, args: Any*): String = {
    ActionContext.current.textProvider.apply(key, defaultValue, args: _*)
  }

  protected final def getTextInternal(msgKey: String, args: Any*): String = {
    if (Strings.isEmpty(msgKey)) {
      null
    } else {
      if (Chars.isAsciiAlpha(msgKey.charAt(0)) && msgKey.indexOf('.') > 0) {
        getText(msgKey, msgKey, args: _*)
      } else {
        msgKey
      }
    }
  }

  protected final def addMessage(msgKey: String, args: Any*): Unit = {
    ActionContext.current.getFlash(true).appendNow(MessagesKey, getTextInternal(msgKey, args: _*))
  }

  protected final def addError(msgKey: String, args: Any*): Unit = {
    ActionContext.current.getFlash(true).appendNow(ErrorsKey, getTextInternal(msgKey, args: _*))
  }

  protected final def addFlashError(msgKey: String, args: Any*): Unit = {
    ActionContext.current.getFlash(true).append(ErrorsKey, getTextInternal(msgKey, args: _*))
  }

  protected final def addFlashMessage(msgKey: String, args: Any*): Unit = {
    ActionContext.current.getFlash(true).append(MessagesKey, getTextInternal(msgKey, args: _*))
  }

  /** 获得action消息
   */
  @ignore
  protected final def actionMessages: List[String] = getFlashMsgs(MessagesKey)

  /**
   * 获得aciton错误消息<br>
   */
  @ignore
  protected final def actionErrors: List[String] = getFlashMsgs(ErrorsKey)

  private def getFlashMsgs(key: String): List[String] = {
    val flash = ActionContext.current.getFlash(false)
    if null == flash then
      List.empty
    else
      val messages = flash.get(key)
      if (null == messages) List()
      else Strings.split(messages, ';').toList
  }
}
