


package org.codelibs.elasticsearch.querybuilders.mock.log4j.core.config;

import java.util.List;
import java.util.Map;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.core.Appender;

public interface Configuration {


    String getName();


    LoggerConfig getLoggerConfig(String name);


    <T extends Appender> T getAppender(String name);


    Map<String, Appender> getAppenders();

    void addAppender(final Appender appender);

    Map<String, LoggerConfig> getLoggers();

    void addLogger(final String name, final LoggerConfig loggerConfig);


    List<String> getPluginPackages();

    Map<String, String> getProperties();


    LoggerConfig getRootLogger();

    <T> T getComponent(String name);

    void addComponent(String name, Object object);

    boolean isShutdownHookEnabled();

    long getShutdownTimeoutMillis();
}
