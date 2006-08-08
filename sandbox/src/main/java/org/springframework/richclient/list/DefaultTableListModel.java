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

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Default implementation for {@link TableListModel}
 * 
 * @author Mathias Broekelmann
 * 
 */
public class DefaultTableListModel extends DefaultTableModel implements TableListModel {

    public DefaultTableListModel() {
        super();
    }

    public DefaultTableListModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public DefaultTableListModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public DefaultTableListModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public DefaultTableListModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public DefaultTableListModel(Vector data, Vector columnNames) {
        super(data, columnNames);
    }
}
