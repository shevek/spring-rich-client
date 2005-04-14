/*
 * Copyright (c) 2002-2004 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.springframework.binding.swing;

import java.io.Serializable;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.core.ToStringCreator;
import org.springframework.rules.constraint.Range;

/**
 * Converts a <code>ValueModel</code> to <code>BoundedRangeModel</code>.
 * Therefore honors a minimum and maximum bound.
 * <p>
 * 
 * <strong>Example: </strong>
 * 
 * <pre>
 * int minSaturation = 0;int maxSaturation = 255;
 *         ValueModel saturationModel = new PropertyAdapter(settingsBean, &quot;saturation&quot;);
 *         JSlider saturationSlider = new JSlider(
 *             new BoundedRangeAdapter(saturationModel, 
 *                                     0, 
 *                                     minSaturation, 
 *                                     maxSaturation));
 *   
 *  
 * </pre>
 * 
 * @author Karsten Lentzsch
 * @author Keith Donald
 * @see javax.swing.JSlider
 */
public final class BoundedRangeModelAdapter implements BoundedRangeModel, Serializable {

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
    public BoundedRangeModelAdapter(ValueModel valueModel, int extent, int min, int max) {
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
        if (!((maximum >= minimum) && (initialValue >= minimum) && ((initialValue + extent) >= initialValue) && ((initialValue + extent) <= maximum))) {
            throw new IllegalArgumentException("Invalid bounded range properties");
        }
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
        setRangeProperties(value, newExtent, getMinimum(), getMaximum(), isAdjusting);
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
        setRangeProperties(newValue, extent, getMinimum(), getMaximum(), isAdjusting);
    }

    public void setValueIsAdjusting(boolean adjusting) {
        setRangeProperties(getValue(), extent, getMinimum(), getMaximum(), adjusting);
    }

    public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting) {
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
        boolean changed = (newValue != getValue()) || (newExtent != extent) || (newMin != getMinimum())
                || (newMax != getMaximum()) || (adjusting != isAdjusting);
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
        return new ToStringCreator(this).toString();
    }

}