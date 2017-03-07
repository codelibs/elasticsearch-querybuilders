


package org.codelibs.elasticsearch.querybuilders.mock.log4j.core.config;

import java.util.Map;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.Level;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.core.LoggerContext;


public final class Configurator {
    public static void shutdown(final LoggerContext ctx) {
        throw new UnsupportedOperationException();
    }

    public static void setLevel(final Map<String, Level> levelMap) {
        throw new UnsupportedOperationException();
    }

    public static void setLevel(final String loggerName, final Level level) {
    }
}
