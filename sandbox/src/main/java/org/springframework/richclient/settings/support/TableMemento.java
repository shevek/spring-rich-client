package org.springframework.richclient.settings.support;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class TableMemento implements Memento {
	private static final Log logger = LogFactory.getLog(TableMemento.class);

	private static final String COLUMN_WIDTHS = "columnWidths";

	private static final String COLUMN_ORDER = "columnOrder";

	private static final String SELECTED_ROWS = "selectedRows";

	private static final String ANCHOR = "anchor";

	private static final String LEAD = "lead";

	private JTable table;

	private String key;

	public TableMemento(JTable table, String key) {
		Assert.notNull(table, "Table cannot be null");
		Assert.isTrue(StringUtils.hasText(key) || StringUtils.hasText(table.getName()),
				"Key is empty or table has no name");

		if (!StringUtils.hasText(key)) {
			key = table.getName();
		}
		
		this.table = table;
		this.key = key;
	}

	public TableMemento(JTable table) {
		this(table, null);
	}

	public void saveState(Settings settings) {
		saveSelectedRows(settings);
		saveColumnOrder(settings);
		saveColumnWidths(settings);
	}

	void saveColumnWidths(Settings settings) {
		StringBuffer sb = new StringBuffer();
		int columnCount = table.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			sb.append(table.getColumnModel().getColumn(i).getWidth());
			if (i < columnCount - 1) {
				sb.append(",");
			}
		}
		settings.setString(key + "." + COLUMN_WIDTHS, sb.toString());
	}

	void saveColumnOrder(Settings settings) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			sb.append(column.getModelIndex());
			if (i < table.getColumnModel().getColumnCount() - 1) {
				sb.append(",");
			}
		}
		settings.setString(key + "." + COLUMN_ORDER, sb.toString());
	}

	void saveSelectedRows(Settings settings) {
		String settingsKey = key + "." + SELECTED_ROWS;
		if (settings.contains(settingsKey)) {
			settings.remove(settingsKey);
		}

		if (table.getSelectedRowCount() > 0) {
			settings.setInt(key + "." + ANCHOR, table.getSelectionModel().getAnchorSelectionIndex());
			settings.setInt(key + "." + LEAD, table.getSelectionModel().getLeadSelectionIndex());
		}

		String selectionString = ArrayUtil.asIntervalString(table.getSelectedRows());
		if (selectionString.length() > 0) {
			settings.setString(settingsKey, selectionString);
		}
	}

	public void restoreState(Settings settings) {
		restoreColumnOrder(settings);
		restoreColumnWidths(settings);
		restoreSelectedRows(settings);
	}

	void restoreColumnWidths(Settings settings) {
		table.getSelectionModel().clearSelection();
		String widthSetting = settings.getString(key + "." + COLUMN_WIDTHS);
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

	void restoreSelectedRows(Settings settings) {
		table.getSelectionModel().clearSelection();
		if (settings.contains(key + "." + SELECTED_ROWS)) {
			String selection = settings.getString(key + "." + SELECTED_ROWS);
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

		if (settings.contains(key + "." + ANCHOR)) {
			table.getSelectionModel().setAnchorSelectionIndex(settings.getInt(key + "." + ANCHOR));
		}
		if (settings.contains(key + "." + LEAD)) {
			table.getSelectionModel().setLeadSelectionIndex(settings.getInt(key + "." + LEAD));
		}
	}

	void restoreColumnOrder(Settings settings) {
		table.getSelectionModel().clearSelection();
		String orderSetting = settings.getString(key + "." + COLUMN_ORDER);
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

	protected String getKey() {
		return key;
	}

	public JTable getTable() {
		return table;
	}
}
