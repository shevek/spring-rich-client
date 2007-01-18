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

import org.easymock.EasyMock;


/**
 * TODO finish comment
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public abstract class AbstractBaseTableModelTests extends AbstractMutableTableModelTests {
    
    /**
     * {@inheritDoc}
     */
    protected MutableTableModel getTableModel() {
        return getBaseTableModel();
    }

    /**
     * Subclasses must implement this method to return the implementation to be tested.
     *
     * @return The table model implementation to be tested. Never null.
     */
    protected abstract BaseTableModel getBaseTableModel();

    /**
     * Creates a new uninitialized {@code AbstractBaseTableModelTests}.
     */
    public AbstractBaseTableModelTests() {
        super();
    }

    /**
     * Test method for {@link BaseTableModel#setRows(java.util.List)}.
     */
    public final void testSetRows() {
        
        BaseTableModel model = getBaseTableModel();
        
        List rows = new ArrayList();
        rows.add(new Object());
        rows.add(new Object());
        
        //create the mock listeners and add them to the model
        TableModelListener listener1 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        TableModelListener listener2 = (TableModelListener) EasyMock.createMock(TableModelListener.class);
        model.addTableModelListener(listener1);
        model.addTableModelListener(listener2);
        
        //set the expectations on the mock listeners
        TableModelEvent expectedEvent = new TableModelEvent(model);
        listener1.tableChanged(matchEvent(expectedEvent));
        listener2.tableChanged(matchEvent(expectedEvent));
        
        //switch the mocks to replay mode
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        //...and execute the test
        model.setRows(rows);
        
        Assert.assertEquals(2, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);
        
        //Create a new list of rows and confirm that it overwrites the existing rows
        List rows2 = new ArrayList(3);
        rows2.add(new Object());
        rows2.add(new Object());
        rows2.add(new Object());
        
        //reset the mocks
        EasyMock.reset(listener1);
        EasyMock.reset(listener2);
        
        //set the expectations on the mock listeners
        listener1.tableChanged(matchEvent(expectedEvent));
        listener2.tableChanged(matchEvent(expectedEvent));
        
        //switch the mocks to replay mode
        EasyMock.replay(listener1);
        EasyMock.replay(listener2);
        
        //...and execute the test
        model.setRows(rows2);
        
        Assert.assertEquals(3, model.getRowCount());
        EasyMock.verify(listener1);
        EasyMock.verify(listener2);

       
    }

    /**
     * Test method for {@link org.springframework.richclient.table.BaseTableModel#hasRowNumbers()}.
     */
    public final void testRowNumbersFlag() {
        
        BaseTableModel model = getBaseTableModel();
        
        Assert.assertTrue("Assert default rowNumbers flag is true", model.hasRowNumbers());
        
        model.setRowNumbers(false);
        
        Assert.assertFalse("Assert rowNumbers flag is false", model.hasRowNumbers());
        
    }

    /**
     * Test method for {@link org.springframework.richclient.table.BaseTableModel#getRow(int)}.
     */
    public final void testGetRow() {
        
        BaseTableModel model = getBaseTableModel();
        
        Object row1 = new Object();
        Object row2 = new Object();
        Object row3 = new Object();
        
        List rows = new ArrayList(3);
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        
        model.setRows(rows);
        
        Assert.assertEquals(row1, model.getRow(0));
        Assert.assertEquals(row2, model.getRow(1));
        Assert.assertEquals(row3, model.getRow(2));
        
        try {
            model.getRow(-1);
            Assert.fail("Should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            //test passes
        }
        
        try {
            model.getRow(3);
            Assert.fail("Should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            //test passes
        }
        
    }

    /**
     * Test method for {@link BaseTableModel#getRows()}.
     */
    public final void testGetRows() {
        
        BaseTableModel model = getBaseTableModel();
        
        Assert.assertNotNull("Assert model.getRows is not null", model.getRows());
        Assert.assertTrue("Assert model.getRows is an empty list", model.getRows().isEmpty());
        
        List rows = new ArrayList(3);
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
        model.setRows(rows);
        
        Assert.assertEquals(rows, model.getRows());
        
    }

    /**
     * Test method for {@link BaseTableModel#rowOf(java.lang.Object)}.
     */
    public final void testRowOf() {
        
        BaseTableModel model = getBaseTableModel();
        Object expectedRow = new Object();
        
        //confirm that -1 is returned if the model does not contain the element
        Assert.assertEquals(-1, model.rowOf(expectedRow));
        Assert.assertEquals(-1, model.rowOf(expectedRow));
        
        //create a list of rows with the expected row at the first and third positions, and add 
        //them to the model
        List rows = new ArrayList();
        rows.add(expectedRow);
        rows.add(new Object());
        rows.add(expectedRow);
        model.setRows(rows);
        
        //confirm that the expected row is at index 0
        Assert.assertEquals(0, model.rowOf(expectedRow));
        
    }

}
