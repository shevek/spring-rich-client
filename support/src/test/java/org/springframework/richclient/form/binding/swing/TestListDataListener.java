/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.swing;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.Assert;

/**
 * Implementation of {@link ListDataListener} that eases testing of 
 * Swing's list related classes.
 * 
 * @author Oliver Hutchison
 */
public class TestListDataListener implements ListDataListener {
    private ListDataEvent lastEvent;

    private int calls;

    public void assertCalls(int calls) {
        Assert.assertEquals("ListDataListener has not been called expected number of times.", calls, this.calls);
    }

    public void assertEvent(int calls, int eventType, int index0, int index1) {
        assertCalls(calls);
        Assert.assertEquals("Last ListDataEvent has unexpected type.", eventType, lastEvent.getType());
        Assert.assertEquals("Last ListDataEvent has unexpected index0.", index0, lastEvent.getIndex0());
        Assert.assertEquals("Last ListDataEvent has unexpected index1.", index1, lastEvent.getIndex1());
    }

    public void intervalAdded(ListDataEvent e) {
        calls++;
        lastEvent = e;
    }

    public void intervalRemoved(ListDataEvent e) {
        calls++;
        lastEvent = e;
    }

    public void contentsChanged(ListDataEvent e) {
        calls++;
        lastEvent = e;
    }
}