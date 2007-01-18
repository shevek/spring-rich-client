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
package org.springframework.richclient.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.springframework.util.ObjectUtils;


/**
 * A skeleton test case for implementations of the {@link MutableTableModel} interface.  
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public abstract class AbstractMutableTableModelTests extends TestCase {

    /**
     * Creates a new uninitialized {@code AbstractMutableTableModelTests}.
     */
    public AbstractMutableTableModelTests() {
        super();
    }
    
    /**
     * Subclasses must implement this method to provide the {@link MutableTableModel} implementation
     * to be tested. This method may be called often, so unnecessary repeated initialization 
     * should be avoided.
     *
     * @return The implementation to be tested. Never null.
     */
    protected abstract MutableTableModel getTableModel();
    
    /**
     * Tests the {@link MutableTableModel#addRow(Object)} method.
     */
    public final void testAddRow() {
        
        MutableTableModel model = getTableModel();
        Object row = new Object();
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
        
        try {
            model.addRow(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        TableModelEvent event = new TableModelEvent(model, 
                                                    0, 
                                                    0, 
                                                    TableModelEvent.ALL_COLUMNS, 
                                                    TableModelEvent.INSERT);
        listener1.tableChanged(matchEvent(event));
        listener2.tableChanged(matchEvent(event));
        
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        Assert.assertEquals(0, model.getRowCount());
        model.addRow(row);
        Assert.assertEquals(1, model.getRowCount());
        
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
        
    }
    
    /**
     * Tests the {@link MutableTableModel#addRows(java.util.List)} method.
     */
    public final void testAddRows() {
        
        MutableTableModel model = getTableModel();
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
       
        try {
            model.addRows(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        //Passing an empty list of rows should have no effect on the model or listeners
        
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        Assert.assertEquals(0, model.getRowCount());
        model.addRows(new ArrayList());
        Assert.assertEquals(0, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
        
        //reset the mocks for the next test
        EasyMock.reset(listener1);
        EasyMock.reset(listener2);
        
        List rows = new ArrayList(2);
        rows.add(new Object());
        rows.add(new Object());

        TableModelEvent expectedEvent = new TableModelEvent(model, 
                                                            0, 
                                                            1, 
                                                            TableModelEvent.ALL_COLUMNS, 
                                                            TableModelEvent.INSERT);
        
        listener1.tableChanged(matchEvent(expectedEvent));
        listener2.tableChanged(matchEvent(expectedEvent));
        
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        Assert.assertEquals(0, model.getRowCount());
        model.addRows(rows);
        Assert.assertEquals(2, model.getRowCount());
        
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
        
    }

    /**
     * Tests the {@link MutableTableModel#remove(int)} method.
     */
    public final void testRemove() {
        
        MutableTableModel model = getTableModel();
        
        //Add 2 rows to the model
        List rows = new ArrayList(2);
        rows.add(new Object());
        rows.add(new Object());
        model.addRows(rows);
        
        //confirm that the 2 rows were added
        Assert.assertEquals(2, model.getRowCount());
        
        //confirm that an exception is thrown if the index is out of bounds
        try {
            model.remove(2);
            Assert.fail("Should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            //...and that the model hasn't been altered
            Assert.assertEquals(2, model.getRowCount());
        }
        
        //Create some mock listeners and add them to the model
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
        
        //For the next test, remove the first row and confirm that the model now contains only one row and that the 
        //listeners were notified correctly
        
        //Create the expected event
        TableModelEvent expectedEvent = new TableModelEvent(model,
                                                            0,
                                                            0,
                                                            TableModelEvent.ALL_COLUMNS,
                                                            TableModelEvent.DELETE);
        
        //set the expectations on the listeners and switch them to replay mode
        listener1.tableChanged(matchEvent(expectedEvent));
        listener2.tableChanged(matchEvent(expectedEvent));
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        //execute the test
        model.remove(0);
        
        //confirm that the model now only contains one row and that the listeners were correctly 
        //notified
        Assert.assertEquals(1, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
     
    }

    /**
     * Tests the {@link MutableTableModel#remove(int, int)} method.
     */
    public final void testRemoveRange() {
        
        MutableTableModel model = getTableModel();
        
        //Add 4 rows to the model
        List rows = new ArrayList(4);
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        model.addRows(rows);
        
        //confirm that the 4 rows were added
        Assert.assertEquals(4, model.getRowCount());
        
        //confirm that an exception is thrown if lastIndex is less than firstIndex
        try {
            model.remove(2, 1);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }   
        
        //confirm that an exception is thrown if the index is out of bounds
        try {
            model.remove(1, 4);
            Assert.fail("Should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            //...and that the model has not been altered
            Assert.assertEquals(4, model.getRowCount());
        }
        
        //Create some mock listeners and add them to the model
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
        
        //For the next test, remove the second and third rows and confirm that the model now 
        //contains only two rows and that the listeners were notified correctly
        
        //Create the expected event
        TableModelEvent expectedEvent = new TableModelEvent(model,
                                                            1,
                                                            2,
                                                            TableModelEvent.ALL_COLUMNS,
                                                            TableModelEvent.DELETE);
        
        //set the expectations on the listeners and switch them to replay mode
        listener1.tableChanged(matchEvent(expectedEvent));
        listener2.tableChanged(matchEvent(expectedEvent));
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        //execute the test
        model.remove(1, 2);
        
        //confirm that the model now only contains two rows and that the listeners were correctly 
        //notified
        Assert.assertEquals(2, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
     
    }

    /**
     * Tests the {@link MutableTableModel#remove(int[])} method.
     */
    public final void testRemoveIntArray() {
        
        MutableTableModel model = getTableModel();
        
        //Add 4 rows to the model
        List rows = new ArrayList(4);
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        model.addRows(rows);
        
        //confirm that the 4 rows were added
        Assert.assertEquals(4, model.getRowCount());
        
        //confirm that an exception is thrown if lastIndex is less than firstIndex
        try {
            model.remove((int[]) null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //test passes
        }
        
        //confirm that an exception is thrown if the index is out of bounds
        try {
            model.remove(new int[] {1, 4});
            Assert.fail("Should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            //...and that the model has not been altered
            Assert.assertEquals(4, model.getRowCount());
        }
        
        //Create some mock listeners and add them to the model
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
        
        // confirm that an empty array has no effect on the model
        
        //switch mocks to replay mode
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        //execute the test
        model.remove(new int[] {});
        
        //confirm that the model hasn't changed and that no listeners were invoked
        Assert.assertEquals(4, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
        
        
        //For the next test, remove the second and third rows and confirm that the model now 
        //contains only two rows
        
        //removing the listeners because I got no idea what's going on inside this method call
        model.removeTableModelListener(listener1);
        model.removeTableModelListener(listener2);
        
        //execute the test
        model.remove(new int[] {1, 3});
        
        //confirm that the model now only contains two rows
        Assert.assertEquals(2, model.getRowCount());
     
    }
    
    /**
     * Tests the {@link MutableTableModel#clear()} method.
     */
    public final void testClear() {
        
        MutableTableModel model = getTableModel();
        
        //Add 4 rows to the model
        List rows = new ArrayList(4);
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        model.addRows(rows);
        
        //confirm that the 4 rows were added
        Assert.assertEquals(4, model.getRowCount());
        
        //Create some mock listeners and add them to the model
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
        
        //Create the expected event
        TableModelEvent expectedEvent = new TableModelEvent(model);
        
        //set the expectations on the listeners and switch them to replay mode
        listener1.tableChanged(matchEvent(expectedEvent));
        listener2.tableChanged(matchEvent(expectedEvent));
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        //execute the test
        model.clear();
        
        //confirm that the model is now empty and that the listeners were correctly 
        //notified
        Assert.assertEquals(0, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
     
    }
    
    protected TableModelEvent matchEvent(TableModelEvent event) {
        EasyMock.reportMatcher(new TableModelEventMatcher(event));
        return event;
    }
    
    /**
     * An argument matcher for TableModelEvents.
     */
    protected static class TableModelEventMatcher implements IArgumentMatcher {
        
        private TableModelEvent expectedEvent;
        
        /**
         * Creates a new {@code TableModelEventMatcher}.
         *
         * @param expectedEvent
         */
        public TableModelEventMatcher(TableModelEvent expectedEvent) {
            this.expectedEvent = expectedEvent;
        }
        
        /**
         * {@inheritDoc}
         */
        public void appendTo(StringBuffer buffer) {
            buffer.append("(");
            buffer.append(this.expectedEvent.getClass().getName());
            buffer.append(" with source [");
            buffer.append(this.expectedEvent.getSource());
            buffer.append("])");
        }

        /**
         * {@inheritDoc}
         */
        public boolean matches(Object argument) {
            
            if (!(argument instanceof TableModelEvent)) {
                return false;
            }
            
            TableModelEvent other = (TableModelEvent) argument;
            
            if (!ObjectUtils.nullSafeEquals(expectedEvent.getSource(), other.getSource())) {
                return false;
            }
            
            if (expectedEvent.getFirstRow() != other.getFirstRow()) {
                return false;
            }
            
            if (expectedEvent.getLastRow() != other.getLastRow()) {
                return false;
            }
            
            if (expectedEvent.getColumn() != other.getColumn()) {
                return false;
            }
            
            if (expectedEvent.getType() != other.getType()) {
                return false;
            }
            
            return true;
            
        }
        
    }
    
}
