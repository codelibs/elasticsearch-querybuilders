/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Levels used for identifying the severity of an event. Levels are organized from most specific to least:
 * <ul>
 * <li>{@link #OFF} (most specific, no logging)</li>
 * <li>{@link #FATAL} (most specific, little data)</li>
 * <li>{@link #ERROR}</li>
 * <li>{@link #WARN}</li>
 * <li>{@link #INFO}</li>
 * <li>{@link #DEBUG}</li>
 * <li>{@link #TRACE} (least specific, a lot of data)</li>
 * <li>{@link #ALL} (least specific, all data)</li>
 * </ul>
 *
 * Typically, configuring a level in a filter or on a logger will cause logging events of that level and those that are
 * more specific to pass through the filter. A special level, {@link #ALL}, is guaranteed to capture all levels when
 * used in logging configurations.
 */
public final class Level implements Comparable<Level>, Serializable {

    /**
     * No events will be logged.
     */
    public static Level OFF;

    /**
     * A severe error that will prevent the application from continuing.
     */
    public static Level FATAL;

    /**
     * An error in the application, possibly recoverable.
     */
    public static Level ERROR;

    /**
     * An event that might possible lead to an error.
     */
    public static Level WARN;

    /**
     * An event for informational purposes.
     */
    public static Level INFO = new Level("info", 0);

    /**
     * A general debugging event.
     */
    public static Level DEBUG;

    /**
     * A fine-grained debug message, typically capturing the flow through the application.
     */
    public static Level TRACE;

    /**
     * All events should be logged.
     */
    public static Level ALL;

    /**
     * @since 2.1
     */
    public static final String CATEGORY = "Level";

    private static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap<>(); // SUPPRESS CHECKSTYLE

    private static final long serialVersionUID = 1581082L;

    private Level(final String name, final int intLevel) {
    }

    public String name() {
        return null;
    }

    public static Level valueOf(final String name) {
        return INFO;
    }

    @Override
    public int compareTo(Level o) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    // CHECKSTYLE:OFF
    public Level clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    // CHECKSTYLE:ON
}
