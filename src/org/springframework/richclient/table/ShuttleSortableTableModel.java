/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * Copyright Computer Science Innovations (CSI), 2003. All rights reserved.
 */
package org.springframework.richclient.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.springframework.util.Assert;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel)
 * and itself implements TableModel. TableSorter does not store or copy the
 * model in the TableModel, instead it maintains an array of integers which it
 * keeps the same size as the number of rows in its model. When the model
 * changes it notifies the sorter that something has changed eg. "rowsAdded" so
 * that its internal array of integers can be reallocated. As requests are made
 * of the sorter (like getValueAt(row, col) it redirects them to its model via
 * the mapping array. That way the TableSorter appears to hold another copy of
 * the table with the rows in a different order. The sorting algorthm used is
 * stable which means that it does not move around rows when its comparison
 * function returns 0 to denote that they are equivalent.
 */
public class ShuttleSortableTableModel
    extends AbstractTableModelFilter
    implements SortableTableModel {
    public static final Comparator OBJECT_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            String s1 = o1.toString();
            String s2 = o2.toString();
            return s1.compareTo(s2);
        }
    };
    private Comparator[] columnComparators;
    private List columnsToSort = new ArrayList(4);
    private int[] indexes;
    private int[] selectedRows = new int[0];
    private int compares;
    private boolean autoSortEnabled = true;

    private Runnable notifyTableRunnable = new Runnable() {
        public void run() {
            fireTableDataChanged();
        }
    };

    public ShuttleSortableTableModel(TableModel model) {
        super(model);
        allocateIndexes();
        initComparators();
    }

    private void allocateIndexes() {
        int rowCount = model.getRowCount();
        // Set up a new array of indexes with the right number of elements
        // for the new model model.
        indexes = new int[rowCount];
        // Initialise with the identity mapping.
        for (int row = 0; row < rowCount; row++) {
            indexes[row] = row;
        }
    }

    private void initComparators() {
        int colCount = model.getColumnCount();
        columnComparators = new Comparator[colCount];
        for (int i = 0; i < model.getColumnCount(); i++) {
            Class clazz = model.getColumnClass(i);
            if (clazz == Object.class
                || !Comparable.class.isAssignableFrom(clazz)) {
                columnComparators[i] = OBJECT_COMPARATOR;
            }
        }
    }

    public boolean isAutoSortEnabled() {
        return autoSortEnabled;
    }

    public void setAutoSortEnabled(boolean autoSortEnabled) {
        this.autoSortEnabled = autoSortEnabled;
    }

    // The mapping only affects the contents of the model rows.
    // Pass all requests to these rows through the mapping array: "indexes".
    public Object getValueAt(int row, int column) {
        return model.getValueAt(indexes[row], column);
    }

    public void setValueAt(Object value, int row, int column) {
        model.setValueAt(value, indexes[row], column);
    }

    public void sortByColumn(ColumnToSort columnToSort) {
        columnsToSort.clear();
        columnsToSort.add(columnToSort);
        sort();
        SwingUtilities.invokeLater(notifyTableRunnable);
    }

    public void sortByColumns(ColumnToSort[] columnsToSort) {
        this.columnsToSort = Arrays.asList(columnsToSort);
        sort();
        SwingUtilities.invokeLater(notifyTableRunnable);
    }

    public void sortByColumns(
        ColumnToSort[] columnsToSort,
        int[] presortSelectedRows) {
        int[] modelIndexes = new int[presortSelectedRows.length];
        for (int i = 0; i < presortSelectedRows.length; i++) {
            modelIndexes[i] = convertSortedIndexToDataIndex(presortSelectedRows[i]);
        }
        this.columnsToSort = Arrays.asList(columnsToSort);
        sort();
        this.selectedRows = new int[modelIndexes.length];
        for (int i = 0; i < modelIndexes.length; i++) {
            this.selectedRows[i] = convertModelToRowIndex(modelIndexes[i]);
        }
        SwingUtilities.invokeLater(notifyTableRunnable);
    }

    public int convertSortedIndexToDataIndex(int index) {
        return indexes[index];
    }

    public int[] convertSortedIndexesToDataIndexes(int[] indexes) {
        int[] converted = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            converted[i] = convertSortedIndexToDataIndex(indexes[i]);
        }
        return converted;
    }

    /* this linear search is a bit slow -- we'll need to optimize later */
    public int convertModelToRowIndex(int index) {
        for (int i = 0; i < indexes.length; i++) {
            if (index == indexes[i]) {
                return i;
            }
        }
        return 0;
    }

    public int[] convertDataIndexesToSortedIndexes(int[] indexes) {
        int[] converted = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            converted[i] = convertModelToRowIndex(indexes[i]);
        }
        return converted;
    }
    
    public int[] getSelectedRows() {
        return selectedRows;
    }

    private void sort() {
        if (columnsToSort.size() > 0) {
            checkModel();
            compares = 0;
            shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
        }
    }

    private void checkModel() {
        if (indexes.length != model.getRowCount()) {
            throw new IllegalStateException("Sorter not informed of a change in model.");
        }
    }

    // This is a home-grown implementation which we have not had time
    // to research - it may perform poorly in some circumstances. It
    // requires twice the space of an in-place algorithm and makes
    // NlogN assigments shuttling the values between the two
    // arrays. The number of compares appears to vary between N-1 and
    // NlogN depending on the initial order but the main reason for
    // using it here is that, unlike qsort, it is stable.
    private void shuttlesort(int from[], int to[], int low, int high) {
        if (high - low < 2) {
            return;
        }
        int middle = (low + high) / 2;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);

        int p = low;
        int q = middle;

        /*
		 * This is an optional short-cut; at each recursive call, check to see
		 * if the elements in this subset are already ordered. If so, no
		 * further comparisons are needed; the sub-array can just be copied.
		 * The array must be copied rather than assigned otherwise sister calls
		 * in the recursion might get out of sinc. When the number of elements
		 * is three they are partitioned so that the first set, [low, mid), has
		 * one element and and the second, [mid, high), has two. We skip the
		 * optimisation when the number of elements is three or less as the
		 * first compare in the normal merge will produce the same sequence of
		 * steps. This optimisation seems to be worthwhile for partially
		 * ordered lists but some analysis is needed to find out how the
		 * performance drops to Nlog(N) as the initial
		 */
        if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
            for (int i = low; i < high; i++) {
                to[i] = from[i];
            }
            return;
        }

        // A normal merge.
        for (int i = low; i < high; i++) {
            if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
                to[i] = from[p++];
            } else {
                to[i] = from[q++];
            }
        }
    }

    public int compare(int row1, int row2) {
        compares++;
        for (int level = 0; level < columnsToSort.size(); level++) {
            ColumnToSort column = (ColumnToSort)columnsToSort.get(level);
            int result =
                compareRowsByColumn(row1, row2, column.getColumnIndex());
            if (result != 0) {
                return column.getSortOrder() == SortOrder.ASCENDING
                    ? result
                    : -result;
            }
        }
        return 0;
    }

    private int compareRowsByColumn(int row1, int row2, int column) {
        Object o1 = model.getValueAt(row1, column);
        Object o2 = model.getValueAt(row2, column);

        // If both values are null, return 0.
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            // Define null less than everything
            return -1;
        } else if (o2 == null) {
            return 1;
        }

        Comparator comparator = columnComparators[column];
        if (comparator != null) {
            int result = comparator.compare(o1, o2);
            if (result > 0) {
                return 1;
            } else if (result < 0) {
                return -1;
            } else {
                return 0;
            }
        }

        Comparable c1 = (Comparable)o1;
        Comparable c2 = (Comparable)o2;
        int result = c1.compareTo(c2);
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public Comparator getComparator(int columnIndex) {
        return this.columnComparators[columnIndex];
    }

    public void setComparator(int columnIndex, Comparator comparator) {
        Assert.notNull(comparator);
        this.columnComparators[columnIndex] = comparator;
    }

    public void tableChanged(final TableModelEvent e) {
        if (e.getType() == TableModelEvent.INSERT) {
            if (autoSortEnabled) {
                reallocateIndexesOnInsert(e.getFirstRow(), e.getLastRow());
                sort();
                final int[] insertedRows =
                    new int[e.getLastRow() - e.getFirstRow() + 1];
                int row = e.getFirstRow();
                for (int i = 0; i < insertedRows.length; i++) {
                    insertedRows[i] = convertModelToRowIndex(row++);
                }
                for (int i = 0; i < insertedRows.length; i++) {
                    fireTableRowsInserted(insertedRows[i], insertedRows[i]);
                }
            } else {
                reallocateIndexesOnInsert(e.getFirstRow(), e.getLastRow());
                super.tableChanged(e);
            }
        } else if (e.getType() == TableModelEvent.DELETE) {
            final int[] deletedRows =
                new int[e.getLastRow() - e.getFirstRow() + 1];
            int row = e.getFirstRow();
            for (int i = 0; i < deletedRows.length; i++) {
                deletedRows[i] = convertModelToRowIndex(row);
                row++;
            }
            allocateIndexes();
            for (int i = 0; i < deletedRows.length; i++) {
                fireTableRowsDeleted(deletedRows[i], deletedRows[i]);
            }
            sort();
        } else {
            allocateIndexes();
            sort();
            super.tableChanged(e);
        }
    }

    private void reallocateIndexesOnInsert(int firstRow, int lastRow) {
        int rowCount = model.getRowCount();
        int[] newIndexes = new int[rowCount];
        for (int row = 0; row < indexes.length; row++) {
            newIndexes[row] = indexes[row];
        }
        for (int row = firstRow; row <= lastRow; row++) {
            newIndexes[row] = row;
        }
        indexes = newIndexes;
    }

}
