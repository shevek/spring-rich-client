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
 */
public class ComboBoxModelAdapter extends AbstractListModel implements
        ComboBoxModel {

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
    public ComboBoxModelAdapter(ValueModel listHolder,
            ValueModel selectionHolder) {
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
    protected ComboBoxModelAdapter(SelectableItemsListModel selectableItems,
            ValueModel selectionHolder, boolean selectedItemFromListModelOnly) {
        if ((!selectedItemFromListModelOnly) && (selectionHolder == null)) { throw new NullPointerException(
                "The selectionHolder must not be null"); }
        this.selectableItems = selectableItems;
        this.selectionHolder = selectionHolder;
        selectionChangeHandler = new SelectionHolderValueChangeHandler();
        if (selectionHolder != null) {
            selectableItems.setValue(selectionHolder.getValue());
            selectionHolder.addValueChangeListener(selectionChangeHandler);
        }
        selectableItemsValueChangeHandler = new SelectableItemsValueChangeHandler();
        selectableItems
                .addValueChangeListener(selectableItemsValueChangeHandler);
        selectableItems.addListDataListener(new ListDataChangeHandler());
    }

    public Object getSelectedItem() {
        if (selectionHolder != null) { return selectionHolder.getValue(); }
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
    private class SelectableItemsValueChangeHandler implements
            ValueChangeListener {
        public void valueChanged() {
            updateSelectionHolderSilently(selectableItems.getValue());
            fireContentsChanged(ComboBoxModelAdapter.this, -1, -1);
        }
    }

    private void updateSelectionHolderSilently(Object newValue) {
        if (selectionHolder == null) { return; }
        selectionHolder.removeValueChangeListener(selectionChangeHandler);
        selectionHolder.setValue(newValue);
        selectionHolder.addValueChangeListener(selectionChangeHandler);
    }

    /*
     * Listens to selectionHolder changes and fires a contents change event.
     */
    private class SelectionHolderValueChangeHandler implements
            ValueChangeListener {
        public void valueChanged() {
            updateSelectionInListSilently(selectionHolder.getValue());
            fireContentsChanged(ComboBoxModelAdapter.this, -1, -1);
        }
    }

    private void updateSelectionInListSilently(Object newValue) {
        selectableItems
                .removeValueChangeListener(selectableItemsValueChangeHandler);
        selectableItems.setValue(newValue);
        selectableItems
                .addValueChangeListener(selectableItemsValueChangeHandler);
    }

    /*
     * Handles ListDataEvents in the list model.
     */
    private class ListDataChangeHandler implements ListDataListener {
        public void intervalAdded(ListDataEvent evt) {
            fireIntervalAdded(ComboBoxModelAdapter.this, evt.getIndex0(), evt
                    .getIndex1());
        }

        public void intervalRemoved(ListDataEvent evt) {
            fireIntervalRemoved(ComboBoxModelAdapter.this, evt.getIndex0(), evt
                    .getIndex1());
        }

        public void contentsChanged(ListDataEvent evt) {
            fireContentsChanged(ComboBoxModelAdapter.this, evt.getIndex0(), evt
                    .getIndex1());
        }
    }
}