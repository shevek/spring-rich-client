package org.springframework.richclient.exceptionhandling;

/**
 *
 * @TODO extend Thread.UncaughtExceptionHandler when Spring-richclient is minimum java 1.5
 * @author Geoffrey De Smet
 */
public interface RegisterableExceptionHandler {

    void registerExceptionHandler();

    void uncaughtException(Thread thread, Throwable throwable);

}
