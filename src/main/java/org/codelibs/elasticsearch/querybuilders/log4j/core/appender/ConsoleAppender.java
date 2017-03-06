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
package org.codelibs.elasticsearch.querybuilders.log4j.core.appender;

import java.nio.charset.Charset;

import org.codelibs.elasticsearch.querybuilders.log4j.core.Appender;

public final class ConsoleAppender implements Appender {

    public enum Target {
        /** Standard output. */
        SYSTEM_OUT {
            @Override
            public Charset getDefaultCharset() {
                return getCharset("sun.stdout.encoding");
            }
        },
        /** Standard error output. */
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
