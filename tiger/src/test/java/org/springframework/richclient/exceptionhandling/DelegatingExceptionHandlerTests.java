package org.springframework.richclient.exceptionhandling;

import junit.framework.TestCase;
import java.util.List;
import java.util.LinkedList;

/**
 * @author Geoffrey De Smet
 */
public class DelegatingExceptionHandlerTests extends TestCase {

    public void testDelegation() {
        DelegatingExceptionHandler delegatingExceptionHandler = new DelegatingExceptionHandler();
        List<DelegatingExceptionHandlerDelegate> delegateList = new LinkedList<DelegatingExceptionHandlerDelegate>();
        ExceptionHandlerCounter illegalArgumentCounter = new ExceptionHandlerCounter();
        delegateList.add(new DelegatingExceptionHandlerDelegate(IllegalArgumentException.class, illegalArgumentCounter));
        ExceptionHandlerCounter nullPointerCounter = new ExceptionHandlerCounter();
        delegateList.add(new DelegatingExceptionHandlerDelegate(NullPointerException.class, nullPointerCounter));
        ExceptionHandlerCounter runtimeCounter = new ExceptionHandlerCounter();
        delegateList.add(new DelegatingExceptionHandlerDelegate(RuntimeException.class, runtimeCounter));
        delegatingExceptionHandler.setDelegateList(delegateList);

        delegatingExceptionHandler.uncaughtException(Thread.currentThread(), new NullPointerException());
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(), new RuntimeException());
        // NumberFormatException extends IllegalArgumentException
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(), new NumberFormatException());
        // IllegalStateException extends RuntimeException
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(), new IllegalStateException());
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(), new IllegalArgumentException());
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(), new NullPointerException());

        assertEquals(2, illegalArgumentCounter.getCounter());
        assertEquals(2, nullPointerCounter.getCounter());
        assertEquals(2, runtimeCounter.getCounter());
    }


    public static class ExceptionHandlerCounter implements Thread.UncaughtExceptionHandler {

        private int counter = 0;

        public int getCounter() {
            return counter;
        }

        public void uncaughtException(Thread t, Throwable e) {
            counter++;
        }

    }

}
