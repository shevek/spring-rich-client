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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedCollectionValueModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.richclient.form.binding.support.AbstractBinding;
import org.springframework.richclient.list.DynamicListModel;

public class ListBinding extends AbstractBinding {

    private final SelectedItemChangeHandler selectedItemChangeHandler = new SelectedItemChangeHandler();

    private final JList list;

    private ListModel model;

    private ValueModel selectedItemHolder;

    private ValueModel selectableItemsHolder;

    private ListCellRenderer renderer;

    private Comparator comparator;

    private Integer selectionMode = null;

    private Class selectedItemType;

    private Class concreteSelectedType;

    public ListBinding(JList list, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.list = list;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public void setModel(ListModel model) {
        this.model = model;
    }

    public void setRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    public void setSelectableItemsHolder(ValueModel selectableItemsHolder) {
        this.selectableItemsHolder = selectableItemsHolder;
    }

    public void setSelectedItemHolder(ValueModel selectedItemHolder) {
        this.selectedItemHolder = selectedItemHolder;
    }

    public void setSelectionMode(final Integer selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setSelectedItemType(final Class selectedItemType) {
        this.selectedItemType = selectedItemType;
    }

    protected Class getSelectedItemType() {
        if (this.selectedItemType == null) {
            if (this.selectedItemHolder != null && this.selectedItemHolder.getValue() != null) {
                setSelectedItemType(this.selectedItemHolder.getValue().getClass());
            }
        }

        return this.selectedItemType;
    }

    /**
     * Determine if the selected item type can be multi-valued (is a collection
     * or an array.
     * @return boolean <code>true</code> if the <code>selectedItemType</code> is a
     * Collection or an Array.
     */
    protected boolean isSelectedItemMultiValued() {
        return isSelectedItemACollection() || isSelectedItemAnArray();
    }

    /**
     * Determine if the selected item type can be multi-valued (is a collection
     * or an array.
     * @return boolean <code>true</code> if the <code>selectedItemType</code> is a
     * Collection or an Array.
     */
    protected boolean isSelectedItemAnArray() {
        Class itemType = getSelectedItemType();
        return itemType != null && itemType.isArray();
    }

    protected boolean isSelectedItemACollection() {
        return getSelectedItemType() != null && Collection.class.isAssignableFrom(getSelectedItemType());
    }

    protected boolean isTrulyMultipleSelect() {
        return list.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION && isSelectedItemMultiValued();
    }

    protected Class getConcreteSelectedType() {
        if (concreteSelectedType == null) {
            if (isSelectedItemACollection()) {
                concreteSelectedType = BufferedCollectionValueModel.getConcreteCollectionType(getSelectedItemType());
            }
            else if (isSelectedItemAnArray()) {
                concreteSelectedType = getSelectedItemType().getComponentType();
            }
        }
        return concreteSelectedType;
    }

    protected JComponent doBindControl() {
        list.setModel(createModel());
        if (selectedItemHolder != null) {
            if (this.selectionMode != null) {
                list.setSelectionMode(this.selectionMode.intValue());
            }
            else if (isSelectedItemMultiValued()) {
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
            selectedValueChanged();
            selectedItemHolder.addValueChangeListener(selectedItemChangeHandler);
            list.addListSelectionListener(selectedItemChangeHandler);
        }
        if (renderer != null) {
            list.setCellRenderer(renderer);
        }
        return list;
    }

    protected void selectedValueChanged() {
        if (isSelectedItemMultiValued()) {
            final int[] indices = indicesOf(selectedItemHolder.getValue());
            if (indices.length < 1) {
                list.clearSelection();
            }
            else if (isTrulyMultipleSelect()) {
                list.setSelectedIndices(indices);
                // The selection may now be different than what is reflected in
                // collection property if this is SINGLE_INTERVAL_SELECTION, so
                // modify if needed...
                updateSelectionHolderFromList();
            }
            else {
                // If it is a collection value but multiple selection is not enabled
                // then use the first item in the collection to select.  This can
                // only be the case if the selection property is a Collection type
                // but client code explicitly set the SELECTION_MODE_KEY context
                // flag to SINGLE_SELECTION.
                list.setSelectedIndex(indices[0]);
                // The selection may now be different than what is reflected in
                // collection property, so modify if needed...
                updateSelectionHolderFromList();
            }
        }
        else {
            if (selectedItemHolder.getValue() != null) {
                list.setSelectedValue(selectedItemHolder.getValue(), true);
            }
            else {
                list.clearSelection();
            }
        }
    }

    /**
     * Return an array of indices in the selectableItems for each element in the provided set.  The set can
     * be either a Collection or an Array.
     * @param itemSet Either an array or a Collection of items
     * @return array of indices of the elements in itemSet within the selectableItems
     */
    protected int[] indicesOf(final Object itemSet) {
        int[] ret = null;

        if (itemSet instanceof Collection) {
            Collection collection = (Collection)itemSet;
            ret = new int[collection.size()];
            int i = 0;
            for (Iterator iter = collection.iterator(); iter.hasNext(); i++) {
                ret[i] = indexOf(iter.next());
            }
        }
        else if (itemSet != null && itemSet.getClass().isArray()) {
            Object[] items = (Object[])itemSet;
            ret = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                ret[i] = indexOf(items[i]);
            }
        }
        else if (itemSet == null) {
            ret = new int[0];
        }
        else {
            throw new IllegalArgumentException("itemSet must be an array or a Collection");
        }

        return ret;
    }

    protected int indexOf(final Object o) {
        final ListModel listModel = list.getModel();
        final int size = listModel.getSize();
        for (int i = 0; i < size; i++) {
            if (comparator == null) {
                if (o.equals(listModel.getElementAt(i))) {
                    return i;
                }
            }
            else if (comparator.compare(o, listModel.getElementAt(i)) == 0) {
                return i;
            }
        }

        return -1;
    }

    private ListModel createModel() {
        if (model != null) {
            return model;
        }
        else {
            ListListModel model;
            if (selectableItemsHolder != null) {
                model = new DynamicListModel(selectableItemsHolder);
            }
            else {
                model = new ListListModel();
            }
            model.setComparator(comparator);
            return model;
        }
    }

    protected void readOnlyChanged() {
        list.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void enabledChanged() {
        list.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void updateSelectionHolderFromList() {
        final Object[] selected = list.getSelectedValues();

        if (isSelectedItemACollection()) {
            try {
                // In order to properly handle buffered forms, we will
                // create a new collection to hold the new selection.
                final Collection newSelection = (Collection)getConcreteSelectedType().newInstance();
                if (selected != null && selected.length > 0) {
                    for (int i = 0; i < selected.length; i++) {
                        newSelection.add(selected[i]);
                    }
                }

                // Only modify the selectedItemHolder if the selection is actually
                // changed.
                final Collection oldSelection = (Collection)selectedItemHolder.getValue();
                if (oldSelection == null || !oldSelection.containsAll(newSelection)
                        || oldSelection.size() != newSelection.size()) {
                    selectedItemHolder.setValueSilently(newSelection, selectedItemChangeHandler);
                }
            }
            catch (InstantiationException e1) {
                throw new RuntimeException("Unable to instantiate new concrete collection class for new selection.", e1);
            }
            catch (IllegalAccessException e1) {
                throw new RuntimeException(e1);
            }
        }
        else if (isSelectedItemAnArray()) {

            final Object[] newSelection = (Object[])Array.newInstance(getConcreteSelectedType(), selected.length);
            for (int i = 0; i < selected.length; i++) {
                newSelection[i] = selected[i];
            }

            // Only modify the selectedItemHolder if the selection is actually
            // changed.
            final Object[] oldSelection = (Object[])selectedItemHolder.getValue();
            if (oldSelection == null || oldSelection.length != newSelection.length
                    || !Arrays.equals(oldSelection, newSelection)) {
                selectedItemHolder.setValueSilently(newSelection, selectedItemChangeHandler);
            }
        }
        else {
            selectedItemHolder.setValueSilently(list.getSelectedValue(), selectedItemChangeHandler);
        }
    }

    private class SelectedItemChangeHandler implements PropertyChangeListener, ListSelectionListener {
        public void propertyChange(PropertyChangeEvent evt) {
            selectedValueChanged();
        }

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                updateSelectionHolderFromList();
            }
        }
    }
}