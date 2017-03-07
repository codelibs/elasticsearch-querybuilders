


package org.codelibs.elasticsearch.querybuilders.mock.log4j;

import java.net.URI;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.spi.LoggerContext;

public class LogManager {


    public static final String ROOT_LOGGER_NAME = "";


    protected LogManager() {
    }

    public static boolean exists(final String name) {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext() {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext(final boolean currentContext) {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext) {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final Object externalContext) {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final URI configLocation) {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final Object externalContext, final URI configLocation) {
        throw new UnsupportedOperationException();
    }


    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final Object externalContext, final URI configLocation, final String name) {
        throw new UnsupportedOperationException();
    }


    protected static LoggerContext getContext(final String fqcn, final boolean currentContext) {
        throw new UnsupportedOperationException();
    }


    protected static LoggerContext getContext(final String fqcn, final ClassLoader loader,
                                              final boolean currentContext) {
        throw new UnsupportedOperationException();
    }


    public static void shutdown() {
        shutdown(false);
    }


    public static void shutdown(final boolean currentContext) {
        shutdown(getContext(currentContext));
    }


    public static void shutdown(final LoggerContext context) {
        throw new UnsupportedOperationException();
    }


    public static Logger getLogger() {
        return new MockLogger();
    }


    public static Logger getLogger(final Class<?> clazz) {
        return new MockLogger();
    }


    public static Logger getLogger(final Object value) {
        return new MockLogger();
    }


    public static Logger getLogger(final String name) {
        return new MockLogger();
    }


    protected static Logger getLogger(final String fqcn, final String name) {
        return new MockLogger();
    }


    public static Logger getRootLogger() {
        return getLogger(ROOT_LOGGER_NAME);
    }
}
