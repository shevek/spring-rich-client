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

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;

/**
 * An implementation of the {@link ComboBoxModel}interface that holds the
 * choice list in a {@link SelectableItemsListModel}and the selection in a
 * ValueModel.
 * <p>
 * 
 * Note that the combo's selectionHolder is different from the selection in the
 * SelectionInList because typical editable comboboxes allow selection of items
 * that are not elements of the list. If you want to restrict the possible
 * selections to elements in the list only, use the constructor that takes a
 * SelectableItemsListModel as the only parameter.
 * 
 * @author Karsten Lentzsch
 * @author Keith Donald
 */
public class ComboBoxModelAdapter extends AbstractListModel implements ComboBoxModel {

    private SelectableItemsListModel selectableItems;

    private ValueModel selectionHolder;

    private ValueChangeListener selectableItemsValueChangeHandler;

    private ValueChangeListener selectionChangeHandler;

    /**
     * Constructs a <code>ComboBoxAdapter</code> for the specified List of
     * items and the given selection holder. Structural changes in the list will
     * be ignored.
     */
    public ComboBoxModelAdapter(List items, ValueModel selectionHolder) {
        this(new SelectableItemsListModel(items), selectionHolder, false);
    }

    /**
     * Constructs a <code>ComboBoxAdapter</code> for the given
     * <code>ListModel</code> and selection holder.
     */
    public ComboBoxModelAdapter(ListModel listModel, ValueModel selectionHolder) {
        this(new SelectableItemsListModel(listModel), selectionHolder, false);
    }

    /**
     * Constructs a <code>ComboBoxAdapter</code> for the specified array of
     * items and the given selection holder. Structural changes in the array
     * will be ignored.
     */
    public ComboBoxModelAdapter(Object[] items, ValueModel selectionHolder) {
        this(new SelectableItemsListModel(items), selectionHolder, false);
    }

    /**
     * Constructs a <code>ComboBoxAdapter</code> for the specified list holder
     * and the given selection holder.
     */
    public ComboBoxModelAdapter(ValueModel listHolder, ValueModel selectionHolder) {
        this(new SelectableItemsListModel(listHolder), selectionHolder, false);
    }

    /**
     * Constructs a <code>ComboBoxValueModel</code> for the given
     * <code>SelectionInList</code>.
     */
    public ComboBoxModelAdapter(SelectableItemsListModel selectionInList) {
        this(selectionInList, null, true);
    }

    /**
     * With this constructor we have two selectionHolders which both can be
     * accessed from client code (the controller of the selectionInList f.i.).
     */
    protected ComboBoxModelAdapter(SelectableItemsListModel selectableItems, ValueModel selectionHolder,
            boolean selectedItemFromListModelOnly) {
        if ((!selectedItemFromListModelOnly) && (selectionHolder == null)) {
            throw new NullPointerException("The selectionHolder must not be null");
        }
        this.selectableItems = selectableItems;
        this.selectionHolder = selectionHolder;
        selectionChangeHandler = new SelectionHolderValueChangeHandler();
        if (selectionHolder != null) {
            selectableItems.setValue(selectionHolder.getValue());
            selectionHolder.addValueChangeListener(selectionChangeHandler);
        }
        selectableItemsValueChangeHandler = new SelectableItemsValueChangeHandler();
        selectableItems.addValueChangeListener(selectableItemsValueChangeHandler);
        selectableItems.addListDataListener(new ListDataChangeHandler());
    }

    public Object getSelectedItem() {
        if (selectionHolder != null) {
            return selectionHolder.getValue();
        }
        return selectableItems.getValue();
    }

    public void setSelectedItem(Object object) {
        if (selectionHolder != null) {
            selectionHolder.setValue(object);
        }
        else {
            selectableItems.setValue(object);
        }
    }

    public int getSize() {
        return selectableItems.getSize();
    }

    public Object getElementAt(int index) {
        return selectableItems.getElementAt(index);
    }

    /*
     * Listens to selectionInList changes and fires a contents change event.
     */
    private class SelectableItemsValueChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            updateSelectionHolderSilently(selectableItems.getValue());
            fireContentsChanged(ComboBoxModelAdapter.this, -1, -1);
        }
    }

    private void updateSelectionHolderSilently(Object newValue) {
        if (selectionHolder == null) {
            return;
        }
        selectionHolder.removeValueChangeListener(selectionChangeHandler);
        selectionHolder.setValue(newValue);
        selectionHolder.addValueChangeListener(selectionChangeHandler);
    }

    /*
     * Listens to selectionHolder changes and fires a contents change event.
     */
    private class SelectionHolderValueChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            updateSelectionInListSilently(selectionHolder.getValue());
            fireContentsChanged(ComboBoxModelAdapter.this, -1, -1);
        }
    }

    private void updateSelectionInListSilently(Object newValue) {
        selectableItems.removeValueChangeListener(selectableItemsValueChangeHandler);
        selectableItems.setValue(newValue);
        selectableItems.addValueChangeListener(selectableItemsValueChangeHandler);
    }

    /*
     * Handles ListDataEvents in the list model.
     */
    private class ListDataChangeHandler implements ListDataListener {
        public void intervalAdded(ListDataEvent evt) {
            fireIntervalAdded(ComboBoxModelAdapter.this, evt.getIndex0(), evt.getIndex1());
        }

        public void intervalRemoved(ListDataEvent evt) {
            fireIntervalRemoved(ComboBoxModelAdapter.this, evt.getIndex0(), evt.getIndex1());
        }

        public void contentsChanged(ListDataEvent evt) {
            fireContentsChanged(ComboBoxModelAdapter.this, evt.getIndex0(), evt.getIndex1());
        }
    }
}