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

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import org.springframework.util.Assert;

/**
 * An abstract implementation of the {@link javax.swing.table.TableModel}
 * interface that converts a {@link javax.swing.ListModel}of row elements.
 * <p>
 * 
 * API users subclass <code>TableAdapter</code> and just implement the method
 * <code>TableModel#getValueAt(int, int)</code>.
 * <p>
 * 
 * The following example implementation is based on a list of customer rows and
 * exposes the first and last name as well as the customer ages:
 * 
 * <pre>
 * public class CustomerTableModel extends TableAdapter {
 * 
 *     private static final String[] COLUMN_NAMES = { &quot;Last Name&quot;, &quot;First Name&quot;,
 *             &quot;Age&quot; };
 * 
 *     public CustomerTableModel(ListModel listModel) {
 *         super(listModel, COLUMN_NAMES);
 *     }
 * 
 *     public Object getValueAt(int rowIndex, int columnIndex) {
 *         Customer customer = (Customer)getRow(rowIndex);
 *         switch (columnIndex) {
 *         case 0:
 *             return customer.getLastName();
 *         case 1:
 *             return customer.getFirstName();
 *         case 2:
 *             return customer.getAge();
 *         default:
 *             return null;
 *         }
 *     }
 * 
 * }
 * </pre>
 * 
 * @author Karsten Lentzsch
 * @author Keith Donald
 * 
 * @see javax.swing.ListModel
 * @see javax.swing.JTable
 */
public abstract class AbstractTableModelAdapter extends AbstractTableModel {

    /**
     * Refers to the <code>ListModel</code> that holds the table row elements
     * and reports changes in the structure and content. The elements of the
     * list model can be requested using <code>#getRow(int)</code>. A typical
     * subclass will use the elements to implement the <code>TableModel</code>
     * method <code>#getValueAt(int, int)</code>.
     * 
     * @see #getRow(int)
     * @see #getRowCount()
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    private final ListModel listModel;

    /**
     * Holds an optional array of column names that is used by the default
     * implementation of the <code>TableModel</code> methods
     * <code>#getColumnCount()</code> and <code>#getColumnName(int)</code>.
     * 
     * @see #getColumnCount()
     * @see #getColumnName(int)
     */
    private final String[] columnNames;

    /**
     * Constructs a <code>TableAdapter</code> on the given
     * <code>ListModel</code>.
     * 
     * @param listModel
     *            the <code>ListModel</code> that holds the row elements
     * @throws NullPointerException
     *             if the list model is <code>null</code>
     */
    public AbstractTableModelAdapter(ListModel listModel) {
        this(listModel, null);
    }

    /**
     * Constructs a <code>TableAdapter</code> on the given
     * <code>ListModel</code>.
     * 
     * @param listModel
     *            the <code>ListModel</code> that holds the row elements
     * @param columnNames
     *            an optional array of column names
     * @throws NullPointerException
     *             if the list model is <code>null</code>
     */
    public AbstractTableModelAdapter(ListModel listModel, String[] columnNames) {
        Assert.notNull(listModel, "The listModel property is required");
        this.listModel = listModel;
        this.columnNames = columnNames;
        listModel.addListDataListener(createChangeHandler());
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public final int getRowCount() {
        return listModel.getSize();
    }

    protected final Object getRow(int index) {
        return listModel.getElementAt(index);
    }

    protected ListDataListener createChangeHandler() {
        return new ListDataChangeHandler();
    }

    /*
     * Listens to subject changes and fires a contents change event.
     */
    private class ListDataChangeHandler implements ListDataListener {
        public void intervalAdded(ListDataEvent evt) {
            fireTableRowsInserted(evt.getIndex0(), evt.getIndex1());
        }

        public void intervalRemoved(ListDataEvent evt) {
            fireTableRowsDeleted(evt.getIndex0(), evt.getIndex1());
        }

        public void contentsChanged(ListDataEvent evt) {
            int firstRow = evt.getIndex0();
            int lastRow = evt.getIndex1();
            fireTableRowsUpdated(firstRow, lastRow);
        }

    }

}