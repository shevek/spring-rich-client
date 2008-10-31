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

import java.util.List;

import javax.swing.ListModel;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.command.ActionCommand;

public class ListUtils {

    private ListUtils() {

    }

    public static ActionCommand createRemoveRowCommand(final List list, final ValueModel selectionIndexHolder) {
        ActionCommand removeCommand = new ActionCommand("removeCommand") {
            protected void doExecuteCommand() {
                int selectedRowIndex = ((Integer) selectionIndexHolder.getValue()).intValue();
                list.remove(selectedRowIndex);
            }
        };
        new SingleListSelectionGuard(selectionIndexHolder, removeCommand);
        return removeCommand;
    }

    /**
     * Returns the list model of a filter chain.
     * 
     * @param listModel
     *            the filtered list model chain
     * @return the (unfiltered) list model
     */
    public static ListModel getFilteredListModel(ListModel listModel) {
        if (listModel instanceof AbstractFilteredListModel) {
            return getFilteredListModel(((AbstractFilteredListModel) listModel).getFilteredModel());
        }
        return listModel;
    }

    /**
     * Returns the unfiltered element index from a chained filtered list model
     * 
     * @param listModel
     *            the chained filtered list model
     * @param filteredIndex
     *            the index of the element to return the unfiltered index for
     * @return the element index of the unfiltered list model
     */
    public static int getElementIndex(ListModel listModel, int filteredIndex) {
        if (listModel instanceof AbstractFilteredListModel) {
            AbstractFilteredListModel filteredModel = (AbstractFilteredListModel) listModel;
            return getElementIndex(filteredModel.getFilteredModel(), filteredModel.getElementIndex(filteredIndex));
        }
        return filteredIndex;
    }
}