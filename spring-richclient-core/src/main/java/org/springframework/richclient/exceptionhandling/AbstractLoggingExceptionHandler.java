package org.springframework.richclient.exceptionhandling;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.exceptionhandling.delegation.ExceptionPurger;
import org.springframework.core.ErrorCoded;

/**
 * Superclass of logging exception handlers.
 * It handles a throwable by logging it and notify it to the user.
 * Subclasses determine how it's notified to the user.
 * @author Geoffrey De Smet
 * @since 0.3
 */
public abstract class AbstractLoggingExceptionHandler extends AbstractRegisterableExceptionHandler {

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
        try {
            if (exceptionPurger != null) {
                throwable = exceptionPurger.purge(throwable);
            }
            logException(thread, throwable);
            notifyUserAboutException(thread, throwable);
        } catch (Throwable handlerThrowable) {
            // An exception handler must never throw a throwable itself
            // because if it (directly or transitively) handles that throwable it can create an infinite loop
            System.err.println("The ExceptionHandler handling an exception has thrown the following exception:");
            handlerThrowable.printStackTrace();
            // No logging because that could throw an exception
        }
    }

    protected String extractErrorCode(Throwable throwable) {
        if (throwable instanceof ErrorCoded) {
            return ((ErrorCoded) throwable).getErrorCode();
        } else if (throwable instanceof SQLException) {
            return Integer.toString(((SQLException) throwable).getErrorCode());
        } else {
            return null;
        }
    }

    /**
     * Log an exception
     */
    public void logException(Thread thread, Throwable throwable) {
        String logMessage;
        String errorCode = extractErrorCode(throwable);
        if (errorCode != null) {
            logMessage = "Uncaught throwable handled with errorCode (" + errorCode + ").";
        } else {
            logMessage = "Uncaught throwable handled.";
        }
        switch (logLevel) {
            case TRACE:
                logger.trace(logMessage, throwable);
                break;
            case DEBUG:
                logger.debug(logMessage, throwable);
                break;
            case INFO:
                logger.info(logMessage, throwable);
                break;
            case WARN:
                logger.warn(logMessage, throwable);
                break;
            case ERROR:
                logger.error(logMessage, throwable);
                break;
            case FATAL:
                logger.fatal(logMessage, throwable);
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
