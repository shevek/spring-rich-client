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
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.binding.value.support.ObservableList;
import org.springframework.richclient.util.Assert;

/**
 * A skeleton {@link TableModel} implementation that adds to the {@link AbstractTableModel} class
 * from the core Java API by providing the functionality to manage the underlying collection 
 * of data for the table.
 * 
 * @author Keith Donald
 */
public abstract class BaseTableModel extends AbstractTableModel implements MutableTableModel {
    
    private Class[] columnClasses;

    /** The names for the column headers. Must never be null. */
    private String[] columnNames;

    /** 
     * The collection of objects that represent the rows of the table. 
     * Class invariant; this must never be null. 
     */
    private List rows;

    private boolean rowNumbers = true;

    /**
     * Creates a new uninitialized {@code BaseTableModel}.
     */
    public BaseTableModel() {
        this.rows = new ArrayList();
    }

    /**
     * Creates a new {@code BaseTableModel} containing the given collection of rows.
     *
     * @param rows The rows of the table model. May be null or empty.
     */
    public BaseTableModel(List rows) {
        if (rows == null) {
            this.rows = new ArrayList();
        }
        else {
            this.rows =  rows;
        }
        
    }

    /**
     * Overwrites the existing table rows with the given collection and fires an appropriate event
     * to all registered listeners.
     *
     * @param rows The collection of rows that will overwrite the existing collection. May be 
     * null or empty.
     */
    public void setRows(List rows) {
        // first check null, if somehow field was null it may not return (in second if)
        if (rows == null) {
            this.rows = new ArrayList();
        }
        if (this.rows == rows) {
            return;
        }
        this.rows = rows;
        fireTableDataChanged();
    }

    /**
     * Sets the flag that indicates whether or not row numbers are to appear in the first column
     * of the displayed table.
     *
     * @param rowNumbers The flag to display row numbers in the first column.
     */
    public void setRowNumbers(boolean rowNumbers) {
        if (this.rowNumbers != rowNumbers) {
            this.rowNumbers = rowNumbers;
            // modify tableModel to add/remove rowNo-Column
            createColumnInfo();
        }
    }

    /**
     * Returns true if row numbers are to appear in the first column of the displayed table (default
     * is true).
     *
     * @return The flag to show row numbers in the first column of the displayed table.
     */
    public boolean hasRowNumbers() {
        return rowNumbers;
    }

