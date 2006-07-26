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

import java.util.List;

/**
 * Designed to display object arrays / lists in a list for multi-column tables,
 * or just plain objects for single column table. Nicely aligned with Hibernate
 * List behaivior for query result sets.
 * 
 * @author keith
 */
public abstract class ListTableModel extends BaseTableModel {

    public ListTableModel() {
        super();
    }

    public ListTableModel(List rows) {
        super(rows);
    }

    protected Object getValueAtInternal(Object row, int columnIndex) {
        if (row != null && getDataColumnCount() > 1) {            
            if (row.getClass().isArray()) {
                Object[] arrayRow = (Object[])row;
                return arrayRow[columnIndex];
            }
            else if (row instanceof List) {
                return ((List)row).get(columnIndex);
            }
            else {
                throw new IllegalArgumentException("Unsupported row collection type " + row);
            }
        }

        if (row != null && row.getClass().isArray())
            return ((Object[])row)[0];

        return row;
    }
}