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

import org.codelibs.elasticsearch.querybuilders.mock.log4j.Level;
import org.codelibs.elasticsearch.Version;
import org.codelibs.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LogConfigurator {

    /**
     * Configure logging without reading a log4j2.properties file, effectively configuring the
     * status logger and all loggers to the console.
     *
     * @param settings for configuring logger.level and individual loggers
     */
    public static void configureWithoutConfig(final Settings settings) {
        Objects.requireNonNull(settings);
        // we initialize the status logger immediately otherwise Log4j will complain when we try to get the context
        configureStatusLogger();
        configureLoggerLevels(settings);
    }

    private static void configureStatusLogger() {
        throw new UnsupportedOperationException();
    }

    private static void configureLoggerLevels(Settings settings) {
        if (ESLoggerFactory.LOG_DEFAULT_LEVEL_SETTING.exists(settings)) {
            final Level level = ESLoggerFactory.LOG_DEFAULT_LEVEL_SETTING.get(settings);
            Loggers.setLevel(ESLoggerFactory.getRootLogger(), level);
        }

        final Map<String, String> levels = settings.filter(ESLoggerFactory.LOG_LEVEL_SETTING::match).getAsMap();
        for (String key : levels.keySet()) {
            final Level level = ESLoggerFactory.LOG_LEVEL_SETTING.getConcreteSetting(key).get(settings);
            Loggers.setLevel(ESLoggerFactory.getLogger(key.substring("logger.".length())), level);
        }
    }

    private static void warnIfOldConfigurationFilePresent(final Path configsPath) throws IOException {
        // TODO: the warning for unsupported logging configurations can be removed in 6.0.0
        assert Version.CURRENT.major < 6;
        final List<String> suffixes = Arrays.asList(".yml", ".yaml", ".json", ".properties");
        final Set<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(configsPath, options, Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final String fileName = file.getFileName().toString();
                if (fileName.startsWith("logging")) {
                    for (final String suffix : suffixes) {
                        if (fileName.endsWith(suffix)) {
                            Loggers.getLogger(LogConfigurator.class).warn(
                                "ignoring unsupported logging configuration file [{}], logging is configured via [{}]",
                                file.toString(),
                                file.getParent().resolve("log4j2.properties"));
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
