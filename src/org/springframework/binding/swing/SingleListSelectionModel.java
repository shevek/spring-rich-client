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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.binding.value.BoundValueModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;

/**
 * A {@link ListSelectionModel}implementation that has the list index bound to
 * a {@link ValueModel}. Therefore this class supports only the
 * <code>SINGLE_SELECTION</code> mode where only one list index can be
 * selected at a time. In this mode the setSelectionInterval and
 * addSelectionInterval methods are equivalent, and only the second index
 * argument (the "lead index") is used.
 * 
 * @author Karsten Lenztch
 * @author Keith Donald
 */
public final class SingleListSelectionModel implements ListSelectionModel {

    private static final int MIN = -1;

    private static final int MAX = Integer.MAX_VALUE;

    private int firstAdjustedIndex = MAX;

    private int lastAdjustedIndex = MIN;

    private int firstChangedIndex = MAX;

    private int lastChangedIndex = MIN;

    /**
     * Refers to the selection index holder.
     */
    private final BoundValueModel selectionIndexHolder;

    /**
     * Indicates if the selection is undergoing a series of changes.
     */
    private boolean valueIsAdjusting;

    /**
     * Holds the <code>List</code> of event listeners.
     */
    private EventListenerList listenerList = new EventListenerList();

    private PropertyChangeListener selectionIndexChangeHandler;

    /**
     * Constructs a <code>SingleListSelectionAdapter</code> with a empty
     * default selection index holder.
     */
    public SingleListSelectionModel() {
        this(new ValueHolder(new Integer(Integer.MIN_VALUE)));
    }

    /**
     * Constructs a <code>SingleListSelectionAdapter</code> with the given
     * selection index holder.
     * 
     * @param selectionIndexHolder
     *            holds the selection index
     */
    public SingleListSelectionModel(BoundValueModel selectionIndexHolder) {
        this.selectionIndexHolder = selectionIndexHolder;
        this.selectionIndexChangeHandler = new SelectionIndexChangeHandler();
        this.selectionIndexHolder.addPropertyChangeListener(selectionIndexChangeHandler);
    }

