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

import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.StringUtils;

public class TableSettings {

    private TableSettings() {
        // no instantiation, static utility class
    }

    public static void saveState(Settings s, String key, JTable table) {
        saveSelectedRows(s, key, table);
        saveColumnOrder(s, key, table);
    }

    public static void saveColumnWidths(Settings s, String key, JTable table) {
        StringBuffer sb = new StringBuffer();
        int columnCount = table.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            sb.append(table.getColumnModel().getColumn(i).getWidth());
            if (i < columnCount - 1) {
                sb.append(",");
            }
        }
        s.setString(key + ".columnWidths", sb.toString());
    }

    public static void restoreColumnWidths(Settings s, String key, JTable table) {
        table.getSelectionModel().clearSelection();
        String widthSetting = s.getString(key + ".columnWidths");
        if (StringUtils.hasText(widthSetting)) {

            String[] stringWidths = widthSetting.split(",");

            try {
                int[] widths = ArrayUtil.toIntArray(stringWidths);

                if (widths.length == table.getColumnCount()) {
                    for (int i = 0; i < widths.length; i++) {
                        table.getColumnModel().getColumn(i).setWidth(widths[i]);
                        table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
                    }
                }
            } catch (IllegalArgumentException e) {
                // TODO log this exception
            }
        }
    }

    public static void saveColumnOrder(Settings s, String key, JTable table) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            sb.append(column.getModelIndex());
            if (i < table.getColumnModel().getColumnCount() - 1) {
                sb.append(",");
            }
        }
        s.setString(key + ".columnOrder", sb.toString());
    }

    public static void saveSelectedRows(Settings s, String key, JTable table) {
        String settingsKey = key + ".selectedRows";
        if (s.contains(settingsKey)) {
            s.remove(settingsKey);
        }

        int[] selection = table.getSelectedRows();
        Arrays.sort(selection);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < selection.length) {
            sb.append(selection[i]);
            if (i < selection.length - 1) {
                if (selection[i] == selection[i + 1] - 1) {
                    while (i < selection.length - 1 && selection[i] == selection[i + 1] - 1) {
                        i++;
                    }
                    sb.append("-");
                    sb.append(selection[i]);
                }
                if (i < selection.length - 1) {
                    sb.append(",");
                }
            }
            i++;
        }
        if (sb.length() > 0) {
            s.setString(key + ".selectedRows", sb.toString());
        }
    }

    public static void restoreSelectedRows(Settings s, String key, JTable table) {
        table.getSelectionModel().clearSelection();
        String selection = s.getString(key + ".selectedRows");
        if (StringUtils.hasText(selection)) {
            String[] parts = selection.split(",");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].indexOf('-') >= 0) {
                    String[] tmp = parts[i].split("-");
                    table.addRowSelectionInterval(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[2]));
                } else {
                    int index = Integer.parseInt(parts[i]);
                    table.addRowSelectionInterval(index, index);
                }
            }
        }
    }

    public static void restoreColumnOrder(Settings s, String key, JTable table) {
        table.getSelectionModel().clearSelection();
        String orderSetting = s.getString(key + ".selectedRows");
        if (StringUtils.hasText(orderSetting)) {
            String[] stringColumns = orderSetting.split(",");

            try {
                int[] columns = ArrayUtil.toIntArray(stringColumns);

                if (columns.length == table.getColumnCount()) {
                    for (int i = 0; i < columns.length; i++) {
                        table.moveColumn(getPosition(table, columns[i]), i);
                    }
                }
            } catch (IllegalArgumentException e) {
                // TODO log this
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

    public static void restoreState(Settings s, String key, JTable table) {
        restoreSelectedRows(s, key, table);
        restoreColumnOrder(s, key, table);
    }
}