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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.MutableComboBoxModel;

import org.springframework.util.ObjectUtils;

/**
 * @author Keith Donald
 */
public class ComboBoxListModel extends ListListModel implements ComboBoxModel,
        MutableComboBoxModel {
    private Object selectedItem;

    private Comparator sorter;

    public ComboBoxListModel() {
        this(new ArrayList());
    }

    public ComboBoxListModel(List items) {
        super(items);
        if (getSize() > 0) {
            selectedItem = getElementAt(0);
        }
    }

    public ComboBoxListModel(List items, Comparator sorter) {
        super(items, sorter);
        if (getSize() > 0) {
            selectedItem = getElementAt(0);
        }
    }

    /**
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selectedItem;
    }

    /**
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object anItem) {
        if (!ObjectUtils.nullSafeEquals(selectedItem, anItem)) {
            this.selectedItem = anItem;
            fireContentsChanged(this, -1, -1);
        }
    }

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, Object o) {
        super.add(index, o);
        setSelectedItemIfNecessary(o);
    }

    protected void setSelectedItemOnNullValue() {
        setSelectedItemIfNecessary(null);
    }

    protected void setSelectedItemIfNecessary(Object o) {
        if (getItems().size() >= 1 && getSelectedItem() == null) {
            if (o != null) {
                setSelectedItem(o);
            }
            else {
                setSelectedItem(getItems().get(0));
            }
        }
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o) {
        boolean result = super.add(o);
        if (result) {
            setSelectedItemIfNecessary(o);
        }
        return result;
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        boolean result = super.addAll(c);
        if (result) {
            setSelectedItemIfNecessary(null);
        }
        return result;
    }

    /**
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection c) {
        boolean result = super.addAll(index, c);
        if (result) {
            setSelectedItemIfNecessary(null);
        }
        return result;
    }

    /**
     * @see java.util.Collection#clear()
     */
    public void clear() {
        super.clear();
        this.selectedItem = null;
    }

    /**
     * @see java.util.List#remove(int)
     */
    public Object remove(int index) {
        if (get(index) == selectedItem) {
            if (index == 0) {
                setSelectedItem(getSize() == 1 ? null : get(index + 1));
            }
            else {
                setSelectedItem(get(index - 1));
            }
        }
        return super.remove(index);
    }

    /**
     * @see javax.swing.MutableComboBoxModel#addElement(java.lang.Object)
     */
    public void addElement(Object anObject) {
        add(anObject);
    }

    /**
     * @see javax.swing.MutableComboBoxModel#insertElementAt(java.lang.Object,
     *      int)
     */
    public void insertElementAt(Object anObject, int index) {
        add(index, anObject);
    }

    /**
     * @see javax.swing.MutableComboBoxModel#removeElementAt(int)
     */
    public void removeElementAt(int index) {
        remove(index);
    }

    /**
     * @see javax.swing.MutableComboBoxModel#removeElement(java.lang.Object)
     */
    public void removeElement(Object o) {
        remove(o);
    }

}