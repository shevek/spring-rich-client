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

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

import org.springframework.rules.constraint.Constraint;
import org.springframework.util.Assert;

/**
 * @author kdonald
 */
public class FilteredComboBoxListModel extends FilteredListModel implements ComboBoxModel {

    private boolean matchedSelected;

    private boolean selectingItem;

    public FilteredComboBoxListModel(ComboBoxModel filteredModel, Constraint filter) {
        super(filteredModel, filter);
    }

    public void setFilteredModel(ListModel model) {
        Assert.isInstanceOf(ComboBoxModel.class, model);
        super.setFilteredModel(model);
    }

    protected ComboBoxModel getComboBoxModel() {
        return (ComboBoxModel) getFilteredModel();
    }

    protected void onMatchingElement(Object element) {
        if (element == getSelectedItem()) {
            matchedSelected = true;
        }
    }

    protected void postConstraintApplied() {
        if (!matchedSelected) {
            if (getSize() > 0) {
                setSelectedItem(getElementAt(0));
            } else {
                setSelectedItem(null);
            }
        }
        matchedSelected = false;
    }

    public Object getSelectedItem() {
        if (getSize() == 0) {
            return null;
        }
        return getComboBoxModel().getSelectedItem();
    }

    public void setSelectedItem(Object anItem) {
        if (!selectingItem) {
            selectingItem = true;
            try {
                getComboBoxModel().setSelectedItem(anItem);
            } finally {
                selectingItem = false;
            }
        }
    }

}