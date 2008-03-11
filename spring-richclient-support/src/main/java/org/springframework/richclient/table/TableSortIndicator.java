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
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.image.ArrowIcon;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class TableSortIndicator implements TableColumnModelListener {
	public static final ArrowIcon STANDARD_UP_ARROW = new ArrowIcon(ArrowIcon.Direction.UP, 4,
			SystemColor.controlDkShadow);

	public static final ArrowIcon STANDARD_DOWN_ARROW = new ArrowIcon(ArrowIcon.Direction.DOWN, 4,
			SystemColor.controlDkShadow);

	private final TableHeaderClickHandler tableHeaderClickHandler = new TableHeaderClickHandler();

	private JTable table;

	private Icon ascendingIcon;

	private Icon descendingIcon;

	private ColumnSortList sortList;

	public TableSortIndicator(JTable table) {
		this(table, STANDARD_UP_ARROW, STANDARD_DOWN_ARROW);
	}

	public TableSortIndicator(JTable table, Icon ascendingIcon, Icon descendingIcon) {
		Assert.notNull(table);
		Assert.notNull(ascendingIcon);
		Assert.notNull(descendingIcon);
		this.table = table;
		this.ascendingIcon = ascendingIcon;
		this.descendingIcon = descendingIcon;
		sortList = new ColumnSortList();
		initHeaderRenderers();
		this.table.getColumnModel().addColumnModelListener(this);
		this.table.getTableHeader().addMouseListener(tableHeaderClickHandler);
	}

	public ColumnSortList getColumnSortList() {
		return sortList;
	}

	private void initHeaderRenderers() {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setHeaderRenderer(new HeaderRenderer(table.getTableHeader()));
			col.setPreferredWidth(TableUtils.calculatePreferredColumnWidth(table, col));
			col.setWidth(col.getPreferredWidth());
		}
	}

	private class TableHeaderClickHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.isMetaDown()) {
				return;
			}
			int columnView = table.getColumnModel().getColumnIndexAtX(e.getX());
			if (columnView == -1) {
				return;
			}
			// make sure mouseclick was not in resize area
			Rectangle r = table.getTableHeader().getHeaderRect(columnView);
			// working with a magic value of 3 here, as it is in TableHeaderUI
			r.grow(-3, 0);
			if (!r.contains(e.getPoint())) {
				return;
			}

			int column = table.convertColumnIndexToModel(columnView);
			int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
			ColumnToSort columnToSort = sortList.getSortLevel(column);
			if (columnToSort != null) {
				if (shiftPressed == 1) {
					sortList.toggleSortOrder(column);
					displayRendererIcon(column, columnToSort.getSortOrder());
				}
				else {
					SortOrder order;
					if (sortList.size() > 1) {
						order = SortOrder.ASCENDING;
					}
					else {
						order = columnToSort.getSortOrder().flip();
					}
					sortList.setSingleSortLevel(column, order);
					removeRendererIcons();
					displayRendererIcon(columnView, order);
				}
			}
			else {
				if (shiftPressed == 1) {
					try {
						sortList.addSortLevel(column, SortOrder.ASCENDING);
						displayRendererIcon(columnView, SortOrder.ASCENDING);
					}
					catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(table.getTopLevelAncestor(),
								"Maximum number of sort levels reached.", "Table Sorter", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				else {
					sortList.setSingleSortLevel(column, SortOrder.ASCENDING);
					removeRendererIcons();
					displayRendererIcon(columnView, SortOrder.ASCENDING);
				}
			}
			table.getTableHeader().resizeAndRepaint();
		}
	}

	private static final class HeaderRenderer extends DefaultTableCellRenderer {
		private JTableHeader tableHeader;

		public HeaderRenderer(JTableHeader header) {
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setIconTextGap(UIConstants.TWO_SPACES);
			setHorizontalTextPosition(SwingConstants.LEFT);
			this.tableHeader = header;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean iSelected, boolean hasFocus,
				int row, int column) {
			setText((value == null) ? "" : value.toString());
			setFont(tableHeader.getFont());
			return this;
		}
	}

	private void displayRendererIcon(int column, SortOrder order) {
		HeaderRenderer rend = getRenderer(column);
		if (order == SortOrder.ASCENDING) {
			rend.setIcon(this.ascendingIcon);
		}
		else {
			rend.setIcon(this.descendingIcon);
		}
	}

	private void removeRendererIcons() {
		TableColumnModel colModel = table.getColumnModel();
		for (int i = 0; i < colModel.getColumnCount(); i++) {
			HeaderRenderer rend = (HeaderRenderer) colModel.getColumn(i).getHeaderRenderer();
			rend.setIcon(null);
		}
	}

	private HeaderRenderer getRenderer(int columnIndex) {
		TableColumn column = table.getColumnModel().getColumn(columnIndex);
		return (HeaderRenderer) column.getHeaderRenderer();
	}

	public void columnAdded(TableColumnModelEvent e) {
		TableColumn column = table.getColumnModel().getColumn(e.getToIndex());
		if (column.getHeaderRenderer() instanceof HeaderRenderer) {
			((HeaderRenderer)column.getHeaderRenderer()).setIcon(null);
		}
		else {
			column.setHeaderRenderer(new HeaderRenderer(table.getTableHeader()));
		}
	}

	public void columnMarginChanged(ChangeEvent e) {
	}

	public void columnMoved(TableColumnModelEvent e) {
	}

	public void columnRemoved(TableColumnModelEvent e) {
	}

	public void columnSelectionChanged(ListSelectionEvent e) {
	}
}