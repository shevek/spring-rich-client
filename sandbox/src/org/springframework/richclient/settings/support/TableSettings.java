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
package org.springframework.richclient.settings.support;

import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * Helper class for saving various <code>JTable</code> settings.
 * 
 * @author Peter De Bruycker
 */
public class TableSettings {

    private static final Log logger = LogFactory.getLog(TableSettings.class);

    private static final String COLUMN_WIDTHS = "columnWidths";

    private static final String COLUMN_ORDER = "columnOrder";

    private static final String SELECTED_ROWS = "selectedRows";

    private static final String ANCHOR = "anchor";

    private static final String LEAD = "lead";

    private TableSettings() {
        // no instantiation, static utility class
    }

    private static void assertArgumentsOk(Settings s, String key, JTable table) {
        Assert.notNull(s, "Settings cannot be null.");
        Assert.notNull(table, "Table cannot be null.");
        Assert.hasText(key, "Key must have text.");
    }

    /**
     * Saves the state of the given table. Uses the name of the table as key.
     * 
     * @param s
     *            the settings
     * @param table
     *            the table
     */
    public static void saveState(Settings s, JTable table) {
        Assert.hasText(table.getName(), "Name attribute of table must be filled in.");

        saveState(s, table.getName(), table);
    }

    /**
     * Saves the state of the given table with the given key.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void saveState(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        saveSelectedRows(s, key, table);
        saveColumnOrder(s, key, table);
        saveColumnWidths(s, key, table);
    }

    /**
     * Save the column widths of the given table in the settings with the key.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void saveColumnWidths(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        StringBuffer sb = new StringBuffer();
        int columnCount = table.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            sb.append(table.getColumnModel().getColumn(i).getWidth());
            if (i < columnCount - 1) {
                sb.append(",");
            }
        }
        s.setString(key + "." + COLUMN_WIDTHS, sb.toString());
    }

    /**
     * Restores the column widths.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void restoreColumnWidths(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        table.getSelectionModel().clearSelection();
        String widthSetting = s.getString(key + "." + COLUMN_WIDTHS);
        if (StringUtils.hasText(widthSetting)) {

            String[] stringWidths = widthSetting.split(",");

            try {
                int[] widths = ArrayUtil.toIntArray(stringWidths);

                if (widths.length == table.getColumnCount()) {
                    for (int i = 0; i < widths.length; i++) {
                        table.getColumnModel().getColumn(i).setWidth(widths[i]);
                        table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
                    }
                } else {
                    logger.warn("Unable to restore column widths, table has " + table.getColumnCount() + " columns, "
                            + widths.length + " columns stored in settings");
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to restore column widths", e);
            }
        }
    }

    /**
     * Saves the column order.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void saveColumnOrder(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            sb.append(column.getModelIndex());
            if (i < table.getColumnModel().getColumnCount() - 1) {
                sb.append(",");
            }
        }
        s.setString(key + "." + COLUMN_ORDER, sb.toString());
    }

    /**
     * Saves the row selection, the anchor and lead selections.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void saveSelectedRows(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        String settingsKey = key + "." + SELECTED_ROWS;
        if (s.contains(settingsKey)) {
            s.remove(settingsKey);
        }

        if (table.getSelectedRowCount() > 0) {
            s.setInt(key + "." + ANCHOR, table.getSelectionModel().getAnchorSelectionIndex());
            s.setInt(key + "." + LEAD, table.getSelectionModel().getLeadSelectionIndex());
        }

        String selectionString = ArrayUtil.asIntervalString(table.getSelectedRows());
        if (selectionString.length() > 0) {
            s.setString(settingsKey, selectionString);
        }
    }

    /**
     * Restores the row selection, the anchor and lead selections.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void restoreSelectedRows(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        table.getSelectionModel().clearSelection();
        if (s.contains(key + "." + SELECTED_ROWS)) {
            String selection = s.getString(key + "." + SELECTED_ROWS);
            if (StringUtils.hasText(selection)) {
                String[] parts = selection.split(",");

                // find max row, so we can check before restoring row selections
                String lastPart = parts[parts.length - 1];
                int maxRow = -1;
                if (lastPart.indexOf('-') >= 0) {
                    maxRow = Integer.parseInt(lastPart.substring(lastPart.indexOf('-')));
                } else {
                    maxRow = Integer.parseInt(lastPart);
                }
                if (maxRow <= table.getRowCount() - 1) {
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].indexOf('-') >= 0) {
                            String[] tmp = parts[i].split("-");
                            table.addRowSelectionInterval(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
                        } else {
                            int index = Integer.parseInt(parts[i]);
                            table.addRowSelectionInterval(index, index);
                        }
                    }
                } else {
                    logger.warn("Unable to restore row selection, table has " + table.getRowCount()
                            + " rows, setting has max row " + maxRow);
                }
            }
        }

        if (s.contains(key + "." + ANCHOR)) {
            table.getSelectionModel().setAnchorSelectionIndex(s.getInt(key + "." + ANCHOR));
        }
        if (s.contains(key + "." + LEAD)) {
            table.getSelectionModel().setLeadSelectionIndex(s.getInt(key + "." + LEAD));
        }
    }

    /**
     * Restores the column order.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void restoreColumnOrder(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        table.getSelectionModel().clearSelection();
        String orderSetting = s.getString(key + "." + COLUMN_ORDER);
        if (StringUtils.hasText(orderSetting)) {
            String[] stringColumns = orderSetting.split(",");

            try {
                int[] columns = ArrayUtil.toIntArray(stringColumns);

                if (columns.length == table.getColumnCount()) {
                    for (int i = 0; i < columns.length; i++) {
                        table.moveColumn(getPosition(table, columns[i]), i);
                    }
                } else {
                    logger.warn("Unable to restore column order, table has " + table.getColumnCount() + " columns, "
                            + columns.length + " columns stored in settings");
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to restore column order.", e);
            }
        }
    }

    /**
     * Returns the position of the column for the given model index. The model
     * index remains constant, but the position changes as the columns are
     * moved.
     * 
     * @param table
     *            the table
     * @param modelIndex
     *            the modelIndex
     * @return the position
     */
    private static int getPosition(JTable table, int modelIndex) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            if (column.getModelIndex() == modelIndex) {
                return i;
            }
        }
        throw new IllegalArgumentException("No column with modelIndex " + modelIndex + " found");
    }

    /**
     * Restores the complete table.
     * 
     * @param s
     *            the settings
     * @param key
     *            the key
     * @param table
     *            the table
     */
    public static void restoreState(Settings s, String key, JTable table) {
        assertArgumentsOk(s, key, table);

        restoreColumnOrder(s, key, table);
        restoreColumnWidths(s, key, table);
        restoreSelectedRows(s, key, table);
    }

    /**
     * Restores the state of the given table. Uses the name of the table as key.
     * 
     * @param s
     *            the settings
     * @param table
     *            the table
     */
    public static void restoreState(Settings s, JTable table) {
        Assert.hasText(table.getName(), "Name attribute of table must be filled in.");

        restoreState(s, table.getName(), table);
    }

    /**
     * write sorting state as [index,comparatorIndex,reverse],...
     * 
     * @param s
     * @param key
     * @param tableComparatorChooser
     */
    public static void saveSortingState(Settings s, String key, TableComparatorChooser tableComparatorChooser) {
        List sortingColumns = tableComparatorChooser.getSortingColumns();
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = sortingColumns.iterator(); iter.hasNext();) {
            int column = ((Integer) iter.next()).intValue();
            String settings = "[" + column + "," + tableComparatorChooser.getColumnComparatorIndex(column) + ","
                    + Boolean.toString(tableComparatorChooser.isColumnReverse(column)) + "]";
            sb.append(settings);
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        System.out.println(sb);
    }

    public static void restoreSortingState(Settings s, JTable table, TableComparatorChooser chooser) {
        restoreSortingState(s, table.getName(), chooser);
    }

    public static void restoreSortingState(Settings s, String key, TableComparatorChooser chooser) {
        if (s.contains(key + "." + "sorting")) {
            String state = s.getString(key + "." + "sorting");
            if (StringUtils.hasText(state)) {
                state = state.substring(1, state.length() - 1);
                String[] colStates = state.split("\\],\\[");
                for (int i = 0; i < colStates.length; i++) {
                    String[] parts = colStates[i].split(",");
                    int column = Integer.parseInt(parts[0]);
                    int comparator = Integer.parseInt(parts[1]);
                    boolean reverse = Boolean.getBoolean(parts[2]);
                    chooser.chooseComparator(column, comparator, reverse);
                }
            }
        }
    }
}