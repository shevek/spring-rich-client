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
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.binding.value.BoundValueModel;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.util.ObjectUtils;

/**
 * A {@link ValueModel} that represents a selection in a list of objects. The
 * list, the selection, and the selection index are exposed using
 * <code>ValueModel</code>s, so others can observe changes in the list and
 * selection.
 * <p>
 * 
 * This class also implements the {@link ListModel} interface that allows API
 * users to observe fine grained changes in the structure and contents of the
 * list. Hence instances of this class can be used directly as model of a
 * <code>JList</code>. If you want to use a <code>SelectableItemsListModel</code>
 * with a <code>JComboBox</code> or <code>JTable</code>, you can convert
 * the <code>SelectableItemsListModel</code> to the associated component model
 * interfaces using the classes {@link ComboBoxModelAdapter} and
 * {@link AbstractTableModelAdapter} respectively.
 * <p>
 * 
 * The <code>SelectableItemsListModel</code> supports two list types as content of its
 * list holder: <code>List</code> and <code>ListModel</code>. The two modes
 * differ in how precise this class can fire events about changes to the content
 * and structure of the list. If you use a <code>List</code>, this class can
 * only report that the list changes completely; this is done by emitting a
 * <code>valueChanged()</code> for the <em>list</em> property. Also, a
 * <code>ListDataEvent</code> is fired that reports a complete change. In
 * contrast, if you use a <code>ListModel</code> it will report the same
 * <code>valueChanged()</code>. But fine grained changes in the list model
 * will be fired by this class to notify observes about changes in the content,
 * added and removed elements.
 * <p>
 * 
 * If the list content doesn't change at all, or if it always changes
 * completely, you can work well with both <code>List</code> content and
 * <code>ListModel</code> content. But if the list structure or content
 * changes, the <code>ListModel</code> reports more fine grained events to
 * registered <code>ListDataListener</code>s, which in turn allows list views
 * to chooser better user interface gestures: for example, a table with scroll
 * pane may retain the current selection and scroll offset.
 * <p>
 * 
 * An example for using a <code>ListModel</code> in a
 * <code>SelectableItemsListModel</code> is the asynchronous transport of list elements
 * from a server to a client. Let's say you transport the list elements in
 * portions of 10 elements to improve the application's responsiveness. The user
 * can then select and work with the <code>SelectableItemsListModel</code> as soon as
 * the <code>ListModel</code> gets populated. If at a later time new elements
 * are added to the list model, the <code>SelectableItemsListModel</code> can retain
 * the selection index (and selection) and will just report a
 * <code>ListDataEvent</code> about the interval added. <code>JList</code>,
 * <code>JTable</code> and <code>JComboBox</code> will then just add the new
 * elements at the end of the list presentation.
 * <p>
 * 
 * If you want to combine <code>List</code> operations and the
 * <code>ListModel</code> change reports, you may consider using an
 * implementation that combines these two interfaces.
 * <p>
 * 
 * <strong>Imporant Note: </strong> If you change the <code>ListModel</code>
 * instance, either by calling <code>#setListModel(ListModel)</code> or by
 * setting a new value to the underlying list holder, you must ensure that the
 * list holder throws a <code>PropertyChangeEvent</code> whenever the instance
 * changes. This event is used to remove a <code>ListDataListener</code> from
 * the old ListModel instance and is later used to add it to the new ListModel
 * instance. It is easy to violate this constraint, just because the
 * <code>PropertyChangeSupport</code> helper class that is used by many beans
 * checks a changed instance via <code>#equals</code>,<code>==</code>.
 * For example, if you change the SelectableItemsListModel's list model from an empty
 * list <code>L1</code> to another empty list instance <code>L2</code>, the
 * PropertyChangeSupport won't generate a PropertyChangeEvent, and so, the
 * SelectableItemsListModel won't know about the change, which may lead to unexpected
 * behavior.
 * <p>
 * 
 * <strong>Constraints: </strong> The list holder holds instances of
 * {@link List}or {@link ListModel}, the selection holder values of type
 * <code>Object</code> and the selection index holder of type
 * <code>Integer</code>. The selection index holder must hold non-null index
 * values; however, when firing an index value change event, both the old and
 * new value may be null. If the ListModel changes, the underyling ValueModel
 * must fire a PropertyChangeEvent.
 * <p>
 * 
 * @author Karsten Lentzsch
 * @author Keith Donald
 * 
 * @see ValueModel
 * @see List
 * @see ListModel
 */

public final class SelectableItemsListModel extends AbstractValueModel implements ListModel {

    /**
     * The name of the bound write-only <em>list</em> property.
     */
    public final static String LIST_PROPERTY_NAME = "list";

