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

package org.beangle.web.servlet.sse

import jakarta.servlet.http.HttpServletResponse

import java.io.IOException

/**
 * SSE 写出器；不负责请求超时。在异步 Servlet 中应在 [[jakarta.servlet.AsyncContext]] 上
 * 使用 `setTimeout` / `addListener` 处理超时，并在回调里 `complete()`、关闭流或通知业务。
 */
class SseWriter(resp: HttpServletResponse) {

  private var completionCallback: Option[() => Unit] = None
  private var errorCallback: Option[Throwable => Unit] = None
  private var completed: Boolean = false
  private var error: Throwable = _

  def onCompletion(callback: () => Unit): Unit = {
    completionCallback = Some(callback)
  }

  def onError(callback: Throwable => Unit): Unit = {
    errorCallback = Some(callback)
  }

  def complete(): Unit = {
    val firstClose = synchronized {
      if (!completed) {
        completed = true
        true
      } else false
    }
    if (firstClose) completionCallback.foreach(_())
  }

  def completeWithError(t: Throwable): Unit = {
    val firstClose = synchronized {
      if (!completed) {
        completed = true
        true
      } else false
    }
    if (firstClose) errorCallback.foreach(_(t))
  }

  /** 初始化 SSE 响应头。 */
  def start(): Unit = {
    resp.setContentType("text/event-stream;charset=UTF-8")
    resp.setCharacterEncoding("UTF-8")
    resp.setHeader("Cache-Control", "no-cache")
    resp.setHeader("Connection", "keep-alive")
  }

  /** 发送一条 SSE 注释，用于 keep-alive。 */
  def ping(comment: String = "ping"): Unit = {
    send(s": $comment")
  }

  /**
   * 发送默认事件（浏览器默认走 `onmessage`）。
   * 如果 data 内包含换行，会拆成多行 `data:`。
   */
  def sendData(data: String): Unit = {
    send(SseEvent.data(data))
  }

  /** 发送由 [[SseEvent]] 链式构造的一条 SSE 事件。 */
  def send(event: SseEvent): Unit = {
    send(event.build())
  }

  /**
   * 发送一段 SSE 字段文本（可为 [[SseEvent.build]] 结果，或自行拼好的 `field: line\\n`）。
   * 若末尾已是两个换行（`\\n\\n`）则不再改动；否则仅在末尾补齐，使以 `\\n\\n` 结束（不修改其它内容）。
   */
  def send(obj: String): Unit = {
    if (this.completed) throw new IOException("Writer already completed")
    if (this.error != null) throw new IOException("Writer already completed with error", this.error)
    try
      doSend(finishSseEvent(obj))
    catch {
      case ex: Exception =>
        synchronized {
          this.error = ex
          if (!completed) completed = true
        }
        errorCallback foreach (_(ex))
        throw ex
    }
  }

  private def doSend(obj: String): Unit = {
    val writer = resp.getWriter
    writer.write(obj)
    writer.flush()
  }

  /**
   * SSE 事件以空行结束（末尾两个 `\\n`）。已满足则原样返回，否则只在末尾补 `\\n` 直至满足。
   */
  private def finishSseEvent(body: String): String = {
    if (body.endsWith("\n\n")) body
    else if (body.endsWith("\n")) body + "\n"
    else body + "\n\n"
  }

}
