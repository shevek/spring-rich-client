/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 */
package org.springframework.richclient.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import EDU.oswego.cs.dl.util.concurrent.Callable;
import EDU.oswego.cs.dl.util.concurrent.FutureResult;
import EDU.oswego.cs.dl.util.concurrent.TimedCallable;

/**
 * An abstract class that you subclass to perform GUI-related work in a
 * dedicated thread.
 * <p>
 * This class was adapted from the SwingWorker class presented in "Using a Swing
 * Worker Thread" in the Swing Connection archives
 * http://java.sun.com/products/jfc/tsc/archive/archive.html
 * <p>
 * This version of SwingWorker extends FutureResult and implements Runnable.
 * Timeouts are supported.
 * Deprecated in favor of javax.swing.SwingWorker.
 */
@Deprecated
public abstract class SwingWorker extends FutureResult implements Runnable {
    /** Worker thread. */
    protected Thread thread;

    /**
     * Computes the value to be returned by the <code>get</code> method.
     */
    protected abstract Object construct() throws Exception;

    /**
     * Called on the event dispatching thread (not on the worker thread) after
     * the <code>construct</code> method has returned.
     */
    protected void finished() {
    }

    protected Object getFinishedResult() {
        try {
            return get();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(
                    "Interrupted exception should not have occured; are you calling from finished()?");
        }
        catch (InvocationTargetException e) {
            return null;
        }
    }

    protected Throwable getTargetException() {
        InvocationTargetException e = getException();
        if (e != null) {
            return e.getTargetException();
        }
        return e;
    }

    /**
     * Override to return a timeout period, in milliseconds. The timeout is the
     * maximum time to wait for the worker to complete. There is no time limit
     * if the timeout is <code>0</code> (default).
     */
    public long getTimeout() {
        return 0;
    }

    /**
     * Calls the <code>construct</code> method to compute the result, and then
     * invokes the <code>finished</code> method on the event dispatch thread.
     */
    public void run() {
        Callable function = new Callable() {
            public Object call() throws Exception {
                return construct();
            }
        };
        Runnable doFinished = new Runnable() {
            public void run() {
                finished();
            }
        };
        /* Convert to TimedCallable if timeout is specified. */
        long msecs = getTimeout();
        if (msecs != 0) {
            function = new TimedCallable(function, msecs);
        }
        setter(function).run();
        SwingUtilities.invokeLater(doFinished);
    }

    /**
     * Starts the worker thread.
     */
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this);
        }
        thread.start();
    }

    /**
     * Stops the worker and sets the exception to InterruptedException.
     */
    public synchronized void interrupt() {
        if (thread != null) {
            /*
             * Try-catch is workaround for JDK1.2.1 applet security bug.
             * JDK1.2.1 overzealously throws a security exception if an applet
             * interrupts a thread that is no longer alive.
             */
            try {
                thread.interrupt();
            }
            catch (Exception ex) {
            }
        }
        setException(new InterruptedException());
    }

    /**
     * Clears the worker thread variable and the FutureResult state, allowing
     * this SwingWorker to be reused. This is not particularly recommended and
     * must be done only when you know that the worker thread is finished and
     * that no other object is depending on the properties of this SwingWorker.
     */
    public synchronized void clear() {
        super.clear();
        thread = null;
    }
}