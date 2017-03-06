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
package org.apache.logging.log4j.core.config;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.Appender;

public interface Configuration {

    /**
     * Returns the configuration name.
     *
     * @return the name of the configuration.
     */
    String getName();

    /**
     * Locates the appropriate LoggerConfig for a Logger name. This will remove tokens from the package name as
     * necessary or return the root LoggerConfig if no other matches were found.
     *
     * @param name The Logger name.
     * @return The located LoggerConfig.
     */
    LoggerConfig getLoggerConfig(String name);

    /**
     * Returns the Appender with the specified name.
     *
     * @param <T>  The expected Appender type.
     * @param name The name of the Appender.
     * @return the Appender with the specified name or null if the Appender cannot be located.
     */
    <T extends Appender> T getAppender(String name);

    /**
     * Returns a Map containing all the Appenders and their name.
     *
     * @return A Map containing each Appender's name and the Appender object.
     */
    Map<String, Appender> getAppenders();

    void addAppender(final Appender appender);

    Map<String, LoggerConfig> getLoggers();

    void addLogger(final String name, final LoggerConfig loggerConfig);

    /**
     * Returns the list of packages to scan for plugins for this Configuration.
     *
     * @return the list of plugin packages.
     * @since 2.1
     */
    List<String> getPluginPackages();

    Map<String, String> getProperties();

    /**
     * Returns the root Logger.
     *
     * @return the root Logger.
     */
    LoggerConfig getRootLogger();

    <T> T getComponent(String name);

    void addComponent(String name, Object object);

    boolean isShutdownHookEnabled();

    long getShutdownTimeoutMillis();
}