    /*
     * Listens to changes of the selection index.
     */
    private class SelectionIndexChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Object oldValue = evt.getOldValue();
            Object newValue = evt.getNewValue();
            int oldIndex = (oldValue == null) ? MIN : ((Integer)oldValue).intValue();
            int newIndex = (newValue == null) ? MIN : ((Integer)newValue).intValue();
            setSelectionIndex(oldIndex, newIndex);
        }
    }

    public BoundValueModel getSelectionIndexHolder() {
        return selectionIndexHolder;
    }

    private void setSelectionIndex(int newSelectionIndex) {
        setSelectionIndex(getSelectionIndex(), newSelectionIndex);
    }

    private int getSelectionIndex() {
        Object value = selectionIndexHolder.getValue();
        return (value == null) ? MIN : ((Integer)value).intValue();
    }

    private void setSelectionIndex(int oldSelectionIndex, int newSelectionIndex) {
        if (oldSelectionIndex == newSelectionIndex) {
            return;
        }
        markDirty(oldSelectionIndex);
        markDirty(newSelectionIndex);
        updateSelectionIndexHolderSilently(newSelectionIndex);
        fireValueChanged();
    }

    private void updateSelectionIndexHolderSilently(int newSelectionIndex) {
        selectionIndexHolder.removePropertyChangeListener(selectionIndexChangeHandler);
        selectionIndexHolder.setValue(new Integer(newSelectionIndex));
        selectionIndexHolder.addPropertyChangeListener(selectionIndexChangeHandler);
    }

    public void addSelectionInterval(int index0, int index1) {
        setSelectionInterval(index0, index1);
    }

    public void setSelectionInterval(int index0, int index1) {
        if ((index0 == -1) || (index1 == -1)) {
            return;
        }
        setSelectionIndex(index1);
    }

    public void removeSelectionInterval(int index0, int index1) {
        if ((index0 == -1) || (index1 == -1)) {
            return;
        }
        int max = Math.max(index0, index1);
        int min = Math.min(index0, index1);
        if ((min <= getSelectionIndex()) && (getSelectionIndex() <= max)) {
            clearSelection();
        }
    }

    public int getMinSelectionIndex() {
        return getSelectionIndex();
    }

    public int getMaxSelectionIndex() {
        return getSelectionIndex();
    }

    public boolean isSelectedIndex(int index) {
        return index < 0 ? false : index == getSelectionIndex();
    }

    public int getAnchorSelectionIndex() {
        return getSelectionIndex();
    }

    public void setAnchorSelectionIndex(int newSelectionIndex) {
        setSelectionIndex(newSelectionIndex);
    }

    public int getLeadSelectionIndex() {
        return getSelectionIndex();
    }

    public void setLeadSelectionIndex(int newSelectionIndex) {
        setSelectionIndex(newSelectionIndex);
    }

    public void clearSelection() {
        setSelectionIndex(-1);
    }

    public boolean isSelectionEmpty() {
        return getSelectionIndex() == -1;
    }

    public void insertIndexInterval(int index, int length, boolean before) {
        /*
         * The first new index will appear at insMinIndex and the last one will
         * appear at insMaxIndex
         */
        if (isSelectionEmpty()) {
            return;
        }

        int insMinIndex = (before) ? index : index + 1;
        int selectionIndex = getSelectionIndex();
        if (selectionIndex < insMinIndex) {
            // The added elements are after the index; do nothing.
        }
        else {
            setSelectionIndex(selectionIndex + length);
        }
    }

    public void removeIndexInterval(int index0, int index1) {
        if ((index0 < -1) || (index1 < -1)) {
            throw new IndexOutOfBoundsException("Both indices must be greater or equals to -1.");
        }
        if (isSelectionEmpty()) {
            return;
        }
        int lower = Math.min(index0, index1);
        int upper = Math.max(index0, index1);
        int selectionIndex = getSelectionIndex();

        if ((lower <= selectionIndex) && (selectionIndex <= upper)) {
            clearSelection();
        }
        else if (upper < selectionIndex) {
            int translated = selectionIndex - (upper - lower + 1);
            setSelectionInterval(translated, translated);
        }
    }

    /**
     * This property is true if upcoming changes to the value of the model
     * should be considered a single event. For example if the model is being
     * updated in response to a user drag, the value of the valueIsAdjusting
     * property will be set to true when the drag is initiated and be set to
     * false when the drag is finished. This property allows listeners to to
     * update only when a change has been finalized, rather than always handling
     * all of the intermediate values.
     * 
     * @param newValueIsAdjusting
     *            The new value of the property.
     * @see #getValueIsAdjusting()
     */
    public void setValueIsAdjusting(boolean newValueIsAdjusting) {
        boolean oldValueIsAdjusting = valueIsAdjusting;
        if (oldValueIsAdjusting == newValueIsAdjusting) {
            return;
        }
        valueIsAdjusting = newValueIsAdjusting;
        fireValueChanged(newValueIsAdjusting);
    }

    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    /**
     * Sets the selection mode. Only <code>SINGLE_SELECTION</code> is allowed
     * in this implementation. Other modes are not supported and will throw an
     * <code>IllegalArgumentException</code>.
     * <p>
     * 
     * With <code>SINGLE_SELECTION</code> only one list index can be selected
     * at a time. In this mode the setSelectionInterval and addSelectionInterval
     * methods are equivalent, and only the second index argument (the "lead
     * index") is used.
     * 
     * @param selectionMode
     *            the mode to be set
     * @see #getSelectionMode()
     * @see javax.swing.ListSelectionModel#setSelectionMode(int)
     */
    public void setSelectionMode(int selectionMode) {
        if (selectionMode != SINGLE_SELECTION) {
            throw new UnsupportedOperationException(
                    "The SingleListSelectionAdapter must be used in single selection mode.");
        }
    }

    public int getSelectionMode() {
        return ListSelectionModel.SINGLE_SELECTION;
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        listenerList.add(ListSelectionListener.class, listener);
    }

    public void removeListSelectionListener(ListSelectionListener listener) {
        listenerList.remove(ListSelectionListener.class, listener);
    }

    public ListSelectionListener[] getListSelectionListeners() {
        return (ListSelectionListener[])listenerList.getListeners(ListSelectionListener.class);
    }

    // Updates first and last change indices
    private void markDirty(int index) {
        if (index < 0) {
            return;
        }
        firstAdjustedIndex = Math.min(firstAdjustedIndex, index);
        lastAdjustedIndex = Math.max(lastAdjustedIndex, index);
    }

    private void fireValueChanged() {
        if (lastAdjustedIndex == MIN) {
            return;
        }

        /*
         * If getValueAdjusting() is true, (eg. during a drag opereration)
         * record the bounds of the changes so that, when the drag finishes (and
         * setValueAdjusting(false) is called) we can post a single event with
         * bounds covering all of these individual adjustments.
         */
        if (getValueIsAdjusting()) {
            firstChangedIndex = Math.min(firstChangedIndex, firstAdjustedIndex);
            lastChangedIndex = Math.max(lastChangedIndex, lastAdjustedIndex);
        }

        /*
         * Change the values before sending the event to the listeners in case
         * the event causes a listener to make another change to the selection.
         */
        int oldFirstAdjustedIndex = firstAdjustedIndex;
        int oldLastAdjustedIndex = lastAdjustedIndex;
        firstAdjustedIndex = MAX;
        lastAdjustedIndex = MIN;
        fireValueChanged(oldFirstAdjustedIndex, oldLastAdjustedIndex);
    }

    /**
     * Notifies listeners that we have ended a series of adjustments.
     * 
     * @param isAdjusting
     *            true if there are multiple changes
     */
    private void fireValueChanged(boolean isAdjusting) {
        if (lastChangedIndex == MIN) {
            return;
        }

        /*
         * Change the values before sending the event to the listeners in case
         * the event causes a listener to make another change to the selection.
         */
        int oldFirstChangedIndex = firstChangedIndex;
        int oldLastChangedIndex = lastChangedIndex;
        firstChangedIndex = MAX;
        lastChangedIndex = MIN;
        fireValueChanged(oldFirstChangedIndex, oldLastChangedIndex, isAdjusting);
    }

    /**
     * Notifies <code>ListSelectionListeners</code> that the value of the
     * selection, in the closed interval <code>firstIndex</code>,
     * <code>lastIndex</code>, has changed.
     * 
     * @param firstIndex
     *            the first index that has changed
     * @param lastIndex
     *            the last index that has changed
     */
    private void fireValueChanged(int firstIndex, int lastIndex) {
        fireValueChanged(firstIndex, lastIndex, getValueIsAdjusting());
    }

    /**
     * Notifies all registered listeners that the selection has changed.
     * 
     * @param firstIndex
     *            the first index in the interval
     * @param lastIndex
     *            the last index in the interval
     * @param isAdjusting
     *            true if this is the final change in a series of adjustments
     * @see EventListenerList
     */
    private void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
        Object[] listeners = listenerList.getListenerList();
        ListSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null) {
                    e = new ListSelectionEvent(this, firstIndex, lastIndex, isAdjusting);
                }
                ((ListSelectionListener)listeners[i + 1]).valueChanged(e);
            }
        }
    }
}