    /**
     * The name of the bound read-write <em>selection</em> property.
     */
    public final static String SELECTION_PROPERTY = "selection";

    /**
     * The name of the bound read-only <em>selectionEmpty</em> property.
     */
    public final static String SELECTION_EMPTY_PROPERTY = "selectionEmpty";

    /**
     * The name of the bound read-write <em>selectionIndex</em> property.
     */
    public final static String SELECTION_INDEX_PROPERTY = "selectionIndex";

    /**
     * A special index that indicates that we have no selection.
     */
    private static final int EMPTY_SELECTION_INDEX = -1;

    /**
     * An empty <code>ListModel</code> that is used if the list holder's
     * content is null.
     */
    private static final ListModel EMPTY_LIST_MODEL = new EmptyListModel();

    /**
     * Holds a <code>List</code> or <code>ListModel</code> that in turn
     * holds the elements.
     */
    private ValueModel selectableItemsHolder;

    /**
     * Holds a copy of the listHolder's value. Used as the old list when the
     * listHolder's value changes. Required because a ValueModel may use
     * <code>null</code> as old value, but the SelectableItemsListModel must know about
     * the old and the new list.
     */
    private Object list;

    /**
     * Holds the selection, an instance of <code>Object</code>.
     */
    private BoundValueModel selectionHolder;

    /**
     * Holds the selection index, an <code>Integer</code>.
     */
    private ValueModel selectionIndexHolder;

    /**
     * Handles changes of the list.
     */
    private final ValueChangeListener selectableItemsChangeHandler;

    /**
     * The <code>PropertyChangeListener</code> used to handle changes of the
     * selection.
     */
    private final PropertyChangeListener selectionChangeHandler;

    /**
     * The <code>PropertyChangeListener</code> used to handle changes of the
     * selection index.
     */
    private final ValueChangeListener selectionIndexChangeHandler;

    /**
     * Handles structural and content changes of the list model.
     */
    private final ListDataListener listDataChangeHandler;

    /**
     * Refers to the list of list data listeners that is used to notify
     * registered listeners if the list model changes.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Duplicates the value of the selectionIndexHolder. Used to provide better
     * old values in PropertyChangeEvents fired after selectionIndex changes and
     * selection changes.
     */
    private int oldSelectionIndex;

    /**
     * Constructs a <code>SelectableItemsListModel</code> on the given list using
     * defaults for the selection holder and selection index holder.
     * <p>
     * 
     * <strong>Note: </strong> Favor <code>ListModel</code> over
     * <code>List</code> when working with the SelectableItemsListModel. Why? The
     *  can work with both types. What's the difference?
     * ListModel provides all list access features required by the
     * 's. In addition it reports more fine grained change
     * events, instances of <code>ListDataEvents</code>. In contrast
     * developer often create Lists and operate on them and the ListModel may be
     * inconvenient for these operations.
     * <p>
     * 
     * A convenient solution for this situation is to use the
     * <code>ArrayListModel</code> and <code>LinkedListModel</code> classes.
     * These implement both List and ListModel, offer the standard List
     * operations and report the fine grained ListDataEvents.
     * 
     * @param list
     *            the initial list
     */
    public SelectableItemsListModel(List list) {
        this(new ValueHolder(list));
    }

    /**
     * Constructs a <code></code> on the given item array using
     * defaults for the selection holder and selection index holder. The
     * specified array will be converted to a List.
     * <p>
     * 
     * Changes to the list "write through" to the array, and changes to the
     * array contents will be reflected in the list.
     * 
     * @param listItems
     *            the array of initial items
     */
    public SelectableItemsListModel(Object[] listItems) {
        this(Arrays.asList(listItems));
    }

    public SelectableItemsListModel(Object[] listItems, ValueModel selectionHolder) {
        this(new ValueHolder(Arrays.asList(listItems)), selectionHolder);
    }

    /**
     * Constructs a <code></code> on the given list model using
     * defaults for the selection holder and selection index holder.
     * 
     * @param listModel
     *            the initial list model
     */
    public SelectableItemsListModel(ListModel listModel) {
        this(new ValueHolder(listModel));
    }

    /**
     * Constructs a <code></code> on the given list holder
     * using defaults for the selection holder and selection index holder.
     * 
     * @param selectableItemsHolder
     *            holds the list or list model
     * @throws NullPointerException
     *             if <code>listHolder</code> is <code>null</code>
     */
    public SelectableItemsListModel(ValueModel selectableItemsHolder) {
        this(selectableItemsHolder, new ValueHolder());
    }

