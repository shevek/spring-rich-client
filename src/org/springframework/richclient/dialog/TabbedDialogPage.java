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
package org.springframework.richclient.dialog;

import java.util.List;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.springframework.richclient.util.LabelUtils;
import org.springframework.util.Assert;

/**
 * A concrete implementation of <code>CompositeDialogPage</code> that presents
 * the child pages in a <code>JTabbedPane</code>.
 * <p>
 * Each child page is placed into a separate tab of the <code>JTabbedPane</code>.
 * This class also decorates the tab titles to indicate the page completed
 * status.
 * 
 * @author oliverh
 */
public class TabbedDialogPage extends CompositeDialogPage {
    private JTabbedPane tabbedPane;

    public TabbedDialogPage(String pageId) {
        super(pageId);
    }

    protected JComponent createControl() {
        createPageControls();
        tabbedPane = new JTabbedPane();
        List pages = getPages();
        for (int i = 0; i < pages.size(); i++) {
            final DialogPage page = (DialogPage)pages.get(i);
            JComponent control = page.getControl();
            control.setPreferredSize(getLargestPageSize());
            tabbedPane.add(control);
            decorateTabTitle(page);
        }
        tabbedPane.setModel(new DefaultSingleSelectionModel() {
            public void setSelectedIndex(int index) {
                if (index == getSelectedIndex()) {
                    return;
                }
                if (canChangeTabs()) {
                    super.setSelectedIndex(index);
                    if (index >= 0) {
                        TabbedDialogPage.super.setActivePage((DialogPage)getPages().get(index));
                    }
                    else {
                        TabbedDialogPage.super.setActivePage(null);
                    }
                }
            }
        });
        setActivePage((DialogPage)pages.get(0));
        return tabbedPane;
    }

    /**
     * Sets the active page of this TabbedDialogPage. This method will also
     * select the tab wich displays the new active page.
     * 
     * @param activePage
     *            the page to be made active. Must be one of the child pages.
     */
    public void setActivePage(DialogPage page) {
        int pageIndex = page == null ? -1 : getPages().indexOf(page);
        tabbedPane.setSelectedIndex(pageIndex);
    }

    protected boolean canChangeTabs() {
        return true;
    }

    protected void updatePageComplete(DialogPage page) {
        super.updatePageComplete(page);
        decorateTabTitle(page);
    }

    protected void decorateTabTitle(DialogPage page) {
        if (tabbedPane == null) {
            return;
        }
        int pageIndex = getPages().indexOf(page);
        Assert.isTrue(pageIndex != -1);
        String title = LabelUtils.htmlBlock("<center>" + page.getTitle() + "<sup><font size=-3 color=red>"
                + (page.isPageComplete() ? "" : "*"));
        tabbedPane.setTitleAt(pageIndex, title);
    }
}