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
public abstract class ListModelTableModelAdapter extends AbstractTableModel {

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
    public ListModelTableModelAdapter(ListModel listModel) {
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
    public ListModelTableModelAdapter(ListModel listModel, String[] columnNames) {
        Assert.notNull(listModel);
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