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
package org.springframework.richclient.progress;

import javax.swing.tree.TreePath;

import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.tree.TreeSelectionListenerSupport;
import org.springframework.util.Assert;

public abstract class TreeStatusBarUpdater extends TreeSelectionListenerSupport {
    private StatusBar statusBar;

    public TreeStatusBarUpdater(StatusBar statusBar) {
        Assert.notNull(statusBar);
        this.statusBar = statusBar;
    }

    private StatusBar getStatusBar() {
        return statusBar;
    }

    protected void onSingleSelection(TreePath newPath) {
        updateStatusBar(getSelectedObjectName());
    }

    protected abstract String getSelectedObjectName();

    protected void onMultiSelection(TreePath[] newPaths) {
        updateStatusBar(getItemsSelected());
    }

    protected void onNoSelection() {
        updateStatusBar(null);
    }

    private void updateStatusBar(int itemsSelected) {
        getStatusBar().setMessage(itemsSelected + " items selected");
    }

    private void updateStatusBar(String selectedObjectName) {
        if (selectedObjectName != null) {
            getStatusBar().setMessage(selectedObjectName);
        }
        else {
            getStatusBar().setMessage((String)null);
        }
    }
}