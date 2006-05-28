package org.springframework.richclient.exceptionhandling;

/**
 * A couple of a throwable class and its exception handler.
 * @author Geoffrey De Smet
 */
public class DelegatingExceptionHandlerDelegate {

    private Class throwableClass;
    private Thread.UncaughtExceptionHandler exceptionHandler;

    public DelegatingExceptionHandlerDelegate() {
    }

    public DelegatingExceptionHandlerDelegate(Class throwableClass,
                                              Thread.UncaughtExceptionHandler exceptionHandler) {
        this.throwableClass = throwableClass;
        this.exceptionHandler = exceptionHandler;
    }

    public Class getThrowableClass() {
        return throwableClass;
    }

    public void setThrowableClass(Class throwableClass) {
        this.throwableClass = throwableClass;
    }

    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    
    /**
     * Returns true if thrownTrowable is an instance of throwableClass.
     *
     * @param thrownTrowable the thrown exception or error.
     * @return true if thrownTrowable is an instance of throwableClass
     */
    public boolean hasAppropriateHandler(Throwable thrownTrowable) {
        return throwableClass.isInstance(thrownTrowable);
    }

}
