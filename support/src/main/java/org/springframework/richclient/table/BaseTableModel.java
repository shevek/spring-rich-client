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

/**
 * @author Keith Donald
 */
public abstract class BaseTableModel extends AbstractTableModel implements MutableTableModel {
    private Class[] columnClasses;

    private String[] columnNames;

    private List rows;

    private boolean rowNumbers = true;

    public BaseTableModel() {
        this(new ArrayList());
    }

    public BaseTableModel(List rows) {
        setRows(rows);
    }

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

    public void setRowNumbers(boolean rowNumbers) {
        if (this.rowNumbers != rowNumbers) {
            this.rowNumbers = rowNumbers;
            // modify tableModel to add/remove rowNo-Column
            createColumnInfo();
        }
    }

    public boolean hasRowNumbers() {
        return rowNumbers;
    }

    protected void createColumnInfo() {
        Class[] columnClasses = createColumnClasses();
        String[] columnNames = createColumnNames();
        if (rowNumbers) {
            // modify columns to add rowNo as first column
            this.columnClasses = new Class[columnClasses.length + 1];
            this.columnClasses[0] = Integer.class;
            System.arraycopy(columnClasses, 0, this.columnClasses, 1, columnClasses.length);

            this.columnNames = new String[columnNames.length + 1];
            this.columnNames[0] = " ";
            System.arraycopy(columnNames, 0, this.columnNames, 1, columnNames.length);
        }
        else {
            // take columns as they are
            this.columnClasses = columnClasses;
            this.columnNames = columnNames;
        }
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getDataColumnCount() {
        return rowNumbers ? columnNames.length - 1 : getColumnCount();
    }

    public Class getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public String[] getColumnHeaders() {
        return columnNames;
    }

    public String[] getDataColumnHeaders() {
        String[] headers = getColumnHeaders();
        if (!hasRowNumbers())
            return headers;

        String[] dataHeaders = new String[headers.length - 1];
        System.arraycopy(headers, 1, dataHeaders, 0, headers.length - 1);
        return dataHeaders;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowNumbers) {
            if (columnIndex == 0) {
                return new Integer(rowIndex + 1);
            }
            columnIndex--;
        }
        return getValueAtInternal(rows.get(rowIndex), columnIndex);
    }

    protected abstract Object getValueAtInternal(Object row, int columnIndex);

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowNumbers) {
            if (columnIndex == 0) {
                return false;
            }
            columnIndex--;
        }
        return isCellEditableInternal(rows.get(rowIndex), columnIndex);
    }

    protected boolean isCellEditableInternal(Object row, int columnIndex) {
        return false;
    }

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

    protected void setValueAtInternal(Object value, Object row, int columnIndex) {
    }

    public Object getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    public List getRows() {
        return rows;
    }

    public List getColumnData(int column) {
        if (getColumnCount() == 1)
            return rows;

        List colData = new ArrayList(getRowCount());
        for (int i = 0; i < getRowCount(); i++) {
            colData.add(getValueAt(i, column));
        }
        return colData;
    }

    public int rowOf(Object o) {
        return rows.indexOf(o);
    }

    public void addRow(Object row) {
        this.rows.add(row);
        int index = this.rows.size() - 1;
        fireTableRowsInserted(index, index);
    }

    public void addRows(List rows) {
        if (rows == null) {
            throw new NullPointerException();
        }
        int firstRow = this.rows.size();
        this.rows.addAll(rows);
        int lastRow = this.rows.size() - 1;
        fireTableRowsInserted(firstRow, lastRow);
    }

    public void remove(int index) {
        this.rows.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public void remove(int firstIndex, int lastIndex) {
        int rowCount = lastIndex - firstIndex + 1;
        for (int i = 0; i < rowCount; i++) {
            rows.remove(firstIndex);
        }
        fireTableRowsDeleted(firstIndex, lastIndex);
    }

    // must sort the indexes first!!!
    public void remove(int[] indexes) {
        Arrays.sort(indexes);
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

    public void clear() {
        this.rows.clear();
        fireTableDataChanged();
    }

    protected Class[] getColumnClasses() {
        return columnClasses;
    }

    protected String[] getColumnNames() {
        return columnNames;
    }

    protected abstract Class[] createColumnClasses();

    protected abstract String[] createColumnNames();

}