/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.value.support;

import javax.swing.DefaultListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.event.ListEvent;
import org.springframework.binding.value.IndexAdapter;

/**
 * This class provides an implementation of {@link EventList} that also implements the
 * {@link ObservableList} interface so that it can be used by an AbstractForm as the list
 * of editable objects.
 * 
 * @author Larry Streepy
 * 
 */
public class ObservableEventList extends TransformedList implements ObservableList {

    private IndexAdapter indexAdapter;
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Creates a {@link ObservableEventList} on the specified source event list.
     */
    public ObservableEventList(EventList source) {
        super( source );

        // listen for changes to the source list
        source.addListEventListener(this);

    }

    /**
     * Gets whether the source {@link EventList} is writable via this API.
     * 
     * @return always true
     */
    protected boolean isWritable() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ca.odell.glazedlists.TransformedList#listChanged(ca.odell.glazedlists.event.ListEvent)
     */
    public void listChanged(ListEvent listChanges) {
        // forward the event to interested listeners
        updates.forwardEvent( listChanges );

        // And then fire model events based on these changes
        while( listChanges.nextBlock() ) {
            // get the current change info
            int startIndex = listChanges.getBlockStartIndex();
            int endIndex = listChanges.getBlockEndIndex();
            int changeType = listChanges.getType();

            // create a table model event for this block
            if( changeType == ListEvent.INSERT )
                fireIntervalAdded(this, startIndex, endIndex);
            else if( changeType == ListEvent.DELETE )
                fireIntervalRemoved(this, startIndex, endIndex);
            else if( changeType == ListEvent.UPDATE )
                fireContentsChanged(this, startIndex, endIndex);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.binding.value.support.ObservableList#getIndexAdapter(int)
     */
    public IndexAdapter getIndexAdapter(int index) {
        if( indexAdapter == null ) {
            indexAdapter = new ThisIndexAdapter();
        }
        indexAdapter.setIndex( index );
        return indexAdapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        return get( index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
        listenerList.add( ListDataListener.class, l );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener l) {
        listenerList.remove( ListDataListener.class, l );
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one
     * or more elements of the list change. The changed elements are specified by the
     * closed interval index0, index1 -- the endpoints are included. Note that index0 need
     * not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if( listeners[i] == ListDataListener.class ) {
                if( e == null ) {
                    e = new ListDataEvent( source, ListDataEvent.CONTENTS_CHANGED, index0, index1 );
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged( e );
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one
     * or more elements are added to the model. The new elements are specified by a closed
     * interval index0, index1 -- the enpoints are included. Note that index0 need not be
     * less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if( listeners[i] == ListDataListener.class ) {
                if( e == null ) {
                    e = new ListDataEvent( source, ListDataEvent.INTERVAL_ADDED, index0, index1 );
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded( e );
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method <b>after</b> one
     * or more elements are removed from the model. <code>index0</code> and
     * <code>index1</code> are the end points of the interval that's been removed. Note
     * that <code>index0</code> need not be less than or equal to <code>index1</code>.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the removed interval, including <code>index0</code>
     * @param index1 the other end of the removed interval, including <code>index1</code>
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if( listeners[i] == ListDataListener.class ) {
                if( e == null ) {
                    e = new ListDataEvent( source, ListDataEvent.INTERVAL_REMOVED, index0, index1 );
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved( e );
            }
        }
    }

    /**
     * Inner class to provide the index adapter for this list.
     */
    private class ThisIndexAdapter extends AbstractIndexAdapter {
        private static final int NULL_INDEX = -1;

        public Object getValue() {
            if( getIndex() == NULL_INDEX ) {
                return null;
            }
            return get( getIndex() );
        }

        public void setValue(Object value) {
            if( getIndex() == NULL_INDEX ) {
                throw new IllegalStateException( "Attempt to set value at null index; operation not allowed" );
            }

            // Propogate this operation onto our event list
            set( getIndex(), value );
        }

        public void fireIndexedObjectChanged() {
            // Not supported
        }
    }

}
