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
package org.springframework.binding.swing;

import java.io.Serializable;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.rules.constraints.Range;
import org.springframework.util.ToStringBuilder;

/**
 * Converts a <code>ValueModel</code> to <code>BoundedRangeModel</code>.
 * Therefore honors a minimum and maximum bound.
 * <p>
 * 
 * <strong>Example: </strong>
 * 
 * <pre>
 * int minSaturation = 0;int maxSaturation = 255;
 *        ValueModel saturationModel = new PropertyAdapter(settingsBean, &quot;saturation&quot;);
 *        JSlider saturationSlider = new JSlider(
 *            new BoundedRangeAdapter(saturationModel, 
 *                                    0, 
 *                                    minSaturation, 
 *                                    maxSaturation));
 *  
 * </pre>
 * 
 * @author Keith Donald
 * @author Karsten Lentzsch
 * @see javax.swing.JSlider
 */
public final class BoundedRangeModelAdapter implements BoundedRangeModel,
        Serializable {

    /**
     * Only one ChangeEvent is needed per model instance since the event's only
     * (read-only) state is the source property. The source of events generated
     * here is always "this".
     */
    protected transient ChangeEvent changeEvent;

    /**
     * The listeners observing model changes.
     */
    protected EventListenerList listenerList = new EventListenerList();

    private final ValueModel valueModel;

    private int extent = 0;

    private Range range = new Range(0, 100);

    private boolean isAdjusting;

    private ValueChangeListener currentValueChangeHandler;
    
    /**
     * Constructs a <code>BoundedRangeModelAdapter</code> on the given subject
     * using the specified extent, minimum and maximum values.
     * 
     * @param valueModel
     *            ValueModel holding the current value of the bounded range
     * @param extent
     *            int
     * @param min
     *            int
     * @param max
     *            int
     */
    public BoundedRangeModelAdapter(ValueModel valueModel, int extent, int min,
            int max) {
        this.valueModel = valueModel;
        init(((Integer)valueModel.getValue()).intValue(), extent, min, max);
        this.currentValueChangeHandler = new CurrentValueChangeHandler();
        valueModel.addValueChangeListener(currentValueChangeHandler);
    }

    private void init(int initialValue, int extent, int minimum, int maximum) {
        validate(initialValue, extent, minimum, maximum);
        this.extent = extent;
        this.range = new Range(minimum, maximum);
    }

    private void validate(int initialValue, int extent, int minimum, int maximum) {
        if (!((maximum >= minimum) && (initialValue >= minimum)
                && ((initialValue + extent) >= initialValue) && ((initialValue + extent) <= maximum))) { throw new IllegalArgumentException(
                "Invalid bounded range properties"); }
    }

    // Handles changes in the value model's value.
    private class CurrentValueChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            fireStateChanged();
        }
    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    public int getExtent() {
        return extent;
    }

    public int getMaximum() {
        return ((Integer)range.getMax()).intValue();
    }

    public int getMinimum() {
        return ((Integer)range.getMin()).intValue();
    }

    public int getValue() {
        return ((Integer)valueModel.getValue()).intValue();
    }

    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    public void setExtent(int n) {
        int newExtent = Math.max(0, n);
        int value = getValue();
        if (value + newExtent > getMaximum()) {
            newExtent = getMaximum() - value;
        }
        setRangeProperties(value, newExtent, getMinimum(), getMaximum(),
                isAdjusting);
    }

    public void setMaximum(int n) {
        int newMin = Math.min(n, getMinimum());
        int newValue = Math.min(n, getValue());
        int newExtent = Math.min(n - newValue, getExtent());
        setRangeProperties(newValue, newExtent, newMin, n, isAdjusting);
    }

    public void setMinimum(int n) {
        int newMax = Math.max(n, getMaximum());
        int newValue = Math.max(n, getValue());
        int newExtent = Math.min(newMax - newValue, getExtent());
        setRangeProperties(newValue, newExtent, n, newMax, isAdjusting);
    }

    public void setValue(int n) {
        int newValue = Math.max(n, getMinimum());
        if (newValue + extent > getMaximum()) {
            newValue = getMaximum() - extent;
        }
        setRangeProperties(newValue, extent, getMinimum(), getMaximum(),
                isAdjusting);
    }

    public void setValueIsAdjusting(boolean adjusting) {
        setRangeProperties(getValue(), extent, getMinimum(), getMaximum(),
                adjusting);
    }

    public void setRangeProperties(int newValue, int newExtent, int newMin,
            int newMax, boolean adjusting) {
        if (newMin > newMax) {
            newMin = newMax;
        }
        if (newValue > newMax) {
            newMax = newValue;
        }
        if (newValue < newMin) {
            newMin = newValue;
        }

        /*
         * Convert the addends to long so that extent can be Integer.MAX_VALUE
         * without rolling over the sum. A JCK test covers this, see bug
         * 4097718.
         */
        if (((long)newExtent + (long)newValue) > newMax) {
            newExtent = newMax - newValue;
        }
        if (newExtent < 0) {
            newExtent = 0;
        }
        boolean changed = (newValue != getValue()) || (newExtent != extent)
                || (newMin != getMinimum()) || (newMax != getMaximum())
                || (adjusting != isAdjusting);
        if (changed) {
            updateValueModelSilently(newValue);
            extent = newExtent;
            this.range = new Range(newMin, newMax);
            isAdjusting = adjusting;
            fireStateChanged();
        }
    }

    private void updateValueModelSilently(int newValue) {
        valueModel.removeValueChangeListener(currentValueChangeHandler);
        valueModel.setValue(new Integer(newValue));
        valueModel.addValueChangeListener(currentValueChangeHandler);
    }

    public String toString() {
        return new ToStringBuilder(this).appendProperties().toString();
    }

}