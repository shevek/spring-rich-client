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

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.util.Assert;

/**
 * Listens to a sort list for changes and when they happen, sorts a sortable
 * table model in a separate worker thread.
 * 
 * @author Keith Donald
 */
public class SortTableCommand extends ActionCommand implements Observer {
    private JTable table;

    private SortableTableModel sortableTableModel;

    private ColumnSortList sortList;

    public SortTableCommand(JTable table, ColumnSortList sortList) {
        super("sortCommand");
        this.table = table;
        Assert
                .isTrue((table.getModel() instanceof SortableTableModel),
                        "The specified table's model must be sortable!");
        this.sortableTableModel = (SortableTableModel)table.getModel();
        this.sortList = sortList;
        this.sortList.addObserver(this);
    }

    public void update(Observable o, Object args) {
        doSort();
    }

    protected void doExecuteCommand() {
        doSort();
    }

    private void doSort() {
        try {
            BusyIndicator.showAt(table);

            final int[] preSortSelectedRows = table.getSelectedRows();

            int[] postSortSelectedRows = sortableTableModel
                    .sortByColumns(sortList.getSortLevels(), preSortSelectedRows);

            for (int i = 0; i < postSortSelectedRows.length; i++) {
                table.addRowSelectionInterval(postSortSelectedRows[i], postSortSelectedRows[i]);
            }

            if (postSortSelectedRows.length > 0) {
                TableUtils.scrollToRow(table, postSortSelectedRows[0]);
            }
        }
        finally {
            BusyIndicator.clearAt(table);
        }
    }

    public void dispose() {
        this.sortList.deleteObserver(this);
    }
}