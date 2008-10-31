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

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

/**
 * Simple adapter to create a {@link ComboBoxModel} from an existing {@link ListModel}
 * 
 * @author Mathias Broekelmann
 */
public class ComboBoxListModelAdapter extends AbstractFilteredListModel implements ComboBoxModel {

    private Object selectedItem;

    /**
     * Constructs a new instance with a given listModel
     * 
     * @param listModel
     *            the listmodel containing the elements
     * 
     * @throws IllegalArgumentException
     *             if listModel is null
     */
    public ComboBoxListModelAdapter(ListModel listModel) {
        super(listModel);
    }
    
    public Object getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Object anItem) {
        this.selectedItem = anItem;
        fireContentsChanged(this, -1, -1);
    }

}