    /**
     * Constructs a <code></code> on the given list holder,
     * selection holder and selection index holder.
     * 
     * @param selectableItemsHolder
     *            holds the list or list model
     * @param selectionHolder
     *            holds the selection
     * @throws NullPointerException
     *             if <code>listHolder</code> or <code>selectionHolder</code>
     *             is <code>null</code>
     */
    public SelectableItemsListModel(ValueModel selectableItemsHolder, ValueModel selectionHolder) {
        this(selectableItemsHolder, selectionHolder, new ValueHolder(new Integer(EMPTY_SELECTION_INDEX)));
    }

    /**
     * Constructs a <code></code> on the given list holder,
     * selection holder and selection index holder.
     * 
     * @param selectableItemsHolder
     *            holds the list or list model
     * @param selectionHolder
     *            holds the selection
     * @param selectionIndexHolder
     *            holds the selection index
     * @throws NullPointerException
     *             if <code>listHolder</code>,<code>selectionHolder</code>,
     *             or <code>selectionIndexHolder</code> is <code>null</code>
     */
    public SelectableItemsListModel(ValueModel selectableItemsHolder, ValueModel selectionHolder,
            ValueModel selectionIndexHolder) {
        this.selectableItemsHolder = selectableItemsHolder;
        this.selectionHolder = (BoundValueModel)selectionHolder;
        this.selectionIndexHolder = selectionIndexHolder;
        initSelectionIndex();

        selectableItemsChangeHandler = new SelectableItemsChangeHandler();
        selectionChangeHandler = new SelectionChangeHandler();
        selectionIndexChangeHandler = new SelectionIndexChangeHandler();
        listDataChangeHandler = new ListDataChangeHandler();

        this.selectableItemsHolder.addValueChangeListener(selectableItemsChangeHandler);
        this.selectionHolder.addPropertyChangeListener(selectionChangeHandler);
        this.selectionIndexHolder.addValueChangeListener(selectionIndexChangeHandler);

        // If the ValueModel holds a ListModel observe list data changes too.
        this.list = selectableItemsHolder.getValue();
        if (this.list != null && (this.list instanceof ListModel)) {
            ((ListModel)this.list).addListDataListener(listDataChangeHandler);
        }
    }

    /**
     * Sets the index according to the selection. This method is invoked by the
     * constructors to synchronize the selection and index. No listeners are
     * installed yet.
     */
    private void initSelectionIndex() {
        Object selectionValue = selectionHolder.getValue();
        if (selectionValue != null) {
            setSelectionIndex(indexOf(selectionValue));
        }
        else {
            oldSelectionIndex = getSelectionIndex();
        }
    }

    /**
     * Returns the current selection, <code>null</code> if the selection index
     * does not represent a selection in the list.
     * 
     * @return the selected element - if any
     */
    public Object getValue() {
        return getSelection();
    }

    /**
     * Sets the first list element that equals the given value as selection.
     * 
     * @param newValue
     *            the new value to set
     */
    public void setValue(Object newValue) {
        setSelection(newValue);
    }

    /**
     * Looks up and returns the current selection using the current selection
     * index. Returns <code>null</code> if no object is selected or if the
     * list has no elements.
     * 
     * @return the current selection, <code>null</code> if none is selected
     */
    public Object getSelection() {
        return getElementAtSafely(getSelectionIndex());
    }

    /**
     * Sets the first list element that equals the given new selection as new
     * selection. Does nothing if the list is empty.
     * 
     * @param newSelection
     *            the object to be set as new selection
     */
    public void setSelection(Object newSelection) {
        if (!isEmpty()) {
            setSelectionIndex(indexOf(newSelection));
        }
    }

    public Object getElementAt(int index) {
        return getElementAt(getListHolder().getValue(), index);
    }

    private Object getElementAt(Object aList, int index) {
        if (aList == null) {
            throw new NullPointerException("The list contents is null.");
        }
        if (aList instanceof ListModel) {
            return ((ListModel)aList).getElementAt(index);
        }
        return ((List)aList).get(index);
    }

    private Object getElementAtSafely(int index) {
        return (index < 0 || index >= getSize()) ? null : getElementAt(index);
    }

    public int getSize() {
        return getSize(getListHolder().getValue());
    }

