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

import javax.swing.JTable;

import junit.framework.TestCase;

import org.springframework.richclient.settings.TransientSettings;

/**
 * @author Peter De Bruycker
 */
public class TableMementoTests extends TestCase {

	private JTable table;

	private TableMemento memento;

	public void testSaveSelectionRowsWithoutSelection() {
		TransientSettings settings = new TransientSettings();
		memento.saveSelectedRows(settings);

		assertFalse(settings.contains("table.selectedRows"));
	}

	public void testSaveSelectionRowsWithoutOneRow() {
		TransientSettings settings = new TransientSettings();

		table.getSelectionModel().setSelectionInterval(1, 1);
		memento.saveSelectedRows(settings);
		assertTrue(settings.contains("table.selectedRows"));
		assertEquals("1", settings.getString("table.selectedRows"));
	}

	public void testSaveSelectionRowsWithoutTwoRows() {
		TransientSettings settings = new TransientSettings();

		table.getSelectionModel().setSelectionInterval(0, 0);
		table.getSelectionModel().addSelectionInterval(2, 2);
		memento.saveSelectedRows(settings);
		assertTrue(settings.contains("table.selectedRows"));
		assertEquals("0,2", settings.getString("table.selectedRows"));
	}

	public void testSaveSelectionRowsWithOneInterval() {
		TransientSettings settings = new TransientSettings();

		table.getSelectionModel().setSelectionInterval(0, 2);
		table.getSelectionModel().addSelectionInterval(4, 4);
		memento.saveSelectedRows(settings);
		assertTrue(settings.contains("table.selectedRows"));
		assertEquals("0-2,4", settings.getString("table.selectedRows"));
	}

	public void testSaveSelectionRowsWithTwoIntervals() {
		TransientSettings settings = new TransientSettings();

		table.getSelectionModel().setSelectionInterval(0, 1);
		table.getSelectionModel().addSelectionInterval(3, 4);
		memento.saveSelectedRows(settings);
		assertTrue(settings.contains("table.selectedRows"));
		assertEquals("0-1,3-4", settings.getString("table.selectedRows"));
	}

	public void testSaveColumnWidths() {
		TransientSettings settings = new TransientSettings();

		table.getColumnModel().getColumn(0).setWidth(30);
		table.getColumnModel().getColumn(1).setWidth(120);
		table.getColumnModel().getColumn(2).setWidth(50);
		table.getColumnModel().getColumn(3).setWidth(70);

		memento.saveColumnWidths(settings);

		assertTrue(settings.contains("table.columnWidths"));
		assertEquals("30,120,50,70", settings.getString("table.columnWidths"));
	}

	public void testRestoreColumnWidths() {
		TransientSettings settings = new TransientSettings();
		settings.setString("table.columnWidths", "30,120,50,70");

		memento.restoreColumnWidths(settings);

		assertEquals(30, table.getColumnModel().getColumn(0).getWidth());
		assertEquals(120, table.getColumnModel().getColumn(1).getWidth());
		assertEquals(50, table.getColumnModel().getColumn(2).getWidth());
		assertEquals(70, table.getColumnModel().getColumn(3).getWidth());

		assertEquals(30, table.getColumnModel().getColumn(0).getPreferredWidth());
		assertEquals(120, table.getColumnModel().getColumn(1).getPreferredWidth());
		assertEquals(50, table.getColumnModel().getColumn(2).getPreferredWidth());
		assertEquals(70, table.getColumnModel().getColumn(3).getPreferredWidth());
	}

	public void testRestoreColumnWidthsWithIncorrectColumnCount() {
		int width0 = table.getColumnModel().getColumn(0).getWidth();
		int width1 = table.getColumnModel().getColumn(1).getWidth();
		int width2 = table.getColumnModel().getColumn(2).getWidth();
		int width3 = table.getColumnModel().getColumn(3).getWidth();

		TransientSettings settings = new TransientSettings();
		settings.setString("table.columnWidths", "30,120,50");

		memento.restoreColumnWidths(settings);

		assertEquals(width0, table.getColumnModel().getColumn(0).getWidth());
		assertEquals(width1, table.getColumnModel().getColumn(1).getWidth());
		assertEquals(width2, table.getColumnModel().getColumn(2).getWidth());
		assertEquals(width3, table.getColumnModel().getColumn(3).getWidth());
	}

