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
package org.codelibs.elasticsearch.querybuilders.log4j.core;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.codelibs.elasticsearch.querybuilders.log4j.LogManager;
import org.codelibs.elasticsearch.querybuilders.log4j.core.config.Configuration;

/**
 * The LoggerContext is the anchor for the logging system. It maintains a list of all the loggers requested by
 * applications and a reference to the Configuration. The Configuration will contain the configured loggers, appenders,
 * filters, etc and will be atomically updated whenever a reconfigure occurs.
 */
public class LoggerContext  extends org.codelibs.elasticsearch.querybuilders.log4j.spi.LoggerContext {
    public LoggerContext(final String name) {
        this(name, null, (URI) null);
    }

    /**
     * Constructor taking a name and a reference to an external context.
     *
     * @param name The context name.
     * @param externalContext The external context.
     */
    public LoggerContext(final String name, final Object externalContext) {
        this(name, externalContext, (URI) null);
    }

    /**
     * Constructor taking a name, external context and a configuration URI.
     *
     * @param name The context name.
     * @param externalContext The external context.
     * @param configLocn The location of the configuration as a URI.
     */
    public LoggerContext(final String name, final Object externalContext, final URI configLocn) {
    }

    /**
     * Constructor taking a name external context and a configuration location String. The location must be resolvable
     * to a File.
     *
     * @param name The configuration location.
     * @param externalContext The external context.
     * @param configLocn The configuration location.
     */
    public LoggerContext(final String name, final Object externalContext, final String configLocn) {
    }

    /**
     * Returns the current LoggerContext.
     * <p>
     * Avoids the type cast for:
     * </p>
     *
     * <pre>
     * (LoggerContext) LogManager.getContext();
     * </pre>
     *
     * <p>
     * WARNING - The LoggerContext returned by this method may not be the LoggerContext used to create a Logger for the
     * calling class.
     * </p>
     *
     * @return The current LoggerContext.
     * @see LogManager#getContext()
     */
    public static LoggerContext getContext() {
        return (LoggerContext) LogManager.getContext();
    }

    /**
     * Returns a LoggerContext.
     * <p>
     * Avoids the type cast for:
     * </p>
     *
     * <pre>
     * (LoggerContext) LogManager.getContext(currentContext);
     * </pre>
     *
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     * @see LogManager#getContext(boolean)
     */
    public static LoggerContext getContext(final boolean currentContext) {
        return (LoggerContext) LogManager.getContext(currentContext);
    }

    /**
     * Returns a LoggerContext.
     * <p>
     * Avoids the type cast for:
     * </p>
     *
     * <pre>
     * (LoggerContext) LogManager.getContext(loader, currentContext, configLocation);
     * </pre>
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param configLocation The URI for the configuration to use.
     * @return a LoggerContext.
     * @see LogManager#getContext(ClassLoader, boolean, URI)
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final URI configLocation) {
        return (LoggerContext) LogManager.getContext(loader, currentContext, configLocation);
    }

    public void start() {
    }

    /**
     * Starts with a specific configuration.
     *
     * @param config The new Configuration.
     */
    public void start(final Configuration config) {
    }

    private void setUpShutdownHook() {
    }

    public void close() {
    }

    public void terminate() {
    }

    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        return true;
    }
}
