package org.springframework.richclient.exceptionhandling;

import java.util.List;
import java.util.Collections;

/**
 * A couple of a throwable class list and the appropriate exception handler.
 * Note: Also subclasses of the classes in the class list will be handled by the exception handler.
 * @author Geoffrey De Smet
 */
public class DelegatingExceptionHandlerDelegate {

    private List<Class> throwableClassList;
    private Thread.UncaughtExceptionHandler exceptionHandler;

    public DelegatingExceptionHandlerDelegate() {
    }

    public DelegatingExceptionHandlerDelegate(Class throwableClass,
                                              Thread.UncaughtExceptionHandler exceptionHandler) {
        this(Collections.singletonList(throwableClass), exceptionHandler);
    }

    public DelegatingExceptionHandlerDelegate(List<Class> throwableClassList,
            Thread.UncaughtExceptionHandler exceptionHandler) {
        this.throwableClassList = throwableClassList;
        this.exceptionHandler = exceptionHandler;
    }

    public void setThrowableClass(Class throwableClass) {
        setThrowableClassList(Collections.singletonList(throwableClass));
    }

    public void setThrowableClassList(List<Class> throwableClassList) {
        this.throwableClassList = throwableClassList;
    }

    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }


    /**
     * Returns true if thrownTrowable is an instance of one of the throwableClassList.
     *
     * @param thrownTrowable the thrown exception or error.
     * @return true if thrownTrowable is an instance of one of the throwableClassList
     */
    public boolean hasAppropriateHandler(Throwable thrownTrowable) {
        for (Class throwableClass : throwableClassList) {
            if (throwableClass.isInstance(thrownTrowable)) {
                return true;
            }
        }
        return false;
    }

}
