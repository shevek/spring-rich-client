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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.springframework.richclient.settings.Settings;

public class TableSettings {
	private TableSettings() {
		// no instantiation, static utility class
	}

	public static void saveState(Settings s, String key, JTable table) {
		saveSelectedRows(s, key, table);
		saveColumnOrder(s, key, table);
	}

	public static void saveColumnOrder(Settings s, String key, JTable table) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			sb.append(column.getModelIndex());
			if(i < table.getColumnModel().getColumnCount() -1 ) {
				sb.append(",");
			}
		}
		s.setString(key+".columnOrder", sb.toString());
	}

	public static void saveSelectedRows(Settings s, String key, JTable table) {
		int[] selection = table.getSelectedRows();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < selection.length; i++) {
			sb.append(selection[i]);
			if (i < selection.length - 1) {
				sb.append(",");
			}
		}
		s.setString(key+".selectedRows", sb.toString());
	}

	public static void restoreSelectedRows(Settings s, String key, JTable table) {
		table.getSelectionModel().clearSelection();
		String selection = s.getString(key+".selectedRows");
		if (!selection.equals("")) {
			String[] indexes = selection.split(",");
			for(int i = 0; i < indexes.length; i++) {
				int index = Integer.parseInt(indexes[i]);
				table.addRowSelectionInterval(index, index);
			}
		}
	}

	public static void restoreColumnOrder(Settings s, String key, JTable table) {
		String columnOrder = s.getString(key+".columnOrder");
		if(!columnOrder.equals("")) {
			List columns = new ArrayList();
			Enumeration columnEnum = table.getColumnModel().getColumns();
			while(columnEnum.hasMoreElements()) {
				columns.add(columnEnum.nextElement());
			}
			System.out.println(columns);

			String[] columnIndexes = columnOrder.split(",");
			for(int i = 0; i < columnIndexes.length; i++) {
				TableColumn column = (TableColumn)columns.get(i);
				int newIndex = Integer.parseInt(columnIndexes[i]);
				System.out.println("old="+i+", new = "+newIndex);
				System.out.println("index of column "+columns.indexOf(column));
				if (columns.indexOf(column) != newIndex)
				{
					System.out.println("old <> newIndex: move column");
					// move the column
					table.getColumnModel().moveColumn(i, newIndex);
					columns.remove(column);
					columns.add(newIndex, column);
					System.out.println(columns);
				}
			}
		}
	}

	public static void restoreState(Settings s, String key, JTable table) {
		restoreSelectedRows(s, key, table);
		restoreColumnOrder(s, key, table);
	}
}