    /**
     * Creates the required column information based on the value provided by the 
     * {@link #createColumnClasses()} and {@link #createColumnNames()} methods.
     */
    protected void createColumnInfo() {
    
        Class[] newColumnClasses = createColumnClasses();
        String[] newColumnNames = createColumnNames();
        
        if (rowNumbers) {
            // modify columns to add rowNo as first column
            this.columnClasses = new Class[newColumnClasses.length + 1];
            this.columnClasses[0] = Integer.class;
            System.arraycopy(newColumnClasses, 0, this.columnClasses, 1, newColumnClasses.length);

            this.columnNames = new String[newColumnNames.length + 1];
            this.columnNames[0] = " ";
            System.arraycopy(newColumnNames, 0, this.columnNames, 1, newColumnNames.length);
        }
        else {
            // take columns as they are
            this.columnClasses = newColumnClasses;
            this.columnNames = newColumnNames;
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the number of columns, excluding the column that displays row numbers if present.
     *
     * @return The number of columns, not counting the row number column if present.
     */
    public int getDataColumnCount() {
        return rowNumbers ? columnNames.length - 1 : getColumnCount();
    }

    /**
     * Returns the type of the object to be displayed in the given column.
     * 
     * @param columnIndex The zero-based index of the column whose type will be returned.
     * 
     * @throws IndexOutOfBoundsException if {@code columnIndex} is not within the bounds of the
     * column range for a row of the table.
     */
    public Class getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    /**
     * Returns the name to be displayed in the header for the column at the given position.
     * 
     * @param columnIndex The zero-based index of the column whose name will be returned.
     * 
     * @throws IndexOutOfBoundsException if {@code columnInde} is not within the bounds of the 
     * column range for a row of the table.
     */
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Returns the array of column headers.
     *
     * @return The array of column headers, never null.
     */
    public String[] getColumnHeaders() {
        return columnNames;
    }

    /**
     * Returns the array of column headers other than the column displaying the row numbers,
     * if present.
     *
     * @return The column headers, not including the row number column. Never null.
     */
    public String[] getDataColumnHeaders() {
        
        String[] headers = getColumnHeaders();
        
        if (!hasRowNumbers()) {
            return headers;
        }

        String[] dataHeaders = new String[headers.length - 1];
        System.arraycopy(headers, 1, dataHeaders, 0, headers.length - 1);
        return dataHeaders;
        
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowNumbers) {
            if (columnIndex == 0) {
                return new Integer(rowIndex + 1);
            }
            columnIndex--;
        }
        return getValueAtInternal(rows.get(rowIndex), columnIndex);
    }

    /**
     * Subclasses must implement this method to return the value at the given column index for 
     * the given object.
     *
     * @param row The object representing a row of data from the table.
     * @param columnIndex The column index of the value to be returned.
     * @return The value at the given index for the given object. May be null.
     */
    protected abstract Object getValueAtInternal(Object row, int columnIndex);

    /**
     * {@inheritDoc}
     */
    //FIXME this method should probably become a template method by making it final
    public boolean isCellEditable(int rowIndex, int columnIndex) {
       
        if (rowNumbers) {
            if (columnIndex == 0) {
                return false;
            }
            columnIndex--;
        }
        
        return isCellEditableInternal(rows.get(rowIndex), columnIndex);
        
    }

    /**
     * Subclasses may override this method to determine if the cell at the specified row and 
     * column position can be edited. Default behaviour is to always return false.
     *
     * @param row The object representing the table row.
     * @param columnIndex The zero-based index of the column to be checked.
     * @return true if the given cell is editable, false otherwise.
     */
    protected boolean isCellEditableInternal(Object row, int columnIndex) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowNumbers) {
            columnIndex--;
        }
        setValueAtInternal(value, rows.get(rowIndex), columnIndex);
        if (getRows() instanceof ObservableList) {
            ((ObservableList) getRows()).getIndexAdapter(rowIndex).fireIndexedObjectChanged();
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Subclasses may implement this method to set the given value on the property at the given 
     * column index of the given row object. The default implementation is to do nothing. 
     *
     * @param value The value to be set.
     * @param row The object representing the row in the table.
     * @param columnIndex The column position of the property on the given row object.
     */
    protected void setValueAtInternal(Object value, Object row, int columnIndex) {
        //do nothing
    }

    /**
     * Returns the object representing the row at the given zero-based index.
     *
     * @param rowIndex The zero-based index of the row whose object should be returned.
     * @return The object for the given row.
     * 
     * @throws IndexOutOfBoundsException if {@code rowIndex} is not within the bounds of the 
     * collection of rows for this table model.
     */
    public Object getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    /**
     * Returns the collection of all the rows in this table model.
     *
     * @return The collection rows. The collection may be empty but will never be null.
     */
    public List getRows() {
        return rows;
    }

    /**
     * Returns the collection of data from the given column of each row in the table model.
     *
     * @param column The zero-based index of the column whose data should be returned.
     * @return The collection of data from the given column.
     * 
     * @throws IndexOutOfBoundsException if the given column is not within the bounds of the 
     * number of columns for the rows in this table model.
     */
    public List getColumnData(int column) {
        
        int columnCount = getColumnCount();
        
        if (column < 0 || column >= columnCount) {
            throw new IndexOutOfBoundsException("The given column index ["
                                                + column
                                                + "] is outside the bounds of the number of columns ["
                                                + columnCount
                                                + "] in the rows of this table model.");
        }
        
        if (columnCount == 1) {
            return rows;
        }

        List colData = new ArrayList(getRowCount());
        
        for (int i = 0; i < getRowCount(); i++) {
            colData.add(getValueAt(i, column));
        }
        
        return colData;
        
    }

    /**
     *  Returns the index of the first row containing the specified element, or -1 if this table 
     *  model does not contain this element.
     *
     * @param obj The object whose row number will be returned.
     * @return The index of the first row containing the given object, or -1 if the table model 
     * does not contain the object.
     */
    public int rowOf(Object obj) {
        
        if (obj == null) {
            return -1;
        }
        
        return rows.indexOf(obj);
        
    }

    /**
     * {@inheritDoc}
     */
    public void addRow(Object row) {
        
        Assert.required(row, "row");
        
        this.rows.add(row);
        int index = this.rows.size() - 1;
        fireTableRowsInserted(index, index);
    }

    /**
     * {@inheritDoc}
     */
    public void addRows(List newRows) {
        Assert.required(newRows, "newRows");
        
        if (newRows.isEmpty()) {
            return;
        }
        
        int firstRow = this.rows.size();
        this.rows.addAll(newRows);
        int lastRow = this.rows.size() - 1;
        fireTableRowsInserted(firstRow, lastRow);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(int index) {
        this.rows.remove(index);
        fireTableRowsDeleted(index, index);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(int firstIndex, int lastIndex) {
        
        if (lastIndex < firstIndex) {
            throw new IllegalArgumentException("lastIndex ["
                                               + lastIndex
                                               + "] cannot be less than firstIndex ["
                                               + firstIndex
                                               + "]");
        }
        
        if (firstIndex < 0 || firstIndex >= this.rows.size()) {
            throw new IndexOutOfBoundsException("The specified starting index ["
                                                + firstIndex
                                                + "] is outside the bounds of the rows collection "
                                                + "which only has ["
                                                + this.rows.size()
                                                + "] elements.");
        }
        
        if (lastIndex >= this.rows.size()) {
            throw new IndexOutOfBoundsException("The specified end index ["
                                                + lastIndex
                                                + "] is outside the bounds of the rows collection "
                                                + "which only has ["
                                                + this.rows.size()
                                                + "] elements.");
        }
        
        int rowCount = lastIndex - firstIndex + 1;
        
        for (int i = 0; i < rowCount; i++) {
            rows.remove(firstIndex);
        }
        
        fireTableRowsDeleted(firstIndex, lastIndex);
        
    }

    /**
     * {@inheritDoc}
     */
    public void remove(int[] indexes) {
        
        Assert.required(indexes, "indexes");
        
        if (indexes.length == 0) {
            return;
        }

        // must sort the indexes first!!!
        Arrays.sort(indexes);
        
        if (indexes[0] < 0 || indexes[0] >= this.rows.size()) {
            throw new IndexOutOfBoundsException("The specified index ["
                                                + indexes[0]
                                                + "] is outside the bounds of the rows collection "
                                                + "which only has ["
                                                + this.rows.size()
                                                + "] elements.");
        }
        
        if ((indexes[indexes.length -1]) >= this.rows.size()) {
            throw new IndexOutOfBoundsException("The specified end index ["
                                                + indexes[indexes.length -1]
                                                + "] is outside the bounds of the rows collection "
                                                + "which only has ["
                                                + this.rows.size()
                                                + "] elements.");
        }
        
        int firstIndex = indexes[0];
        int lastIndex = indexes[0];
        int i = 0;
        int shift = 0;
        // this is kind of complicated - only removes contiguous selection
        // intervals to minimize number of events published to GUI.
        while (i < indexes.length - 1) {
            if (indexes[i + 1] == (lastIndex + 1)) {
                lastIndex++;
            }
            else {
                remove(firstIndex - shift, lastIndex - shift);
                shift += lastIndex - firstIndex + 1;
                firstIndex = indexes[i + 1];
                lastIndex = indexes[i + 1];
            }
            i++;
        }
        remove(firstIndex - shift, lastIndex - shift);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        this.rows.clear();
        fireTableDataChanged();
    }

    /**
     * Returns the array of class types for the columns displayed by this table model.
     *
     * @return The array of column class types, never null.
     */
    protected Class[] getColumnClasses() {
        return columnClasses;
    }

    /**
     * Returns the array of column headers for the columns displayed by this table model.
     *
     * @return The array of column headers, never null.
     */
    protected String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Subclasses must implement this method to return the array of class types for the columns 
     * displayed by this table model.
     *
     * @return The array of column class types, never null.
     */
    protected abstract Class[] createColumnClasses();

    /**
     * Subclasses must implement this method to return the array of column headers for the 
     * columns to be displayed by this table model.
     * 
     * @return The array of column headers, never null.
     */
    protected abstract String[] createColumnNames();

}
