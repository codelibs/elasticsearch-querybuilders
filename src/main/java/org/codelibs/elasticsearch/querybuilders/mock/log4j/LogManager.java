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
package org.codelibs.elasticsearch.querybuilders.mock.log4j;

import java.net.URI;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.spi.LoggerContext;

public class LogManager {

    /**
     * The name of the root Logger.
     */
    public static final String ROOT_LOGGER_NAME = "";

    /**
     * Prevents instantiation
     */
    protected LogManager() {
    }

    public static boolean exists(final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the current LoggerContext.
     * <p>
     * WARNING - The LoggerContext returned by this method may not be the LoggerContext used to create a Logger for the
     * calling class.
     * </p>
     *
     * @return The current LoggerContext.
     */
    public static LoggerContext getContext() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext.
     *
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    public static LoggerContext getContext(final boolean currentContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * @return a LoggerContext.
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final Object externalContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param configLocation The URI for the configuration to use.
     * @return a LoggerContext.
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final URI configLocation) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * @param configLocation The URI for the configuration to use.
     * @return a LoggerContext.
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final Object externalContext, final URI configLocation) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * @param configLocation The URI for the configuration to use.
     * @param name The LoggerContext name.
     * @return a LoggerContext.
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final Object externalContext, final URI configLocation, final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext
     *
     * @param fqcn The fully qualified class name of the Class that this method is a member of.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    protected static LoggerContext getContext(final String fqcn, final boolean currentContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a LoggerContext
     *
     * @param fqcn The fully qualified class name of the Class that this method is a member of.
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    protected static LoggerContext getContext(final String fqcn, final ClassLoader loader,
                                              final boolean currentContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Shutdown using the LoggerContext appropriate for the caller of this method.
     * This is equivalent to calling {@code LogManager.shutdown(false)}.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @since 2.6
     */
    public static void shutdown() {
        shutdown(false);
    }

    /**
     * Shutdown the logging system if the logging system supports it.
     * This is equivalent to calling {@code LogManager.shutdown(LogManager.getContext(currentContext))}.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @param currentContext if true a default LoggerContext (may not be the LoggerContext used to create a Logger
     *            for the calling class) will be used.
     *            If false the LoggerContext appropriate for the caller of this method is used. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            used and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be used.
     * @since 2.6
     */
    public static void shutdown(final boolean currentContext) {
        shutdown(getContext(currentContext));
    }

    /**
     * Shutdown the logging system if the logging system supports it.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @param context the LoggerContext.
     * @since 2.6
     */
    public static void shutdown(final LoggerContext context) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @return The Logger for the calling class.
     * @throws UnsupportedOperationException if the calling class cannot be determined.
     */
    public static Logger getLogger() {
        return new MockLogger();
    }

    /**
     * Returns a Logger using the fully qualified name of the Class as the Logger name.
     *
     * @param clazz The Class whose name should be used as the Logger name. If null it will default to the calling
     *            class.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code clazz} is {@code null} and the calling class cannot be
     *             determined.
     */
    public static Logger getLogger(final Class<?> clazz) {
        return new MockLogger();
    }

    /**
     * Returns a Logger using the fully qualified class name of the value as the Logger name.
     *
     * @param value The value whose class name should be used as the Logger name. If null the name of the calling class
     *            will be used as the logger name.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code value} is {@code null} and the calling class cannot be
     *             determined.
     */
    public static Logger getLogger(final Object value) {
        return new MockLogger();
    }


    /**
     * Returns a Logger with the specified name.
     *
     * @param name The logger name. If null the name of the calling class will be used.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code name} is {@code null} and the calling class cannot be determined.
     */
    public static Logger getLogger(final String name) {
        return new MockLogger();
    }


    /**
     * Returns a Logger with the specified name.
     *
     * @param fqcn The fully qualified class name of the class that this method is a member of.
     * @param name The logger name.
     * @return The Logger.
     */
    protected static Logger getLogger(final String fqcn, final String name) {
        return new MockLogger();
    }

    /**
     * Returns the root logger.
     *
     * @return the root logger, named {@link #ROOT_LOGGER_NAME}.
     */
    public static Logger getRootLogger() {
        return getLogger(ROOT_LOGGER_NAME);
    }
}
