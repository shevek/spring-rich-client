package org.springframework.richclient.exceptionhandling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Superclass of logging exception handlers.
 *
 * @author Geoffrey De Smet
 */
public abstract class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler, InitializingBean {

    protected static final String LOG_MESSAGE = "Uncaught throwable handled";

    protected final transient Log logger = LogFactory.getLog(getClass());

    protected LogLevel logLevel;

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(logLevel);
    }

    public final void uncaughtException(Thread thread, Throwable throwable) {
        logException(thread, throwable);
        showExceptionToUser(thread, throwable);
    }

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

    public abstract void showExceptionToUser(Thread thread, Throwable throwable);

}
