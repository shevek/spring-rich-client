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

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.util.Assert;

/**
 * A TitledApplicationDialog that delegates to a single DialogPage for its
 * title, content and messages.
 * 
 * @author oliverh
 */
public abstract class TitledPageApplicationDialog extends
        TitledApplicationDialog implements PropertyChangeListener {

    private DialogPage dialogPage;

    public TitledPageApplicationDialog(DialogPage dialogPage) {
        super();
        setDialogPage(dialogPage);
    }

    public TitledPageApplicationDialog(DialogPage dialogPage, Window parent) {
        super(dialogPage.getTitle(), parent);
        setDialogPage(dialogPage);
    }

    public TitledPageApplicationDialog(DialogPage dialogPage, Window parent,
            CloseAction closeAction) {
        super(dialogPage.getTitle(), parent, closeAction);
        setDialogPage(dialogPage);
    }

    protected DialogPage getDialogPage() {
        return dialogPage;
    }

    private void setDialogPage(DialogPage dialogPage) {
        this.dialogPage = dialogPage;
    }

    protected JComponent createTitledDialogContentPane() {
        Assert.notNull(dialogPage);
        dialogPage.addMessageListener(new MessageListener() {
            public void messageUpdated(MessageReceiver source) {
                update();
            }
        });
        dialogPage.addPropertyChangeListener(this);
        update();
        return dialogPage.getControl();
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (DialogPage.PAGE_COMPLETE_PROPERTY.equals(e.getPropertyName())) {
            setEnabled(dialogPage.isPageComplete());
        }
        else {
            update();
        }
    }

    public String getDescription() {
        return dialogPage.getDescription();
    }

    protected void update() {
        setTitle(dialogPage.getTitle());
        updateTitleBar();
        updateMessage();
    }

    protected void updateTitleBar() {
        setTitleAreaText(dialogPage.getTitle());
        setTitleAreaImage(dialogPage.getImage());
    }

    protected void updateMessage() {
        setMessage(dialogPage.getMessage(), dialogPage.getSeverity());
    }
}