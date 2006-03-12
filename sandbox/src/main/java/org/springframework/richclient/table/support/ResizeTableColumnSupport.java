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
package org.springframework.richclient.table.support;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Support for resizing of columns when double clicking in the resize area.
 * 
 * @author Peter De Bruycker
 */
public class ResizeTableColumnSupport implements TableCellRenderer, TableModelListener {

    private static final int PADDING = 3;

    private int[] preferredWidths;

    private List registeredRenderers = new ArrayList();

    private JTable table;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component comp = getRenderer(column).getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);
        preferredWidths[column] = Math.max(comp.getPreferredSize().width + ResizeTableColumnSupport.PADDING,
                preferredWidths[column]);
        return comp;
    }

    private void resetWidths() {
        preferredWidths = new int[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableCellRenderer headerRenderer = getColumn(i).getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            preferredWidths[i] = headerRenderer.getTableCellRendererComponent(table, table.getColumnName(i), false,
                    false, 0, i).getPreferredSize().width
                    + ResizeTableColumnSupport.PADDING * 2;
        }
    }

    private TableColumn getColumn(int column) {
        return table.getColumnModel().getColumn(column);
    }

    private TableCellRenderer getRenderer(int column) {
        TableCellRenderer renderer = (TableCellRenderer) registeredRenderers.get(column);
        if (renderer == null) {
            renderer = table.getDefaultRenderer(table.getColumnClass(column));
        }
        return renderer;
    }

    /**
     * Installs resize support on the given table.
     * 
     * @param table
     *            the table
     */
    public static void install(JTable table) {
        new ResizeTableColumnSupport(table);
    }

    private void initColumns() {
        registeredRenderers = new ArrayList(table.getColumnCount());
        for (int i = 0; i < table.getColumnCount(); i++) {
            registeredRenderers.add(getColumn(i).getCellRenderer());
            getColumn(i).setCellRenderer(this);
        }
    }

    private void initialize() {
        initColumns();
        resetWidths();
        table.getModel().addTableModelListener(this);
    }

    /**
     * Installs resize support on the given table
     * 
     * @param table
     *            the table
     */
    public ResizeTableColumnSupport(final JTable table) {
        // TODO assert table not null
        this.table = table;

        initialize();

        table.addPropertyChangeListener("model", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                TableModel oldModel = (TableModel) evt.getOldValue();
                if (oldModel != null) {
                    oldModel.removeTableModelListener(ResizeTableColumnSupport.this);
                }

                initialize();
            }
        });
        table.getTableHeader().addMouseListener(resizingMouseListener);
    }

    private ResizingMouseListener resizingMouseListener = new ResizingMouseListener();

    private class ResizingMouseListener extends MouseAdapter {

        //		copied from BasicTableHeaderUI
        private TableColumn getResizingColumn(Point p) {
            return getResizingColumn(p, table.getTableHeader().getColumnModel().getColumnIndexAtX(p.x));
        }

        // copied from BasicTableHeaderUI
        private TableColumn getResizingColumn(Point p, int column) {
            if (column == -1) {
                return null;
            }
            Rectangle r = table.getTableHeader().getHeaderRect(column);
            r.grow(-3, 0);
            if (r.contains(p)) {
                return null;
            }
            int midPoint = r.x + r.width / 2;
            int columnIndex = (p.x < midPoint) ? column - 1 : column;
            if (columnIndex == -1) {
                return null;
            }
            return table.getTableHeader().getColumnModel().getColumn(columnIndex);
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                TableColumn column = getResizingColumn(e.getPoint());
                if (column != null) {
                    e.consume();
                    column.setPreferredWidth(preferredWidths[table.convertColumnIndexToView(column.getModelIndex())]);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        resetWidths();
    }
}