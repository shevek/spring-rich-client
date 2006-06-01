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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * @author Keith Donald
 */
public class ColumnSortList extends Observable {
    public static final int MAX_SORT_LEVELS = 4;

    private List sortLevels = new ArrayList();

    public ColumnSortList() {
    }

    public ColumnToSort getSortLevel(int columnIndex) {
        return findColumnToSort(columnIndex);
    }

    public ColumnToSort[] getSortLevels() {
        return (ColumnToSort[])sortLevels.toArray(new ColumnToSort[0]);
    }

    public int size() {
        return sortLevels.size();
    }

    public boolean isSorted(int columnIndex) {
        if (findColumnToSort(columnIndex) != null)
            return true;

        return false;
    }

    public void addSortLevel(int columnIndex, SortOrder order) {
        if (sortLevels.size() == MAX_SORT_LEVELS) {
            throw new IllegalArgumentException("Max sort level reached.");
        }
        sortLevels.add(new ColumnToSort(sortLevels.size(), columnIndex, order));
        setChanged();
        notifyObservers();
    }

    public void toggleSortOrder(int columnIndex) {
        ColumnToSort column = findColumnToSort(columnIndex);
        if (column == null)
            throw new IllegalArgumentException("No such sorted column.");

        column.toggleSortOrder();
        setChanged();
        notifyObservers();
    }

    public void setSingleSortLevel(int columnIndex, SortOrder order) {
        sortLevels.clear();
        addSortLevel(columnIndex, order);
    }

    private ColumnToSort findColumnToSort(int columnIndex) {
        for (Iterator i = sortLevels.iterator(); i.hasNext();) {
            ColumnToSort column = (ColumnToSort)i.next();
            if (column.getColumnIndex() == columnIndex) {
                return column;
            }
        }
        return null;
    }
}