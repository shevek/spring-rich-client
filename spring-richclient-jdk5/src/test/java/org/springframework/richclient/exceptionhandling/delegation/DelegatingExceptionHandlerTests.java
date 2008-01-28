package org.springframework.richclient.exceptionhandling.delegation;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import org.springframework.richclient.exceptionhandling.delegation.ChainInspectingExceptionHandlerDelegate.ChainPart;

/**
 * @author Geoffrey De Smet
 */
public class DelegatingExceptionHandlerTests extends TestCase {

    public void testSimpleDelegation() {
        DelegatingExceptionHandler delegatingExceptionHandler = new DelegatingExceptionHandler();
        List<ExceptionHandlerDelegate> delegateList = new LinkedList<ExceptionHandlerDelegate>();
        ExceptionHandlerCounter illegalArgumentCounter = new ExceptionHandlerCounter();
        delegateList.add(new SimpleExceptionHandlerDelegate(IllegalArgumentException.class, illegalArgumentCounter));
        ExceptionHandlerCounter nullPointerCounter = new ExceptionHandlerCounter();
        delegateList.add(new SimpleExceptionHandlerDelegate(NullPointerException.class, nullPointerCounter));
        ExceptionHandlerCounter runtimeCounter = new ExceptionHandlerCounter();
        delegateList.add(new SimpleExceptionHandlerDelegate(RuntimeException.class, runtimeCounter));
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

    public void testSimplePurgedDelegation() {
        DelegatingExceptionHandler delegatingExceptionHandler = new DelegatingExceptionHandler();
        List<ExceptionHandlerDelegate> delegateList = new LinkedList<ExceptionHandlerDelegate>();
        ExceptionHandlerCounter nullPointerCounter = new ExceptionHandlerCounter(NullPointerException.class);
        SimpleExceptionHandlerDelegate delegate = new SimpleExceptionHandlerDelegate(NullPointerException.class, nullPointerCounter);
        delegate.setExceptionPurger(new DefaultExceptionPurger(null, IllegalArgumentException.class));
        delegateList.add(delegate);
        ExceptionHandlerCounter runtimeCounter = new ExceptionHandlerCounter();
        delegateList.add(new SimpleExceptionHandlerDelegate(RuntimeException.class, runtimeCounter));
        delegatingExceptionHandler.setDelegateList(delegateList);

        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new NullPointerException()); // ok
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException(new NullPointerException())); // ok
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException(new IllegalStateException(new NullPointerException()))); // not ok
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException(new IllegalArgumentException(new NullPointerException()))); // ok
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException()); // not ok

        assertEquals(3, nullPointerCounter.getCounter());
        assertEquals(2, runtimeCounter.getCounter());
    }
    
    public void testChainInpstectingDelegation() {
        DelegatingExceptionHandler delegatingExceptionHandler = new DelegatingExceptionHandler();
        List<ExceptionHandlerDelegate> delegateList = new LinkedList<ExceptionHandlerDelegate>();
        List<ChainPart> chainPartList = new ArrayList<ChainPart>();
        chainPartList.add(new ChainPart(IllegalArgumentException.class, 0, 2));
        chainPartList.add(new ChainPart(IllegalStateException.class, 1));
        ExceptionHandlerCounter chainCounter = new ExceptionHandlerCounter();
        delegateList.add(new ChainInspectingExceptionHandlerDelegate(chainPartList, chainCounter));
        List<ChainPart> cornerChainPartList = new ArrayList<ChainPart>();
        cornerChainPartList.add(new ChainPart(NumberFormatException.class));
        ExceptionHandlerCounter cornerCounter = new ExceptionHandlerCounter();
        delegateList.add(new ChainInspectingExceptionHandlerDelegate(cornerChainPartList, cornerCounter));
        ExceptionHandlerCounter runtimeCounter = new ExceptionHandlerCounter();
        delegateList.add(new SimpleExceptionHandlerDelegate(RuntimeException.class, runtimeCounter));
        delegatingExceptionHandler.setDelegateList(delegateList);


        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException(
                        new RuntimeException(new IllegalStateException()))); // chainCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new RuntimeException(new IllegalArgumentException(
                        new RuntimeException(new IllegalStateException())))); // chainCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new RuntimeException(new RuntimeException(new IllegalArgumentException(
                        new RuntimeException(new IllegalStateException()))))); // chainCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new RuntimeException(new RuntimeException(new RuntimeException(new IllegalArgumentException(
                        new RuntimeException(new IllegalStateException())))))); // runtimeCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException(
                        new IllegalStateException())); // runtimeCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalArgumentException(
                        new RuntimeException(new RuntimeException(new IllegalStateException())))); // runtimeCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new IllegalStateException(
                        new RuntimeException(new IllegalArgumentException()))); // runtimeCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new NumberFormatException()); // cornerCounter
        delegatingExceptionHandler.uncaughtException(Thread.currentThread(),
                new RuntimeException(new NumberFormatException())); // cornerCounter

        assertEquals(3, chainCounter.getCounter());
        assertEquals(2, cornerCounter.getCounter());
        assertEquals(4, runtimeCounter.getCounter());
    }
    
    public static class ExceptionHandlerCounter implements Thread.UncaughtExceptionHandler {

        private int counter = 0;
        private Class filterClass;

        private ExceptionHandlerCounter() {}

        private ExceptionHandlerCounter(Class filterClass) {
            this.filterClass = filterClass;
        }

        public int getCounter() {
            return counter;
        }

        public void uncaughtException(Thread t, Throwable e) {
            if (filterClass == null || filterClass.isInstance(e)) {
                counter++;
            }
        }
    }



}
