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

import org.springframework.richclient.settings.TransientSettings;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class TableSettingsTests extends TestCase {

    private JTable table;

    public void testSaveSelectionRowsWithoutSelection() {
        TransientSettings settings = new TransientSettings();
        TableSettings.saveSelectedRows(settings, "table", table);

        assertFalse(settings.contains("table.selectedRows"));
    }

    public void testSaveSelectionRowsWithoutOneRow() {
        TransientSettings settings = new TransientSettings();

        table.getSelectionModel().setSelectionInterval(1, 1);
        TableSettings.saveSelectedRows(settings, "table", table);
        assertTrue(settings.contains("table.selectedRows"));
        assertEquals("1", settings.getString("table.selectedRows"));
    }

    public void testSaveSelectionRowsWithoutTwoRows() {
        TransientSettings settings = new TransientSettings();

        table.getSelectionModel().setSelectionInterval(0, 0);
        table.getSelectionModel().addSelectionInterval(2, 2);
        TableSettings.saveSelectedRows(settings, "table", table);
        assertTrue(settings.contains("table.selectedRows"));
        assertEquals("0,2", settings.getString("table.selectedRows"));
    }

    public void testSaveSelectionRowsWithOneInterval() {
        TransientSettings settings = new TransientSettings();

        table.getSelectionModel().setSelectionInterval(0, 2);
        table.getSelectionModel().addSelectionInterval(4, 4);
        TableSettings.saveSelectedRows(settings, "table", table);
        assertTrue(settings.contains("table.selectedRows"));
        assertEquals("0-2,4", settings.getString("table.selectedRows"));
    }

    public void testSaveSelectionRowsWithTwoIntervals() {
        TransientSettings settings = new TransientSettings();

        table.getSelectionModel().setSelectionInterval(0, 1);
        table.getSelectionModel().addSelectionInterval(3, 4);
        TableSettings.saveSelectedRows(settings, "table", table);
        assertTrue(settings.contains("table.selectedRows"));
        assertEquals("0-1,3-4", settings.getString("table.selectedRows"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        table = new JTable(new Object[][] { { "cel(0,0)", "cell(0,1)", "cell(0,2)" },
                { "cell(1,0)", "cell(1,1)", "cell(1,2)" }, { "cell(2,0)", "cell(2,1)", "cell(2,2)" },
                { "cell(3,0)", "cell(3,1)", "cell(3,2)" }, { "cell(4,0)", "cell(4,1)", "cell(4,2)" } }, new Object[] {
                "col0", "col1", "col2" });
        assertEquals(-1, table.getSelectedRow());
    }
}