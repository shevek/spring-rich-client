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
package org.springframework.richclient.util;

import java.text.NumberFormat;
import java.util.EventListener;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import org.springframework.util.EventListenerListHelper;
import org.springframework.util.StopWatch;
import org.springframework.util.closure.Closure;

/**
 * Some benchmarking of EventListenerListHelper
 * 
 * @author oliverh
 */
public abstract class ListenerListHelperPerformanceTests {

    private static int NUMBER_OF_WARM_UP_ITERATIONS = 30000;

    private static int NUMBER_OF_ITERATIONS = 500000;

    public static int[] NUM_LISTENER_TESTS = new int[] { 0, 2, 5, 10 };

    protected int listenerInvocationCount = 0;

    protected int numInstalledListeners = 0;

    private StopWatch sw;

    public ListenerListHelperPerformanceTests(String description) {
        sw = new StopWatch(description);
    }

    public void runTests(int iterations) {
        for (int i = 0; i < NUM_LISTENER_TESTS.length; i++) {
            addListeners(NUM_LISTENER_TESTS[i]);

            sw.start(numInstalledListeners + " listeners");
            for (int j = 0; j < iterations / 10; j++) {
                runTest();
                runTest();
                runTest();
                runTest();
                runTest();
                runTest();
                runTest();
                runTest();
                runTest();
                runTest();
            }
            sw.stop();
        }
    }

    public void printResults() {
        System.out.println(sw.prettyPrint());
        System.out.println(NumberFormat.getNumberInstance().format(
                Math.round(listenerInvocationCount / sw.getTotalTimeSeconds()))
                + " listener invocations per second");
    }

    private void addListeners(int numListeners) {
        for (; numInstalledListeners < numListeners; numInstalledListeners++) {
            addListener();
        }
    }

    protected abstract void addListener();

    protected abstract void runTest();

    private class TestListener implements EventListener {
        /* silly code to try and confuse hotspot otimizations */
        volatile boolean fug = true;

        public void fireNoArgsEvent() {
            listenerInvocationCount++;
        }

        public void fireOneArgsEvent(Object arg) {
            listenerInvocationCount++;
        }
    }

    private static class BaseLine extends ListenerListHelperPerformanceTests {
        public BaseLine() {
            super("BaseLine - No Arg");
        }

        int listeners = 0;

        TestListener listener = new TestListener();

        protected void addListener() {
            listeners++;
        }

        protected final void runTest() {
            for (int i = 0; i < listeners; i++) {
                listener.fireNoArgsEvent();
            }
        }

        public TestListener getListener() {
            return listener;
        }
    }

    private static class BaseLineOneArg extends ListenerListHelperPerformanceTests {
        public BaseLineOneArg() {
            super("BaseLine - One Arg");
        }

        int listeners = 0;

        TestListener listener = new TestListener();

        protected void addListener() {
            listeners++;
        }

        protected final void runTest() {
            Object event = new Object();
            for (int i = 0; i < listeners; i++) {
                listener.fireOneArgsEvent(event);
            }
        }

        public TestListener getListener() {
            return listener;
        }
    }

    private static class ReflectionNoArgTest extends ListenerListHelperPerformanceTests {
        public ReflectionNoArgTest() {
            super("Reflection - No Args");
        }

        private final EventListenerListHelper listenerList = new EventListenerListHelper(TestListener.class);

        protected void addListener() {
            listenerList.add(new TestListener());
        }

        protected final void runTest() {
            listenerList.fire("fireNoArgsEvent");
        }

        public TestListener getListener() {
            return (TestListener)listenerList.iterator().next();
        }
    }

    private static class ReflectionOneArgTest extends ListenerListHelperPerformanceTests {
        public ReflectionOneArgTest() {
            super("Reflection - 1 Args");
        }

        private final EventListenerListHelper listenerList = new EventListenerListHelper(TestListener.class);

        protected void addListener() {
            listenerList.add(new TestListener());
        }

        protected final void runTest() {
            listenerList.fire("fireOneArgsEvent", new Object());
        }

        public TestListener getListener() {
            return (TestListener)listenerList.iterator().next();
        }
    }

