


package org.codelibs.elasticsearch.querybuilders.mock.log4j.core.appender;

import java.nio.charset.Charset;

import org.codelibs.elasticsearch.querybuilders.mock.log4j.core.Appender;

public final class ConsoleAppender implements Appender {

    public enum Target {

        SYSTEM_OUT {
            @Override
            public Charset getDefaultCharset() {
                return getCharset("sun.stdout.encoding");
            }
        },

        SYSTEM_ERR {
            @Override
            public Charset getDefaultCharset() {
                return getCharset("sun.stderr.encoding");
            }
        };

        public abstract Charset getDefaultCharset();

        protected Charset getCharset(final String property) {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public String getName() {
        return null;
    }
}
