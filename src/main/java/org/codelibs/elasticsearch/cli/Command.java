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

package org.codelibs.elasticsearch.cli;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.codelibs.elasticsearch.querybuilders.mock.log4j.Level;
import org.apache.lucene.util.SetOnce;
import org.codelibs.elasticsearch.common.SuppressForbidden;
import org.codelibs.elasticsearch.common.logging.LogConfigurator;
import org.codelibs.elasticsearch.common.settings.Settings;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * An action to execute within a cli.
 */
public abstract class Command implements Closeable {

    /** A description of the command, used in the help output. */
    protected final String description;

    /** The option parser for this command. */
    protected final OptionParser parser = new OptionParser();

    private final OptionSpec<Void> helpOption = parser.acceptsAll(Arrays.asList("h", "help"), "show help").forHelp();
    private final OptionSpec<Void> silentOption = parser.acceptsAll(Arrays.asList("s", "silent"), "show minimal output");
    private final OptionSpec<Void> verboseOption =
        parser.acceptsAll(Arrays.asList("v", "verbose"), "show verbose output").availableUnless(silentOption);

    public Command(String description) {
        this.description = description;
    }

    final SetOnce<Thread> shutdownHookThread = new SetOnce<>();

    /** Parses options for this command from args and executes it. */
    public final int main(String[] args, Terminal terminal) throws Exception {
        if (addShutdownHook()) {
            shutdownHookThread.set(new Thread(() -> {
                try {
                    this.close();
                } catch (final IOException e) {
                    try (
                        final StringWriter sw = new StringWriter();
                        final PrintWriter pw = new PrintWriter(sw)) {
                        e.printStackTrace(pw);
                        terminal.println(sw.toString());
                    } catch (final IOException impossible) {
                        // StringWriter#close declares a checked IOException from the Closeable interface but the Javadocs for StringWriter
                        // say that an exception here is impossible
                        throw new AssertionError(impossible);
                    }
                }
            }));
            Runtime.getRuntime().addShutdownHook(shutdownHookThread.get());
        }

        // initialize default for es.logger.level because we will not read the log4j2.properties
        final String loggerLevel = System.getProperty("es.logger.level", Level.INFO.name());
        final Settings settings = Settings.builder().put("logger.level", loggerLevel).build();
        LogConfigurator.configureWithoutConfig(settings);

        try {
            mainWithoutErrorHandling(args, terminal);
        } catch (OptionException e) {
            printHelp(terminal);
            terminal.println(Terminal.Verbosity.SILENT, "ERROR: " + e.getMessage());
            return ExitCodes.USAGE;
        } catch (UserException e) {
            if (e.exitCode == ExitCodes.USAGE) {
                printHelp(terminal);
            }
            terminal.println(Terminal.Verbosity.SILENT, "ERROR: " + e.getMessage());
            return e.exitCode;
        }
        return ExitCodes.OK;
    }

    /**
     * Executes the command, but all errors are thrown.
     */
    void mainWithoutErrorHandling(String[] args, Terminal terminal) throws Exception {
        final OptionSet options = parser.parse(args);

        if (options.has(helpOption)) {
            printHelp(terminal);
            return;
        }

        if (options.has(silentOption)) {
            terminal.setVerbosity(Terminal.Verbosity.SILENT);
        } else if (options.has(verboseOption)) {
            terminal.setVerbosity(Terminal.Verbosity.VERBOSE);
        } else {
            terminal.setVerbosity(Terminal.Verbosity.NORMAL);
        }

        execute(terminal, options);
    }

    /** Prints a help message for the command to the terminal. */
    private void printHelp(Terminal terminal) throws IOException {
        terminal.println(description);
        terminal.println("");
        printAdditionalHelp(terminal);
        parser.printHelpOn(terminal.getWriter());
    }

    /** Prints additional help information, specific to the command */
    protected void printAdditionalHelp(Terminal terminal) {}

    @SuppressForbidden(reason = "Allowed to exit explicitly from #main()")
    protected static void exit(int status) {
        System.exit(status);
    }

    /**
     * Executes this command.
     *
     * Any runtime user errors (like an input file that does not exist), should throw a {@link UserException}. */
    protected abstract void execute(Terminal terminal, OptionSet options) throws Exception;

    /**
     * Return whether or not to install the shutdown hook to cleanup resources on exit. This method should only be overridden in test
     * classes.
     *
     * @return whether or not to install the shutdown hook
     */
    protected boolean addShutdownHook() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }

}
