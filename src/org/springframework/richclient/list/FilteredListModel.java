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
package org.springframework.richclient.list;

import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.springframework.rules.UnaryPredicate;

/**
 * @author Keith Donald
 */
public class FilteredListModel extends AbstractListModel implements Observer {

    private ListModel filteredListModel;

    private UnaryPredicate filter;

    private int[] indexes;

    private int filteredSize;

    public FilteredListModel(ListModel listModel, UnaryPredicate filter) {
        this.filteredListModel = listModel;
        this.filter = filter;
        if (this.filter instanceof Observable) {
            ((Observable)this.filter).addObserver(this);
        }
        this.indexes = new int[listModel.getSize()];
        applyFilter();
    }

    public void update(Observable changed, Object arg) {
        applyFilter();
        fireContentsChanged(this, -1, -1);
    }

    protected ListModel getModel() {
        return filteredListModel;
    }

    private void applyFilter() {
        filteredSize = 0;
        for (int i = 0, j = 0; i < filteredListModel.getSize(); i++) {
            Object element = filteredListModel.getElementAt(i);
            if (filter.test(element)) {
                indexes[j] = i;
                j++;
                filteredSize++;
                onMatchingElement(element);
            }
        }
        onFilterApplied();
    }

    protected void onMatchingElement(Object element) {

    }

    protected void onFilterApplied() {

    }

    public int getSize() {
        return filteredSize;
    }

    public Object getElementAt(int index) {
        return filteredListModel.getElementAt(indexes[index]);
    }

}