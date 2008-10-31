package org.springframework.richclient.exceptionhandling;

/**
 * An exception handler which can be registered (to for example the EDT and all threads).
 *
 * @TODO extend Thread.UncaughtExceptionHandler when Spring-richclient is minimum java 1.5
 * @author Geoffrey De Smet
 * @since 0.3
 */
public interface RegisterableExceptionHandler {

    void registerExceptionHandler();

    void uncaughtException(Thread thread, Throwable throwable);

}
