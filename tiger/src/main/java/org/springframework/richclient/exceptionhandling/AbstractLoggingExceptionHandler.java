package org.springframework.richclient.exceptionhandling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.exceptionhandling.delegation.ExceptionPurger;

/**
 * Superclass of logging exception handlers.
 * It handles a throwable by logging it and notify it to the user.
 * Subclasses determine how it's notified to the user.
 * @author Geoffrey De Smet
 * @since 0.3
 */
public abstract class AbstractLoggingExceptionHandler extends AbstractRegisterableExceptionHandler {

    protected static final String LOG_MESSAGE = "Uncaught throwable handled";

    protected final transient Log logger = LogFactory.getLog(getClass());

    protected LogLevel logLevel = LogLevel.ERROR;
    protected ExceptionPurger exceptionPurger = null;

    /**
     * The log level at which the throwable should be logged.
     * The default is ERROR.
     * @param logLevel the
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
    
    /**
     * If set the throwable will first be purged before handling it.
     * @param exceptionPurger
     */
    public void setExceptionPurger(ExceptionPurger exceptionPurger) {
        this.exceptionPurger = exceptionPurger;
    }

    /**
     * Logs an exception and shows it to the user.
     */
    public final void uncaughtException(Thread thread, Throwable throwable) {
        if (exceptionPurger != null) {
            throwable = exceptionPurger.purge(throwable);
        }
        logException(thread, throwable);
        notifyUserAboutException(thread, throwable);
    }

    /**
     * Log an exception
     */
    public void logException(Thread thread, Throwable throwable) {
        switch (logLevel) {
            case TRACE:
                logger.trace(LOG_MESSAGE, throwable);
                break;
            case DEBUG:
                logger.debug(LOG_MESSAGE, throwable);
                break;
            case INFO:
                logger.info(LOG_MESSAGE, throwable);
                break;
            case WARN:
                logger.warn(LOG_MESSAGE, throwable);
                break;
            case ERROR:
                logger.error(LOG_MESSAGE, throwable);
                break;
            case FATAL:
                logger.fatal(LOG_MESSAGE, throwable);
                break;
            default:
                logger.error("Unrecognized log level (" + logLevel + ") for throwable", throwable);
        }
    }

    /**
     * Notify user about an exception
     */
    public abstract void notifyUserAboutException(Thread thread, Throwable throwable);

}