    private int getSize(Object list) {
        if (list == null) {
            return 0;
        }
        if (list instanceof ListModel) {
            return ((ListModel)list).getSize();
        }
        return ((List)list).size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public ListModel getListModel() {
        Object aList = getListHolder().getValue();
        if (aList == null) {
            return EMPTY_LIST_MODEL;
        }
        if (aList instanceof ListModel) {
            return (ListModel)aList;
        }
        return new ListModelAdapter((List)aList);
    }

    public void setListModel(ListModel newListModel) {
        getListHolder().setValue(newListModel);
    }

    public void setList(List newList) {
        getListHolder().setValue(newList);
    }

    public boolean isSelectionEmpty() {
        return !hasSelection();
    }

    public boolean hasSelection() {
        return getSelectionIndex() != EMPTY_SELECTION_INDEX;
    }

    /**
     * Clears the selection - if any.
     */
    public void clearSelection() {
        setSelectionIndex(EMPTY_SELECTION_INDEX);
    }

    /**
     * Returns the selection index.
     * 
     * @return the selection index
     * 
     * @throws NullPointerException
     *             if the selection index holder has a null Object set
     */
    public int getSelectionIndex() {
        return ((Integer)getSelectionIndexHolder().getValue()).intValue();
    }

    /**
     * Sets a new selection index. Does nothing if it is the same as before.
     * 
     * @param newSelectionIndex
     *            the selection index to be set
     * @throws IndexOutOfBoundsException
     *             if the new selection index is outside the bounds of the list
     */
    public void setSelectionIndex(int newSelectionIndex) {
        if (newSelectionIndex < EMPTY_SELECTION_INDEX || newSelectionIndex > getSize())
            throw new IndexOutOfBoundsException("The selection index must be between -1 and " + getSize());
        oldSelectionIndex = getSelectionIndex();
        if (oldSelectionIndex == newSelectionIndex) {
            return;
        }
        getSelectionIndexHolder().setValue(new Integer(newSelectionIndex));
    }

    /**
     * Returns the list holder.
     * 
     * @return the list holder
     */
    public ValueModel getListHolder() {
        return selectableItemsHolder;
    }

    /**
     * Returns the selection holder.
     * 
     * @return the selection holder
     */
    public ValueModel getSelectionHolder() {
        return selectionHolder;
    }

    /**
     * Returns the selection index holder.
     * 
     * @return the selection index holder
     */
    public ValueModel getSelectionIndexHolder() {
        return selectionIndexHolder;
    }

    protected int indexOf(Object element) {
        return indexOf(getListHolder().getValue(), element);
    }

    private int indexOf(Object list, Object element) {
        if (element == null || getSize(list) == 0) {
            return EMPTY_SELECTION_INDEX;
        }
        if (list instanceof List) {
            return ((List)list).indexOf(element);
        }

        // Search the first occurrence of element in the list model.
        ListModel listModel = (ListModel)list;
        int size = listModel.getSize();
        for (int index = 0; index < size; index++) {
            if (element.equals(listModel.getElementAt(index)))
                return index;
        }
        return EMPTY_SELECTION_INDEX;
    }

    /**
     * Updates the selection index to keep the selection if possible. Invoked
     * when the list changes. First checks for empty selection and an empty old
     * list; in both cases the new selection is empty. Otherwise looks up the
     * old selection, computes the index of the old selection in the new list
     * and finally sets this index as as selection index.
     * 
     * @param newList
     *            the list after the change
     */
    private void updateSelectionIndex(Object newList) {
        if (!hasSelection()) {
            return;
        }
        Object oldSelection = getSelectionHolder().getValue();
        setSelectionIndex(indexOf(newList, oldSelection));
    }

    public void addListDataListener(ListDataListener l) {
        listenerList.add(ListDataListener.class, l);
    }

    public void removeListDataListener(ListDataListener l) {
        listenerList.remove(ListDataListener.class, l);
    }

    public ListDataListener[] getListDataListeners() {
        return (ListDataListener[])listenerList.getListeners(ListDataListener.class);
    }

    private void fireContentsChanged(int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }
                ((ListDataListener)listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    private void fireIntervalAdded(int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }
                ((ListDataListener)listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    private void fireIntervalRemoved(int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }
                ((ListDataListener)listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    private static class EmptyListModel extends AbstractListModel {
        public int getSize() {
            return 0;
        }

        public Object getElementAt(int index) {
            return null;
        }
    }

    // Converts a List to ListModel by wrapping the underlying list.
    private static class ListModelAdapter extends AbstractListModel {
        private final List list;

        ListModelAdapter(List list) {
            this.list = list;
        }

        public int getSize() {
            return list.size();
        }

        public Object getElementAt(int index) {
            return list.get(index);
        }
    }

    /*
     * Handles changes of the list model.
     */
    private class SelectableItemsChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            Object oldList = list;
            updateList(oldList, selectableItemsHolder.getValue());
        }

        /**
         * Removes the list data change handler from the old list in case it is
         * a <code>ListModel</code> and adds it to new one in case it is a
         * <code>ListModel</code>. Also updates the selection index, fires a
         * property change for the list and a contents change event.
         * 
         * @param oldList
         *            the old list content
         * @param newList
         *            the new list content
         */
        private void updateList(Object oldList, Object newList) {
            if (oldList != null && (oldList instanceof ListModel)) {
                ((ListModel)oldList).removeListDataListener(listDataChangeHandler);
            }
            if (newList != null && (newList instanceof ListModel)) {
                ((ListModel)newList).addListDataListener(listDataChangeHandler);
            }
            list = newList;
            updateSelectionIndex(newList);
            firePropertyChange(LIST_PROPERTY_NAME, oldList, newList);
            fireContentsChanged(0, Integer.MAX_VALUE);
        }

    }

    /*
     * Listens to changes of the selection.
     */
    private class SelectionChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Object oldSelection = evt.getOldValue();
            Object newSelection = evt.getNewValue();
            int newSelectionIndex = indexOf(newSelection);
            firePropertyChange(SELECTION_PROPERTY, oldSelection, newSelection);
            fireValueChanged();
            firePropertyChange(SELECTION_EMPTY_PROPERTY, oldSelectionIndex == EMPTY_SELECTION_INDEX,
                    newSelectionIndex == EMPTY_SELECTION_INDEX);
            if (newSelectionIndex == oldSelectionIndex) {
                return;
            }
            oldSelectionIndex = newSelectionIndex;
            selectionIndexHolder.removeValueChangeListener(selectionIndexChangeHandler);
            selectionIndexHolder.setValue(new Integer(newSelectionIndex));
            selectionIndexHolder.addValueChangeListener(selectionIndexChangeHandler);
            firePropertyChange(SELECTION_INDEX_PROPERTY, oldSelectionIndex, newSelectionIndex);
        }
    }

