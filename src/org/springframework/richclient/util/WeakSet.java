/**
 * GUI Commands
 * Copyright (C) 2002  Andrew Pietsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id$
 */
package org.springframework.richclient.util;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A private class based on the HashSet but that only keeps weak references to
 * its collection. It provides access only via an iterator.
 */
public class WeakSet {
    private HashSet referenceSet;

    WeakIterator iterator = new WeakIterator();

    private Iterator emptyIterator = new Iterator() {
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            return null;
        }

        public void remove() {
        }
    };

    public WeakSet() {
        this(10);
    }

    public WeakSet(int size) {
        if (size < 0) { throw new IllegalArgumentException(
                "Size must be zero or greater"); }
        referenceSet = new HashSet(size);
    }

    public boolean isEmpty() {
        return (referenceSet.size() == 0);
    }
    
    public int size() {
        return referenceSet.size();
    }

    public boolean add(Object o) {
        if (o == null) { return false; }
        if (!contains(o)) {
            referenceSet.add(new WeakReference(o));
            return true;
        }
        return false;
    }

    public boolean remove(Object o) {
        Iterator iter = referenceSet.iterator();
        while (iter.hasNext()) {
            WeakReference reference = (WeakReference)iter.next();
            Object target = reference.get();
            if (target == null) {
                iter.remove();
            }
            else {
                if (target.equals(o)) {
                    iter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public void removeAll() {
        referenceSet.clear();
    }

    public boolean contains(Object o) {
        Iterator iter = referenceSet.iterator();
        while (iter.hasNext()) {
            WeakReference reference = (WeakReference)iter.next();
            Object target = reference.get();
            if (target == null) {
                iter.remove();
            }
            else {
                if (o.equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Iterator iterator() {
        if (referenceSet.size() < 1) { return emptyIterator; }
        return iterator.restart(referenceSet.iterator());
    }

    private class WeakIterator implements Iterator {
        Iterator parentIterator;

        Object nextObject = null;

        protected WeakIterator() {
        }

        public Iterator restart(Iterator parent) {
            parentIterator = parent;
            nextObject = null;
            return this;
        }

        public boolean hasNext() {
            if (parentIterator == null) { return false; }
            if (nextObject != null) { return true; }
            nextObject = prepareNext();
            if (nextObject == null) { return false; }
            return true;
        }

        public Object next() {
            if (!hasNext()) { throw new NoSuchElementException(); }
            // return the next object but make sure we loose our reference to
            // it on the way out so it can be collected.
            Object o = nextObject;
            nextObject = null;
            return o;
        }

        public void remove() {
            throw new UnsupportedOperationException(
                    "Method not  supported by this Iterator");
        }

        private Object prepareNext() {
            Object o = null;
            while (o == null && parentIterator.hasNext()) {
                WeakReference ref = (WeakReference)parentIterator.next();
                o = ref.get();
                if (o == null) {
                    parentIterator.remove();
                }
            }
            return o;
        }
    }

}

