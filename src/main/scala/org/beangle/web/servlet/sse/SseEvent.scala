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

import scala.collection.mutable

/**
 * 按 SSE（Server-Sent Events）约定构造一条事件消息，支持链式调用。
 *
 * 字段对应关系：`name` → `event:`，`id` → `id:`，`data` → `data:`（多行会拆成多行 `data:`），
 * `comment` → 以 `:` 开头的注释行，`retry` → `retry:`（毫秒，用于客户端重连间隔）。
 *
 * 使用 [[SseEvent.name]]、[[SseEvent.data]] 等入口构造，再链式补充其它字段；
 * 调用 `build()` 得到各字段行（每行以 `\n` 结束，**不含** SSE 用于结束事件的空行）；
 * 换行与事件结束空行由 [[SseWriter.send]] 在写出时统一处理。
 */
final class SseEvent private () {

  private var idOpt: Option[String] = None
  private var eventOpt: Option[String] = None
  private val dataLines = mutable.ArrayBuffer[String]()
  private val commentLines = mutable.ArrayBuffer[String]()
  private var retryOpt: Option[Int] = None

  /** `id:` 字段；若含换行会被压成单行。 */
  def id(value: String): this.type = {
    idOpt = Some(SseEvent.singleLineField(value))
    this
  }

  /** `event:` 字段（事件名）；若含换行会被压成单行。 */
  def name(value: String): this.type = {
    eventOpt = Some(SseEvent.singleLineField(value))
    this
  }

  /** 同 [[name]]，与 SSE 术语一致。 */
  def event(value: String): this.type = name(value)

  /**
   * `data:` 字段；可多次调用以追加多行。
   * 字符串内的换行会按 SSE 要求拆成多行 `data:`。
   */
  def data(value: String): this.type = {
    val normalized = SseEvent.normalizeMultiline(value)
    normalized.split("\n", -1).foreach(dataLines += _)
    this
  }

  /**
   * 注释行（以 `:` 开头、客户端忽略）；可多次调用。
   * 含换行时会拆成多条注释行。
   */
  def comment(value: String): this.type = {
    val normalized = SseEvent.normalizeMultiline(value)
    normalized.split("\n", -1).foreach(commentLines += _)
    this
  }

  /** `retry:` 字段，单位为毫秒。 */
  def retry(milliseconds: Int): this.type = {
    retryOpt = Some(milliseconds)
    this
  }

  /**
   * 渲染为 SSE 字段块：每行 `field: value\\n`，**不包含**事件结束所需的额外空行。
   */
  def build(): String = {
    val sb = new StringBuilder
    idOpt.foreach { v => sb.append("id: ").append(v).append('\n') }
    eventOpt.foreach { v => sb.append("event: ").append(v).append('\n') }
    retryOpt.foreach { ms => sb.append("retry: ").append(ms).append('\n') }
    commentLines.foreach { line => sb.append(':').append(line).append('\n') }
    dataLines.foreach { line => sb.append("data: ").append(line).append('\n') }
    sb.toString()
  }
}

object SseEvent {

  /** 从 `event:` 字段开始构造，可继续链式调用 [[SseEvent]] 上其它方法。 */
  def name(value: String): SseEvent = new SseEvent().name(value)

  /** 从 `data:` 字段开始构造，可继续链式调用 [[SseEvent]] 上其它方法。 */
  def data(value: String): SseEvent = new SseEvent().data(value)

  private def normalizeMultiline(value: String): String = {
    val n = Option(value).getOrElse("")
      .replace("\r\n", "\n")
      .replace('\r', '\n')
    n.stripSuffix("\n")
  }

  private def singleLineField(value: String): String = {
    val n = normalizeMultiline(value)
    n.replace('\n', ' ').trim
  }
}
