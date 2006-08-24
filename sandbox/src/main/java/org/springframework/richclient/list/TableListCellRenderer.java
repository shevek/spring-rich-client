/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.richclient.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.springframework.util.Assert;

/**
 * ListCellRenderer which renders table cells in a list cell.
 * <p>
 * can be used in a {@link JComboBox} to render a table as a popup.
 * 
 * @author Mathias Broekelmann
 * 
 */
public class TableListCellRenderer extends JTable implements ListCellRenderer {

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    private ListCellRenderer cellRenderer = new DefaultListCellRenderer();

    private final JPanel headerPanel = new JPanel(new BorderLayout(0, 0));

    private static class TableListCellRendererModel implements TableModel {

        private int row;

        private TableListModel listModel;

        public TableListCellRendererModel(TableListModel model) {
            listModel = model;
        }

        /**
         * @return the listModel
         */
        public TableListModel getListModel() {
            return listModel;
        }

        public void setListModel(TableListModel model) {
            listModel = model;
        }

        public int getRowCount() {
            return 1;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public Object getValueAt(int aRow, int aColumn) {
            return listModel.getValueAt(row, aColumn);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public int getColumnCount() {
            return listModel.getColumnCount();
        }

        public void addTableModelListener(TableModelListener l) {
        }

        public Class getColumnClass(int columnIndex) {
            return listModel.getColumnClass(columnIndex);
        }

        public String getColumnName(int columnIndex) {
            return listModel.getColumnName(columnIndex);
        }

        public void removeTableModelListener(TableModelListener l) {
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

    }

    public TableListCellRenderer() {
        this(new DefaultTableListModel());
    }

    public TableListCellRenderer(TableListModel model) {
        this(model, null);
    }

    public TableListCellRenderer(TableListModel model, TableColumnModel columnModel) {
        super(new TableListCellRendererModel(model), columnModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setOpaque(true);
        setBorder(getNoFocusBorder());
        headerPanel.add(getTableHeader(), BorderLayout.NORTH);
    }

    public TableListModel getTableListModel() {
        return getTableListCellRendererModel().getListModel();
    }

    public void setTableListModel(TableListModel model) {
        getTableListCellRendererModel().setListModel(model);
    }

    public void setModel(TableModel dataModel) {
        Assert.isInstanceOf(TableListCellRendererModel.class, dataModel);
        super.setModel(dataModel);
    }

    private TableListCellRendererModel getTableListCellRendererModel() {
        return (TableListCellRendererModel) super.getModel();
    }

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return SAFE_NO_FOCUS_BORDER;
        } else {
            return noFocusBorder;
        }
    }

    public void setTableHeader(JTableHeader tableHeader) {
        super.setTableHeader(tableHeader);
        if (headerPanel != null) {
            if (tableHeader == null)
                headerPanel.removeAll();
            else
                headerPanel.add(tableHeader, BorderLayout.NORTH);
        }
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (index == -1) {
            Component comp = cellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return comp;
        }
        getTableListCellRendererModel().setRow(index);

        setComponentOrientation(list.getComponentOrientation());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("List.focusCellHighlightBorder");
            }
        } else {
            border = getNoFocusBorder();
        }
        setBorder(border);

        if (index == 0) {
            headerPanel.add(this);
            return headerPanel;
        }

        return this;
    }

    /**
     * Overridden for performance reasons.
     */
    public void validate() {
    }
    
    /**
     * Overridden for performance reasons.
     */
    protected void validateTree() {
    }

    /**
     * Overridden for performance reasons.
     */
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons.
     */
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
        Object newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, byte oldValue,
        byte newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, char oldValue,
        char newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, short oldValue,
        short newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, int oldValue,
        int newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, long oldValue,
        long newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, float oldValue,
        float newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, double oldValue,
        double newValue) {
    }

    /**
     * Overridden for performance reasons.
     */
    public void firePropertyChange(String propertyName, boolean oldValue,
        boolean newValue) {
    }
}
