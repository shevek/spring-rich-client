package org.springframework.richclient.exceptionhandling;

import java.util.logging.Level;

/**
 * This enum is not available in Logging commons, but it should be.
 * @author Geoffrey De Smet
 * @since 0.3
 */
public enum LogLevel {
    TRACE(Level.FINEST),
    DEBUG(Level.FINER),
    INFO(Level.INFO),
    WARN(Level.WARNING),
    ERROR(Level.SEVERE),
    FATAL(Level.SEVERE);

    private final Level jdkLogLevel;

    private LogLevel(Level jdkLogLevel)
    {
        this.jdkLogLevel = jdkLogLevel;
    }

    public Level getJdkLogLevel()
    {
        return jdkLogLevel;
    }

}
