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

import javax.swing.event.ListSelectionListener;

import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.table.ListSelectionListenerSupport;
import org.springframework.util.Assert;

/**
 * <code>ListSelectionListenerSupport</code> implementation that updates the statusbar
 * of the application. The <code>getSelectedObjectName</code> must return the string
 * representation of the selected object.
 * <br>
 * Usage:
 * <pre>
 * JTable table = ...
 * 
 * ListStatusBarUpdater updater = new ListStatusBarUpdater(getStatusBar()) {
 *     protected String getSelectedObjectName() {
 *         // return the selected Object's name
 *     }
 * };
 * 
 * table.getSelectionModel().addListSelectionListener(updater);
 * </pre>
 * @author peter.de.bruycker
 */
public abstract class ListStatusBarUpdater extends ListSelectionListenerSupport implements ListSelectionListener {

    private StatusBar statusBar;

    /**
     * Constructs a new <code>TableStatusBarUpdater</code> instance.
     * @param table the table
     * @param statusBar the status bar
     */
    public ListStatusBarUpdater(StatusBar statusBar) {
        Assert.notNull(statusBar);
        this.statusBar = statusBar;
    }

    /**
     * Returns the string representation of the selected object.
     * @return the string representation
     */
    protected abstract String getSelectedObjectName();

    /**
     * Method getStatusBar.
     * @return the status bar
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * @see org.springframework.richclient.table.TableSelectionListenerSupport#onSingleSelection(int)
     */
    protected void onSingleSelection(int index) {
        updateStatusBar(getSelectedObjectName());
    }

    /**
     * @see org.springframework.richclient.table.TableSelectionListenerSupport#onMultiSelection(int[])
     */
    protected void onMultiSelection(int[] indexes) {
        updateStatusBar(getItemsSelected());
    }

    /**
     * @see org.springframework.richclient.table.TableSelectionListenerSupport#onNoSelection()
     */
    protected void onNoSelection() {
        updateStatusBar(null);
    }

    private void updateStatusBar(int itemsSelected) {
        // TODO i18n this message
        getStatusBar().setMessage(itemsSelected + " items selected");
    }

    private void updateStatusBar(String selectedObjectName) {
        getStatusBar().setMessage(selectedObjectName);
    }

}