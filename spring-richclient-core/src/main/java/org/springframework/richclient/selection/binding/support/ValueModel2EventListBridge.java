/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.selection.binding.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.springframework.binding.value.ValueModel;
import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;

/**
 * Creates a bridge between a <code>ValueModel</code> and an <code>EventList</code>. This means that when the
 * <code>ValueModel</code> value changes, it's copied into the EventList. There's also a {@link #synchronize()} method
 * that manually copies the values.
 * 
 * @author Peter De Bruycker
 */
public class ValueModel2EventListBridge {

    private boolean manualSynchronize;
    private ValueModel valueModel;
    private EventList eventList;
    private PropertyChangeListener valueChangeHandler = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            synchronize();
        }
    };

    /**
     * Same as calling {@link #ValueModel2EventListBridge(ValueModel, EventList, boolean)} with
     * <code>manualSynchronize = false</code>.
     * 
     * @param valueModel
     *            the ValueModel, cannot be <code>null</code>
     * @param eventList
     *            the EventList, cannot be <code>null</code>
     * @see #ValueModel2EventListBridge(ValueModel, EventList, boolean)
     */
    public ValueModel2EventListBridge(ValueModel valueModel, EventList eventList) {
        this(valueModel, eventList, false);
    }

    /**
     * Creates a <code>ValueModel2EventListBridge</code> for the given ValueModel and EventList.
     * 
     * @param valueModel
     *            the ValueModel, cannot be <code>null</code>
     * @param eventList
     *            the EventList, cannot be <code>null</code>
     * @param manualSynchronize
     *            manual or automatic synchronize?
     */
    public ValueModel2EventListBridge(ValueModel valueModel, EventList eventList, boolean manualSynchronize) {
        Assert.notNull(valueModel, "valueModel is required");
        Assert.notNull(eventList, "eventList is required");

        this.valueModel = valueModel;
        this.eventList = eventList;
        this.manualSynchronize = manualSynchronize;

        if (!manualSynchronize) {
            synchronize();
            valueModel.addValueChangeListener(valueChangeHandler);
        }
    }

    /**
     * Synchronizes the EventList with the ValueModel. The values of the collection in the ValueModel are copied to the
     * EventList. If the value in the ValueModel is <code>null</code>, the EventList will be emptied.
     */
    public void synchronize() {
        System.out.println(Thread.currentThread().getName());
        eventList.clear();

        // call ValueModel.getValue() only once, as it's possible that getValue is expensive (as can be the case with a
        // RefreshableValueHolder
        Object value = valueModel.getValue();

        if (value != null) {
            Assert.isInstanceOf(Collection.class, value, "The value in the ValueModel is not a Collection");
            eventList.addAll((Collection) value);
        }
    }

    /**
     * Performs necessary cleanup (removing listeners, ...)
     */
    public void dispose() {
        if (!manualSynchronize) {
            valueModel.removeValueChangeListener(valueChangeHandler);
        }
    }

    /**
     * Returns the ValueModel.
     * 
     * @return the ValueModel
     */
    public ValueModel getValueModel() {
        return valueModel;
    }

    /**
     * Returns the EventList.
     * 
     * @return the EventLists
     */
    public EventList getEventList() {
        return eventList;
    }
}