    /*
     * Listens to changes of the selection index.
     */
    private class SelectionIndexChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            int newSelectionIndex = getSelectionIndex();
            firePropertyChange(SELECTION_INDEX_PROPERTY, oldSelectionIndex, newSelectionIndex);
            firePropertyChange(SELECTION_EMPTY_PROPERTY, oldSelectionIndex == EMPTY_SELECTION_INDEX,
                    newSelectionIndex == EMPTY_SELECTION_INDEX);
            fireValueChanged();
            Object oldSelection = getElementAtSafely(oldSelectionIndex);
            Object newSelection = getElementAtSafely(newSelectionIndex);
            oldSelectionIndex = newSelectionIndex;
            if (selectionHolder == null || ObjectUtils.nullSafeEquals(oldSelection, newSelection)) {
                return;
            }
            updateSelectionHolderSilently(newSelection);
            firePropertyChange(SELECTION_PROPERTY, oldSelection, newSelection);
        }

        private void updateSelectionHolderSilently(Object newSelection) {
            selectionHolder.removePropertyChangeListener(selectionChangeHandler);
            selectionHolder.setValue(newSelection);
            selectionHolder.addPropertyChangeListener(selectionChangeHandler);
        }
    }

    /*
     * Handles ListDataEvents in the list model.
     */
    private class ListDataChangeHandler implements ListDataListener {
        public void intervalAdded(ListDataEvent evt) {
            int index0 = evt.getIndex0();
            int index1 = evt.getIndex1();
            int index = getSelectionIndex();
            fireIntervalAdded(index0, index1);
            if (index < index0) {
                // The added elements are after the index; do nothing.
            }
            else {
                setSelectionIndex(index + (index1 - index0 + 1));
            }
        }

        public void intervalRemoved(ListDataEvent evt) {
            int index0 = evt.getIndex0();
            int index1 = evt.getIndex1();
            int index = getSelectionIndex();
            fireIntervalRemoved(index0, index1);
            if (index < index0) {
                // The removed elements are after the index; do nothing.
            }
            else if (index <= index1) {
                setSelectionIndex(EMPTY_SELECTION_INDEX);
            }
            else {
                setSelectionIndex(index - (index1 - index0 + 1));
            }
        }

        public void contentsChanged(ListDataEvent evt) {
            fireContentsChanged(evt.getIndex0(), evt.getIndex1());
            updateSelectionContentsChanged(evt.getIndex0(), evt.getIndex1());
        }

        private void updateSelectionContentsChanged(int first, int last) {
            if (first < 0) {
                return;
            }
            int selectionIndex = getSelectionIndex();
            if (first <= selectionIndex && (selectionIndex <= last)) {
                // need to synch directly on the holder because the
                // usual methods for setting selection/-index check for
                // equality
                getSelectionHolder().setValue(getElementAt(selectionIndex));
            }
        }
    }
}