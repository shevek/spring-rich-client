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
package org.springframework.richclient.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class TreeSelectionListenerSupport implements TreeSelectionListener {
    private static final TreePath[] EMPTY_TREE_PATH_ARRAY = new TreePath[0];

    private int itemsSelected = 0;

    protected int getItemsSelected() {
        return itemsSelected;
    }

    public void valueChanged(TreeSelectionEvent e) {
        TreePath[] paths = e.getPaths();
        List addedPaths = new ArrayList();
        List removedPaths = new ArrayList();
        for (int i = 0; i < paths.length; i++) {
            if (e.isAddedPath(i)) {
                itemsSelected++;
                addedPaths.add(paths[i]);
            }
            else {
                itemsSelected--;
                removedPaths.add(paths[i]);
            }
        }
        if (itemsSelected == 1) {
            onSingleSelection(e.getNewLeadSelectionPath());
        }
        else if (itemsSelected == 0) {
            onNoSelection((TreePath[])removedPaths.toArray(EMPTY_TREE_PATH_ARRAY));
        }
        else {
            onMultiSelection((TreePath[])addedPaths.toArray(EMPTY_TREE_PATH_ARRAY), (TreePath[])removedPaths
                    .toArray(EMPTY_TREE_PATH_ARRAY));
        }
    }

    protected void onSingleSelection(TreePath selectedPath) {

    }

    protected void onNoSelection(TreePath[] removedPaths) {
        onNoSelection();
    }

    protected void onNoSelection() {

    }

    protected void onMultiSelection(TreePath[] addedPaths, TreePath[] removedPaths) {
        onMultiSelection(addedPaths);
    }

    protected void onMultiSelection(TreePath[] addedPaths) {

    }

}