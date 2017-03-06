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
package org.codelibs.elasticsearch.querybuilders.log4j.core.config;

import java.util.Map;

import org.codelibs.elasticsearch.querybuilders.log4j.Level;
import org.codelibs.elasticsearch.querybuilders.log4j.LogManager;
import org.codelibs.elasticsearch.querybuilders.log4j.Marker;
import org.codelibs.elasticsearch.querybuilders.log4j.core.Appender;

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