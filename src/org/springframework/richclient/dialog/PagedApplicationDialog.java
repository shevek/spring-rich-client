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

import javax.swing.JComponent;

import org.springframework.richclient.util.GuiStandardUtils;

/**
 * @author Keith Donald
 */
public abstract class PagedApplicationDialog extends ApplicationDialog {

    private DialogPage dialogPage;

    public void showPage(DialogPage newDialogPage) {
        if (!isControlCreated()) { throw new IllegalStateException(
                "No dialog control has been created.  Call showDialog() first."); }
        if (this.dialogPage != null) {
            getDialogContentPane().remove(this.dialogPage.getControl());
        }
        this.dialogPage = newDialogPage;
        JComponent dialogContentPane = createDialogContentPane();
        dialogContentPane.setBorder(GuiStandardUtils.createStandardBorder());
        getDialogContentPane().add(dialogContentPane);
        update();
    }
    
    protected void addDialogComponents() {
        //getDialogContentPane().add(createDialogPageLabelPane(), BorderLayout.NORTH);
        //DialogPage page = createDialogPage();
        //showPage(page);
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#createDialogContentPane()
     */
    protected final JComponent createDialogContentPane() {
        return dialogPage.getControl();
    }

    protected abstract void update();

}