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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class AbstractTableModelFilter extends AbstractTableModel implements TableModelListener {
    protected final Log logger = LogFactory.getLog(getClass());

    protected TableModel filteredModel;

    public AbstractTableModelFilter(TableModel model) {
        Assert.notNull(model);
        this.filteredModel = model;
        this.filteredModel.addTableModelListener(this);
    }

    public TableModel getFilteredModel() {
        return filteredModel;
    }

    // By default, implement TableModel by forwarding all messages
    // to the model.
    public Object getValueAt(int aRow, int aColumn) {
        return filteredModel.getValueAt(aRow, aColumn);
    }

    public void setValueAt(Object aValue, int aRow, int aColumn) {
        filteredModel.setValueAt(aValue, aRow, aColumn);
    }

    public int getRowCount() {
        return filteredModel.getRowCount();
    }

    public int getColumnCount() {
        return filteredModel.getColumnCount();
    }

    public String getColumnName(int aColumn) {
        return filteredModel.getColumnName(aColumn);
    }

    public Class getColumnClass(int aColumn) {
        return filteredModel.getColumnClass(aColumn);
    }

    public boolean isCellEditable(int row, int column) {
        return filteredModel.isCellEditable(row, column);
    }

    // By default forward all events to all the listeners.
    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }
}