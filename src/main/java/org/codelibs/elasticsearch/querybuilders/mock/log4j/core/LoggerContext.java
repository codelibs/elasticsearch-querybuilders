


package org.codelibs.elasticsearch.querybuilders.mock.log4j.core;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.LogManager;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.core.config.Configuration;


public class LoggerContext extends org.codelibs.elasticsearch.querybuilders.mock.log4j.spi.LoggerContext {
    public LoggerContext(final String name) {
        this(name, null, (URI) null);
    }


    public LoggerContext(final String name, final Object externalContext) {
        this(name, externalContext, (URI) null);
    }


    public LoggerContext(final String name, final Object externalContext, final URI configLocn) {
    }


    public LoggerContext(final String name, final Object externalContext, final String configLocn) {
    }


    public static LoggerContext getContext() {
        return (LoggerContext) LogManager.getContext();
    }


    public static LoggerContext getContext(final boolean currentContext) {
        return (LoggerContext) LogManager.getContext(currentContext);
    }


    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
                                           final URI configLocation) {
        return (LoggerContext) LogManager.getContext(loader, currentContext, configLocation);
    }

    public void start() {
    }


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
