package org.springframework.richclient.exceptionhandling;

/**
 * Logs an exception but does not notify the user in any way.
 * Normally it is a bad practice not to notify the user if something goes wrong.
 *
 * @author Geoffrey De Smet
 */
public class SimpleLoggingExceptionHandler extends AbstractLoggingExceptionHandler {

    /**
     * Does nothing.
     */
    public void notifyUserAboutException(Thread thread, Throwable throwable) {
    }

}
