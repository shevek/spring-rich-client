package org.springframework.richclient.exceptionhandling;

/**
 * Logs an exception but does not notify the user in any way.
 * Normally it's a bad practice not to notify the user if something goes wrong.
 *
 * @author Geoffrey De Smet
 */
public class SilentExceptionHandler extends LoggingExceptionHandler {

    /**
     * Does nothing.
     */
    public void showExceptionToUser(Thread thread, Throwable throwable) {
    }

}
