/*
 * Copyright 2002-2007 the original author or authors.
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
import java.util.List;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.util.Assert;

/**
 * Helper class used when listening to selection events on a <code>JTable</code> or a
 * <code>JList</code>.
 * @author Peter De Bruycker
 */
public class ListSelectionListenerSupport implements ListSelectionListener {
    private int itemsSelected = 0;

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {

        // filter unwanted events
        if (e.getValueIsAdjusting()) {
            return;
        }

        ListSelectionModel listSelectionModel = getListSelectionModel(e);

        if (listSelectionModel.isSelectionEmpty()) {
            itemsSelected = 0;
            onNoSelection();
        }
        else {
            List indexList = new ArrayList();

            // find selected indexes
            int minIndex = listSelectionModel.getMinSelectionIndex();
            int maxIndex = listSelectionModel.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (listSelectionModel.isSelectedIndex(i)) {
                    indexList.add(new Integer(i));
                }
            }

            itemsSelected = indexList.size();
            if (itemsSelected == 1) {
                onSingleSelection(((Integer)indexList.get(0)).intValue());
            }
            else {
                int[] indexes = new int[indexList.size()];
                for (int i = 0; i < indexList.size(); i++) {
                    indexes[i] = ((Integer)indexList.get(i)).intValue();
                }
                onMultiSelection(indexes);
            }
        }

    }

    /**
     * Retrieve the <code>ListSelectionModel</code> from the given
     * <code>ListSelectionEvent</code>.
     * If the event is coming from a JList, the source of the event is
     * the JList itself.
     * If the event is coming from a JTable, the source of the event is the
     * ListSelectionModel
     * (talk about consistency)
     * @param e the event
     * @return the <code>ListSelectionModel</code>
     */
    private ListSelectionModel getListSelectionModel(ListSelectionEvent e) {
        if (e.getSource() instanceof JList) {
            // we're coming from a JList
            return ((JList)e.getSource()).getSelectionModel();
        }

        Assert.isTrue(e.getSource() instanceof ListSelectionModel, "Unsupported source in ListSelectionEvent");

        return (ListSelectionModel)e.getSource();
    }

    /**
     * Returns the number of selected items.
     * @return the number of selected items
     */
    protected int getItemsSelected() {
        return itemsSelected;
    }

    /**
     * Called when multiple rows are selected.
     * Override this method to handle multiple selection
     * @param indexes the selected indexes
     */
    protected void onMultiSelection(int[] indexes) {
    }

    /**
     * Called when nothing gets selected.
     * Override this method to handle empty selection
     */
    protected void onNoSelection() {
    }

    /**
     * Called when the user selects a single row.
     * Override this method to handle single selection
     * @param index the selected row
     */
    protected void onSingleSelection(int index) {
    }
}