	public void testRestoreColumnWidthsWithIllegalSettingsString() {
		int width0 = table.getColumnModel().getColumn(0).getWidth();
		int width1 = table.getColumnModel().getColumn(1).getWidth();
		int width2 = table.getColumnModel().getColumn(2).getWidth();
		int width3 = table.getColumnModel().getColumn(3).getWidth();

		TransientSettings settings = new TransientSettings();
		settings.setString("table.columnWidths", "illegalPref");

		memento.restoreColumnWidths(settings);

		assertEquals(width0, table.getColumnModel().getColumn(0).getWidth());
		assertEquals(width1, table.getColumnModel().getColumn(1).getWidth());
		assertEquals(width2, table.getColumnModel().getColumn(2).getWidth());
		assertEquals(width3, table.getColumnModel().getColumn(3).getWidth());
	}

	public void testSaveColumnOrder() {
		TransientSettings settings = new TransientSettings();

		table.getColumnModel().moveColumn(0, 2);
		table.getColumnModel().moveColumn(3, 1);

		memento.saveColumnOrder(settings);

		assertTrue(settings.contains("table.columnOrder"));
		assertEquals("1,3,2,0", settings.getString("table.columnOrder"));
	}

	public void testRestoreColumnOrderWithIncorrectColumnCount() {
		TransientSettings settings = new TransientSettings();
		settings.setString("table.columnOrder", "1,3,2");

		memento.restoreColumnOrder(settings);

		memento.saveColumnOrder(settings);
		assertTrue(settings.contains("table.columnOrder"));
		assertEquals("0,1,2,3", settings.getString("table.columnOrder"));
	}

	/**
	 * Still got a bug in it. This was tested under jdk1.3.1 and it worked :-(,
	 * switched to jdk1.4.2, and it fails
	 */
	public void testRestoreColumnOrder() {
		TransientSettings settings = new TransientSettings();
		settings.setString("table.columnOrder", "0,3,1,2");

		memento.restoreColumnOrder(settings);

		memento.saveColumnOrder(settings);
		assertTrue(settings.contains("table.columnOrder"));
		assertEquals("0,3,1,2", settings.getString("table.columnOrder"));
	}

	public void testRestoreColumnOrderWithIllegalSettingsString() {
		TransientSettings settings = new TransientSettings();
		settings.setString("table.columnOrder", "illegalPref");

		memento.restoreColumnOrder(settings);

		memento.saveColumnOrder(settings);
		assertTrue(settings.contains("table.columnOrder"));
		assertEquals("0,1,2,3", settings.getString("table.columnOrder"));
	}

	protected void setUp() throws Exception {
		table = new JTable(new Object[][] { { "cel(0,0)", "cell(0,1)", "cell(0,2)", "cell(0,3)" },
				{ "cell(1,0)", "cell(1,1)", "cell(1,2)", "cell(1,3)" },
				{ "cell(2,0)", "cell(2,1)", "cell(2,2)", "cell(2,3)" },
				{ "cell(3,0)", "cell(3,1)", "cell(3,2)", "cell(3,3)" },
				{ "cell(4,0)", "cell(4,1)", "cell(4,2)", "cell(4,3)" } },
				new Object[] { "col0", "col1", "col2", "col3" });
		assertEquals(-1, table.getSelectedRow());

		memento = new TableMemento(table, "table");
	}
	
	public void testConstructor() {
		try {
			new TableMemento(null);
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// test passes
		}

		try {
			table.setName(null);
			new TableMemento(table, "");
			fail("Should throw IllegalArgumentException: table has no name");
		} catch (Exception e) {
			// test passes
		}

		table.setName("table0");

		TableMemento memento = new TableMemento(table);
		assertEquals(table, memento.getTable());
		assertEquals("table0", memento.getKey());
		
		memento = new TableMemento(table, "key");
		assertEquals(table, memento.getTable());
		assertEquals("key", memento.getKey());
	}
}