


package org.codelibs.elasticsearch.querybuilders.mock.log4j.core.config;

import java.util.Map;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.Level;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.core.Appender;

public class LoggerConfig {
    public LoggerConfig(final String name, final Level level, final boolean additive) {
    }

    public void setLevel(final Level level) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public void addAppender(final Appender appender, final Level level, final Object filter) {
        throw new UnsupportedOperationException();
    }

    public Map<String, Appender> getAppenders() {
        throw new UnsupportedOperationException();
    }

    public void removeAppender(final String name) {
        throw new UnsupportedOperationException();
    }
}
