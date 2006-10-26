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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.springframework.core.enums.LetterCodedLabeledEnum;
import org.springframework.core.enums.ShortCodedLabeledEnum;
import org.springframework.core.enums.StringCodedLabeledEnum;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.table.renderer.BeanTableCellRenderer;
import org.springframework.richclient.table.renderer.BooleanTableCellRenderer;
import org.springframework.richclient.table.renderer.DateTimeTableCellRenderer;
import org.springframework.richclient.table.renderer.LabeledEnumTableCellRenderer;
import org.springframework.richclient.util.WindowUtils;

/**
 * @author Keith Donald
 */
public class TableUtils {

    public static void scrollToRow(JTable table, int row) {
		if (!(table.getParent() instanceof JViewport)) {
			return;
		}
		JViewport viewport = (JViewport)table.getParent();
		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle rect = table.getCellRect(row, 0, true);
		// The location of the viewport relative to the table
		Point pt = viewport.getViewPosition();
		// Translate the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0,0)
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		// Scroll the area into view
		viewport.scrollRectToVisible(rect);
	}

	public static JTable createStandardSortableTable(TableModel tableModel) {
		JTable table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		installDefaultRenderers(table);
		attachSorter(table);
		sizeColumnsToFitRowData(table);
		return table;
	}

	public static JTable attachSorter(JTable table) {
		TableModel tableModel = table.getModel();
		ShuttleSortableTableModel sortedModel = new ShuttleSortableTableModel(tableModel);
		table.setAutoCreateColumnsFromModel(true);
		table.setModel(sortedModel);
		TableSortIndicator sortIndicator = new TableSortIndicator(table);
		new SortTableCommand(table, sortIndicator.getColumnSortList());
		return table;
	}

	public static void installDefaultRenderers(JTable table) {
		BeanTableCellRenderer beanRenderer = new BeanTableCellRenderer();
		table.setDefaultRenderer(Object.class, beanRenderer);
		LabeledEnumTableCellRenderer er = new LabeledEnumTableCellRenderer();
		table.setDefaultRenderer(ShortCodedLabeledEnum.class, er);
		table.setDefaultRenderer(StringCodedLabeledEnum.class, er);
		table.setDefaultRenderer(LetterCodedLabeledEnum.class, er);
		table.setDefaultRenderer(Date.class, new DateTimeTableCellRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer());
	}

	public static void setPreferredColumnWidths(JTable table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			int w = calculatePreferredColumnWidth(table, col);
			col.setPreferredWidth(w);
			col.setWidth(w);
		}
	}

	/**
	 * Calculates the preferred width of a table column based on the header.
	 * 
	 * @param table
	 * @return the preferred table width
	 */
	public static int calculatePreferredColumnWidth(JTable table, TableColumn col) {
		TableModel model = table.getModel();
		String colName = model.getColumnName(col.getModelIndex());
		return new JLabel(colName).getPreferredSize().width + UIConstants.THREE_SPACES + UIConstants.TWO_SPACES;
	}

	/**
	 * Returns the innermost table model associated with this table; if layers
	 * of table model filters are wrapping it.
	 */
	public static TableModel getUnfilteredTableModel(JTable table) {
		return getUnfilteredTableModel(table.getModel());
	}

    /**
     * resizes the column widths to optimally fit the row data.
     * <p>
     * this method only tests the first row (if it exists) 
     * @param table the table whose columns should be resized, not null
     */
    public static void sizeColumnsToFitRowData(JTable table) {
        sizeColumnsToFitRowData(table, 1);
    }

    /**
     * resizes the column widths to optimally fit the row data.
     * 
     * @param table
     *            the table whose columns should be resized, not null
     * @param maxNumberOfRows
     *            specifies the maximum number of rows to evaluate the column widths. If it is lower or equals 0 all rows
     *            will be evaluated
     */
    public static void sizeColumnsToFitRowData(JTable table, int maxNumberOfRows) {
		if (table.getRowCount() > 0) {
            int rowSize = maxNumberOfRows <= 0 ? table.getRowCount() : Math
                    .min(maxNumberOfRows, table.getRowCount());
			for (int col = 0, colSize = table.getColumnCount(); col < colSize; col++) {
                int width = 0;
                TableColumn column = table.getColumnModel().getColumn(col);
                TableCellRenderer r = table.getColumnModel().getColumn(col).getCellRenderer();
                for (int row = 0; row < rowSize; row++) {
                    Object val = table.getValueAt(row, col);
    				if (r == null) {
    					if (val != null) {
    						r = table.getDefaultRenderer(val.getClass());
    					}
    				}
    				if (r != null) {
    					Component c = r
                                .getTableCellRendererComponent(table, val, false, false, row, col);
    					int cWidth = c.getPreferredSize().width;
                        if(cWidth > width) {
                            width = cWidth;
                        }
    				}
    			}
                column.setPreferredWidth(width + UIConstants.ONE_SPACE);
                column.setWidth(column.getPreferredWidth());
			}
		}
		int width = Math.min(table.getColumnModel().getTotalColumnWidth(), (int)(WindowUtils.getScreenWidth() * .75));
		table.setPreferredScrollableViewportSize(new Dimension(width, 300));
	}

	public static TableModel getUnfilteredTableModel(TableModel tableModel) {
		if (tableModel instanceof AbstractTableModelFilter) {
			return getUnfilteredTableModel(((AbstractTableModelFilter)tableModel).getFilteredModel());
		}
		return tableModel;
	}

	/**
	 * Workaround for a very annoying bug in jtable where an editing cell value
	 * does not get committed on focus lost.
	 * 
	 * @param table
	 */
	public static void stopCellEditing(JTable table) {
		int row = table.getEditingRow();
		int col = table.getEditingColumn();
		if (table.isEditing()) {
			if (row < table.getRowCount()) {
				table.getCellEditor(row, col).stopCellEditing();
			}
		}
	}
}