    private static class ClosureNoArgTest extends ListenerListHelperPerformanceTests {
        public ClosureNoArgTest() {
            super("Closure - No Args");
        }

        private final EventListenerListHelper listenerList = new EventListenerListHelper(TestListener.class);

        protected void addListener() {
            listenerList.add(new TestListener());
        }

        protected final void runTest() {
            listenerList.forEach(new Closure() {

                public Object call(Object argument) {
                    ((TestListener)argument).fireNoArgsEvent();
                    return null;
                }

            });
        }

        public TestListener getListener() {
            return (TestListener)listenerList.iterator().next();
        }
    }

    private static class ClosureOneArgTest extends ListenerListHelperPerformanceTests {
        public ClosureOneArgTest() {
            super("Closure - 1 Args");
        }

        private final EventListenerListHelper listenerList = new EventListenerListHelper(TestListener.class);

        protected void addListener() {
            listenerList.add(new TestListener());
        }

        protected final void runTest() {
            listenerList.forEach(new Closure() {

                public Object call(Object argument) {
                    ((TestListener)argument).fireOneArgsEvent(new Object());
                    return null;
                }

            });
        }
    }

    private static class IteratorNoArgTest extends ListenerListHelperPerformanceTests {
        public IteratorNoArgTest() {
            super("Iterator - No Args");
        }

        private final EventListenerListHelper listenerList = new EventListenerListHelper(TestListener.class);

        protected void addListener() {
            listenerList.add(new TestListener());
        }

        protected final void runTest() {
            for (Iterator i = listenerList.iterator(); i.hasNext();) {
                ((TestListener)i.next()).fireNoArgsEvent();
            }
        }
    }

    private static class IteratorOneArgTest extends ListenerListHelperPerformanceTests {
        public IteratorOneArgTest() {
            super("Iterator - 1 Args");
        }

        private final EventListenerListHelper listenerList = new EventListenerListHelper(TestListener.class);

        protected void addListener() {
            listenerList.add(new TestListener());
        }

        protected final void runTest() {
            for (Iterator i = listenerList.iterator(); i.hasNext();) {
                ((TestListener)i.next()).fireOneArgsEvent(new Object());
            }
        }
    }

    private static class EventListenerListNoArgTest extends ListenerListHelperPerformanceTests {
        public EventListenerListNoArgTest() {
            super("EventListenerList - No Args");
        }

        private final EventListenerList listenerList = new EventListenerList();

        protected void addListener() {
            listenerList.add(TestListener.class, new TestListener());
        }

        protected final void runTest() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TestListener.class) {
                    ((TestListener)listeners[i + 1]).fireNoArgsEvent();
                }
            }
        }
    }

    private static class EventListenerListOneArgTest extends ListenerListHelperPerformanceTests {
        public EventListenerListOneArgTest() {
            super("EventListenerList - 1 Args");
        }

        private final EventListenerList listenerList = new EventListenerList();

        protected void addListener() {
            listenerList.add(TestListener.class, new TestListener());
        }

        protected final void runTest() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TestListener.class) {
                    ((TestListener)listeners[i + 1]).fireOneArgsEvent(new Object());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Class[] tests = new Class[] { BaseLine.class, BaseLineOneArg.class, ReflectionNoArgTest.class,
                    ReflectionOneArgTest.class, ClosureNoArgTest.class, ClosureOneArgTest.class,
                    IteratorNoArgTest.class, IteratorOneArgTest.class, EventListenerListNoArgTest.class,
                    EventListenerListOneArgTest.class };

            for (int i = 0; i < tests.length; i++) {
                System.gc();
                ListenerListHelperPerformanceTests test = (ListenerListHelperPerformanceTests)tests[i].getConstructor(
                        null).newInstance(null);

                test.runTests(NUMBER_OF_WARM_UP_ITERATIONS);

                System.gc();
                test = (ListenerListHelperPerformanceTests)tests[i].getConstructor(null).newInstance(null);

                test.runTests(NUMBER_OF_ITERATIONS);
                test.printResults();
                System.out.println();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}