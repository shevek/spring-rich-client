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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.springframework.core.NestedRuntimeException;
import org.springframework.rules.Closure;
import org.springframework.util.ArrayUtils;
import org.springframework.util.Assert;
import org.springframework.util.Cache;
import org.springframework.util.ToStringBuilder;

/**
 * Helper implementation of an event listener list.
 * <p>
 * Provides methods for maintaining a list of listeners and firing events on
 * that list. This class is thread safe and serializable.
 * <p>
 * Usage Example:
 * 
 * <pre>
 * private ListenerListHelper fooListeners = new ListenerListHelper(
 *         FooListener.class);
 * 
 * public void addFooListener(FooListener listener) {
 *     fooListeners.add(listener);
 * }
 * 
 * public void removeFooListener(FooListener listener) {
 *     fooListeners.remove(listener);
 * }
 * 
 * protected void fireFooXXX() {
 *     fooListeners.fire(&quot;fooXXX&quot;, new Event());
 * }
 * 
 * protected void fireFooYYY() {
 *     fooListeners.fire(&quot;fooYYY&quot;);
 * }
 * </pre>
 * 
 * @author oliverh
 */
public class ListenerListHelper implements Serializable {

    private static final Iterator EMPTY_ITERATOR = new Iterator() {
        public boolean hasNext() {
            return false;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Object next() {
            throw new UnsupportedOperationException();
        }
    };

    private static final Cache methodCache = new Cache() {
        protected Object create(Object o) {
            MethodCacheKey key = (MethodCacheKey)o;
            Method fireMethod = null;

            Method[] methods = key.listenerClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equals(key.methodName)
                        && method.getParameterTypes().length == key.numParams) {
                    if (fireMethod != null) { throw new UnsupportedOperationException(
                            "Listener class ["
                                    + key.listenerClass
                                    + "] has more than 1 implementation of method ["
                                    + key.methodName + "] with ["
                                    + key.numParams + "] parameters."); }
                    fireMethod = method;
                }
            }

            if (fireMethod == null) { throw new IllegalArgumentException(
                    "Listener class [" + key.listenerClass
                            + "] does not implement method [" + key.methodName
                            + "] with [" + key.numParams + "] parameters."); }
            return fireMethod;
        }
    };

    private final Class listenerClass;

    private Object[] listeners;

    /**
     * Create new <code>ListenerListHelper</code> instance for the given
     * listener class.
     */
    public ListenerListHelper(Class listenerClass) {
        Assert.notNull(listenerClass);
        this.listenerClass = listenerClass;
    }

    /**
     * Returns whether or not any listeners are registered with this list.
     */
    public boolean hasListeners() {
        return listeners != null && listeners.length > 0;
    }

    /**
     * Returns the total number of listeners registered with this list.
     */
    public int getListenerCount() {
        return listeners != null ? 0 : listeners.length;
    }

    /**
     * Returns an iterator over the list of listeners registered with this list.
     */
    public Iterator iterator() {
        if (listeners == null) {
            return EMPTY_ITERATOR;
        }
        else {
            return new ObjectArrayIterator(listeners);
        }
    }

    /**
     * Executes the provided closure on each of the listeners registered with
     * this list.
     */
    public void forEach(Closure closure) {
        if (listeners != null) {
            Object[] listenersCopy = listeners;
            for (int i = 0; i < listenersCopy.length; i++) {
                closure.call(listenersCopy[i]);
            }
        }
    }

    /**
     * Invokes the specified method on each of the listeners registered with
     * this list.
     * 
     * @param methodName
     *            the name of the method to invoke.
     */
    public void fire(String methodName) {
        if (listeners != null) {
            fireEventWithReflection(methodName, ArrayUtils.EMPTY_OBJECT_ARRAY);
        }
    }

    /**
     * Invokes the specified method on each of the listeners registered with
     * this list.
     * 
     * @param methodName
     *            the name of the method to invoke.
     * @param arg
     *            the single argument to pass to each invocation.
     */
    public void fire(String methodName, Object arg) {
        if (listeners != null) {
            fireEventWithReflection(methodName, new Object[] { arg });
        }
    }

    /**
     * Invokes the specified method on each of the listeners registered with
     * this list.
     * 
     * @param methodName
     *            the name of the method to invoke.
     * @param args
     *            an array of arguments to pass to each invocation.
     */
    public void fire(String methodName, Object[] args) {
        if (listeners != null) {
            fireEventWithReflection(methodName, args);
        }
    }

    /**
     * Adds <code>listener</code> to the list of registerd listeners. If
     * listener is already registerd it will not be added a second time.
     */
    public void add(Object listener) {
        checkListenerType(listener);
        synchronized (this) {
            if (listeners == null) {
                listeners = new Object[] { listener };
            }
            else {
                int listenersLength = listeners.length;
                for (int i=0; i < listenersLength; i++) {
                    if (listeners[i] == listener) {
                        return;
                    }
                }
                Object[] tmp = new Object[listenersLength + 1];
                System.arraycopy(listeners, 0, tmp, 0, listenersLength);
                tmp[listenersLength] = listener;
                listeners = tmp;
            }
        }
    }

    /**
     * Removes <code>listener</code> from the list of registerd listeners.
     */
    public void remove(Object listener) {
        checkListenerType(listener);
        synchronized (this) {
            if (listeners == null) {
                return;
            }
            else {
                int listenersLength = listeners.length;
                int index = 0;                
                for (; index < listenersLength; index++) {
                    if (listeners[index] == listener) {
                        break;
                    }
                }
                if (index < listenersLength) {
                    if (listenersLength == 1) {
                        listeners = null;
                    } else {
                        Object[] tmp = new Object[listenersLength - 1];
                        System.arraycopy(listeners, 0, tmp, 0, index);
                        if (index < tmp.length) {
                            System.arraycopy(listeners, index + 1, tmp, index,
                                    tmp.length - index);
                        }
                        listeners = tmp;
                    }
                }
            }
        }
    }

    public String toString() {
        return new ToStringBuilder(this).append("listenerClass", listenerClass)
                .append("listeners", listeners).toString();
    }

    private void fireEventWithReflection(String eventName, Object[] events) {
        Method fireMethod = (Method)methodCache.get(new MethodCacheKey(
                listenerClass, eventName, events.length));
        Object[] listenersCopy = listeners;
        for (int i = 0; i < listenersCopy.length; i++) {
            try {
                fireMethod.invoke(listenersCopy[i], events);
            }
            catch (InvocationTargetException e) {
                throw new NestedRuntimeException(
                        "Exception thrown by listener", e.getCause()) {
                };
            }
            catch (IllegalAccessException e) {
                throw new NestedRuntimeException("Unable to invoke listener", e) {
                };
            }
        }
    }

    private void checkListenerType(Object listener) {
        if (!listenerClass.isInstance(listener)) { throw new IllegalArgumentException(
                "Listener [" + listener + "] is not an instanceof ["
                        + listenerClass + "]."); }
    }

    private static final class ObjectArrayIterator implements Iterator {
        private final Object[] array;

        private int index;

        public ObjectArrayIterator(Object[] array) {
            this.array = array;
            this.index = 0;
        }

        public boolean hasNext() {
            return index < array.length;
        }

        public Object next() {
            return array[index++];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class MethodCacheKey {
        public final Class listenerClass;

        public final String methodName;

        public final int numParams;

        public MethodCacheKey(Class listenerClass, String methodName,
                int numParams) {
            this.listenerClass = listenerClass;
            this.methodName = methodName;
            this.numParams = numParams;
        }

        public boolean equals(Object o2) {
            if (o2 == null) { return false; }
            MethodCacheKey k2 = (MethodCacheKey)o2;
            return listenerClass.equals(k2.listenerClass)
                    && methodName.equals(k2.methodName)
                    && numParams == k2.numParams;
        }

        public int hashCode() {
            return listenerClass.hashCode() ^ methodName.hashCode() ^ numParams;
        }
    }
}