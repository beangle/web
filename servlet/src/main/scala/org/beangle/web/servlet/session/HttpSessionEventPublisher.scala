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

package org.beangle.web.servlet.session

import org.beangle.commons.event.EventMulticaster

import jakarta.servlet.http.{ HttpSessionEvent, HttpSessionListener }

/**
 *
 * Publishes <code>HttpSessionApplicationEvent</code>s to the Bean Root Context.
 * Maps jakarta.servlet.http.HttpSessionListener.sessionCreated() to {@link HttpSessionCreationEvent}.
 * Maps jakarta.servlet.http.HttpSessionListener.sessionDestroyed() to
 * {@link HttpSessionDestroyedEvent}.
 */
class HttpSessionEventPublisher(em: EventMulticaster) extends HttpSessionListener {

  /**
   * Handles the HttpSessionEvent by publishing a {@link HttpSessionCreationEvent} to the
   * application appContext.
   *
   * @param event HttpSessionEvent passed in by the container
   */
  override def sessionCreated(event: HttpSessionEvent): Unit =
    em.multicast(new HttpSessionCreationEvent(event.getSession))

  /**
   * Handles the HttpSessionEvent by publishing a {@link HttpSessionDestroyedEvent} to the
   * application appContext.
   *
   * @param event The HttpSessionEvent pass in by the container
   */
  override def sessionDestroyed(event: HttpSessionEvent): Unit =
    em.multicast(new HttpSessionDestroyedEvent(event.getSession))
}
