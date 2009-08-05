package org.springframework.richclient.exceptionhandling;

/**
 * An exception handler which can be registered (to for example the EDT and all threads).
 *
 * @author Geoffrey De Smet
 * @since 0.3
 */
public interface RegisterableExceptionHandler extends Thread.UncaughtExceptionHandler {

    void registerExceptionHandler();

}
