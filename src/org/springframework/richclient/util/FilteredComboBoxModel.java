/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.util;

import javax.swing.ComboBoxModel;

import org.springframework.rules.UnaryPredicate;

/**
 * @author kdonald
 */
public class FilteredComboBoxModel extends FilteredListModel implements
        ComboBoxModel {

    private boolean matchedSelected;
    
    public FilteredComboBoxModel(ComboBoxModel filteredModel, UnaryPredicate filter) {
        super(filteredModel, filter);
    }
    
    protected ComboBoxModel getComboBoxModel() {
        return (ComboBoxModel)getModel();
    }
    
    protected void onMatchingElement(Object element) {
        if (element == getSelectedItem()) {
            matchedSelected = true;
        }
    }
    
    protected void onFilterApplied() {
        if (!matchedSelected) {
            if (getSize() > 0 ) {
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
        getComboBoxModel().setSelectedItem(anItem);
    }

}
