package org.codelibs.elasticsearch.querybuilders.mock.log4j.spi;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.core.config.Configuration;

public class LoggerContext {
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void updateLoggers() {
        throw new UnsupportedOperationException();
    }
}
