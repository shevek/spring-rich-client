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
 * the License.
 */
package org.springframework.richclient.table;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * Thread responsible for publishing changes to the Model. Sleeps for a defined
 * amount of time, waits for no activity in the UI and then users invokeAndWait
 * to publish changes.
 */
public class TableUpdater extends Thread {
    private int sleepTime = 3000;

    private int eqSleepTime = 1000;

    private boolean updatesEnabled = true;

    private Runnable publishRunnable;

    private Runnable emptyRunnable;

    private TableDataProvider tableDataProvider;

    private MutableTableModel tableModel;

    private boolean done;

    public TableUpdater(TableDataProvider provider, MutableTableModel tableModel) {
        super();
        setPriority(Thread.MIN_PRIORITY);
        this.updatesEnabled = true;
        this.tableDataProvider = provider;
        this.tableModel = tableModel;

        // Runnable used to publish changes to the event dispatching thread
        this.publishRunnable = new Runnable() {
            public void run() {
                publishChangesOnEventDispatchingThread();
            }
        };

        // Empty runnable, used to wait until the event dispatching thread
        // has finished processing any pending events.
        this.emptyRunnable = new Runnable() {
            public void run() {
            }
        };
    }

    public void interrupt() {
        done = true;
        super.interrupt();
    }

    public void run() {
        while (!isInterrupted() && !done) {
            try {
                sleep(sleepTime);
                waitForUpdatesEnabled();
                waitForIdleEventQueue();
                publishChanges();
            }
            catch (InterruptedException ie) {
            }
        }
    }

    private void waitForUpdatesEnabled() {
        synchronized (this) {
            while (!this.updatesEnabled) {
                try {
                    wait();
                }
                catch (InterruptedException ie) {
                }
            }
        }
    }

    private void waitForIdleEventQueue() {
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        while (queue.peekEvent() != null) {
            try {
                sleep(eqSleepTime);
            }
            catch (InterruptedException ie) {
            }
        }
    }

    /**
     * Publishes changes on the event dispatching thread when the system isn't
     * busy. This blocks the caller until the changes have been published.
     */
    private void publishChanges() {
        // And wait until there are no pending events.
        /*
         * try { tableModel.lock(); } catch (InterruptedException e) {
         * System.err.println("Table updater interrupted on table lock attempt.
         * Nothing updated."); return; }
         */

        try {
            // publish the changes on the event dispatching thread
            SwingUtilities.invokeAndWait(publishRunnable);
        }
        catch (InterruptedException ie) {
        }
        catch (InvocationTargetException ite) {
        }

        try {
            // Wait until the system has completed processing of any
            // events we triggered as part of publishing changes.
            SwingUtilities.invokeAndWait(emptyRunnable);
        }
        catch (InterruptedException ie) {
        }
        catch (InvocationTargetException ite) {
        }

        /*
         * tableModel.unlock();
         */
    }

    /**
     * Does the actual publishing of changes.
     */
    private void publishChangesOnEventDispatchingThread() {
        List newRows = tableDataProvider.takeData();
        if (newRows.size() > 0) {
            tableModel.addRows(newRows);
            RepaintManager.currentManager(null).paintDirtyRegions();
            newRows.clear();
        }
    }

    /**
     * If enable is true, we are allowed to publish changes, otherwise we
     * aren't.
     */
    public void setUpdatesEnabled(boolean enable) {
        synchronized (this) {
            updatesEnabled = enable;
            if (updatesEnabled) {
                notify();
            }
        }
    }

    public boolean getUpdatesEnabled() {
        return updatesEnabled;
    }
}