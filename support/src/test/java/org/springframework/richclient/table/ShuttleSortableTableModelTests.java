/*
 * Copyright 2002-2008 the original author or authors.
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

import java.util.Comparator;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;

public class ShuttleSortableTableModelTests extends TestCase {

    public void testNullComparisonWithComparator() {
        Object[] columnNames = new Object[] { "first name", "last name" };
        Object[][] data = new Object[][] { { "Peter", "De Bruycker" },
                { "Jan", "Hoskens" }, { null, "test" } };

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        ShuttleSortableTableModel shuttleSortableTableModel = new ShuttleSortableTableModel(tableModel);
        shuttleSortableTableModel.setComparator(0, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;

                if (s1 == null && s2 == null) {
                    return 0;
                }

                if (s1 == null) {
                    return 1;
                }
                if (s2 == null) {
                    return -1;
                }

                return s1.compareTo(s2);
            }
        });

        shuttleSortableTableModel.sortByColumn(new ColumnToSort(1, 0));

        // the row with first name == null must be the last one after sort
        assertEquals("Jan", shuttleSortableTableModel.getValueAt(0, 0));
        assertEquals("Peter", shuttleSortableTableModel.getValueAt(1, 0));
        assertEquals(null, shuttleSortableTableModel.getValueAt(2, 0));
    }

    public void testNullComparisonWithoutComparator() {
        Object[] columnNames = new Object[] { "first name", "last name", "test bean" };
        Object[][] data = new Object[][] { { "Peter", "De Bruycker", new TestBean("1") },
                { "Jan", "Hoskens", new TestBean("2") }, { null, "test", null } };

        TableModel tableModel = new DefaultTableModel(data, columnNames) {
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return TestBean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        ShuttleSortableTableModel shuttleSortableTableModel = new ShuttleSortableTableModel(tableModel);

        shuttleSortableTableModel.sortByColumn(new ColumnToSort(1, 2));

        // the row with first name == null must be the last one after sort
        assertEquals(null, shuttleSortableTableModel.getValueAt(0, 0));
        assertEquals("Peter", shuttleSortableTableModel.getValueAt(1, 0));
        assertEquals("Jan", shuttleSortableTableModel.getValueAt(2, 0));
    }
}
