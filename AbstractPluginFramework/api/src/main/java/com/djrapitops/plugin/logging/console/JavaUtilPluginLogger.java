package com.djrapitops.plugin.logging.console;

import com.djrapitops.plugin.logging.L;
import com.djrapitops.plugin.logging.debug.DebugLogger;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic {@link PluginLogger} implementation for platforms using {@link Logger} as logging solution.
 *
 * @author Rsl1222
 */
public class JavaUtilPluginLogger implements PluginLogger {

    protected final Consumer<String> console;
    protected final Supplier<DebugLogger> debugLogger;
    protected final Supplier<Logger> logger;

    /**
     * Create a new JavaUtilPluginLogger.
     *
     * @param console     Consumer of the logging method for colored messages on the console.
     * @param debugLogger {@link DebugLogger} to log all channels on.
     * @param logger      plugin logger for logging messages.
     */
    public JavaUtilPluginLogger(Consumer<String> console, Supplier<DebugLogger> debugLogger, Logger logger) {
        this(console, debugLogger, () -> logger);
    }

    /**
     * Create a new JavaUtilPluginLogger.
     *
     * @param console     Consumer of the logging method for colored messages on the console.
     * @param debugLogger {@link DebugLogger} to log all channels on.
     * @param logger      Supplier for plugin logger for logging messages.
     */
    public JavaUtilPluginLogger(Consumer<String> console, Supplier<DebugLogger> debugLogger, Supplier<Logger> logger) {
        this.console = console;
        this.debugLogger = debugLogger;
        this.logger = logger;
    }

    @Override
    public void log(L level, String... message) {
        if (level == L.DEBUG) {
            debugLogger.get().log(message);
            return;
        } else if (level != L.DEBUG_INFO) {
            log(L.DEBUG, message);
        }
        switch (level) {
            case CRITICAL:
            case ERROR:
                for (String line : message) {
                    logger.get().log(Level.SEVERE, line);
                }
                break;
            case WARN:
                for (String line : message) {
                    logger.get().log(Level.WARNING, line);
                }
                break;
            case INFO_COLOR:
                for (String line : message) {
                    console.accept(line);
                }
                break;
            case DEBUG_INFO:
                for (String line : message) {
                    logger.get().log(Level.INFO, "[DEBUG] {0}", line);
                }
                break;
            case INFO:
            default:
                for (String line : message) {
                    logger.get().log(Level.INFO, line);
                }
                break;
        }
    }

    @Override
    public void log(L level, String message, Throwable throwable) {
        switch (level) {
            case CRITICAL:
            case ERROR:
                logger.get().log(Level.SEVERE, message, throwable);
                break;
            case WARN:
            default:
                logger.get().log(Level.WARNING, message, throwable);
                break;
        }
    }

    @Override
    public DebugLogger getDebugLogger() {
        return debugLogger.get();
    }
}