/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codelibs.elasticsearch.common.logging;

import org.codelibs.elasticsearch.querybuilders.log4j.Level;
import org.codelibs.elasticsearch.querybuilders.log4j.Logger;
import org.codelibs.elasticsearch.querybuilders.log4j.Marker;
import org.codelibs.elasticsearch.querybuilders.log4j.MockLogger;
import org.codelibs.elasticsearch.querybuilders.log4j.message.Message;
import org.codelibs.elasticsearch.querybuilders.log4j.spi.ExtendedLogger;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

class PrefixLogger extends MockLogger {
    private static final WeakHashMap<String, WeakReference<Marker>> markers = new WeakHashMap<>();


    public String prefix() {
        throw new UnsupportedOperationException();
    }

    PrefixLogger(final ExtendedLogger logger, final String name, final String prefix) {
    }

    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message message, final Throwable t) {
        throw new UnsupportedOperationException();
    }
}
