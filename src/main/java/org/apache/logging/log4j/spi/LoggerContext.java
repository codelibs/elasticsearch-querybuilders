package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.core.config.Configuration;

public class LoggerContext {
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void updateLoggers() {
        throw new UnsupportedOperationException();
    }
}
