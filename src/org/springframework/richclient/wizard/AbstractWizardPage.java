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
package org.springframework.richclient.wizard;

import java.awt.Image;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.dialog.AbstractDialogPage;
import org.springframework.richclient.dialog.MessageListener;
import org.springframework.util.Assert;
import org.springframework.util.ToStringBuilder;

public abstract class AbstractWizardPage extends AbstractDialogPage implements
        WizardPage, DescriptionConfigurable {
    private static final Log logger = LogFactory
            .getLog(AbstractWizardPage.class);

    private String pageId;

    private Wizard wizard;

    private boolean isPageComplete = true;

    private WizardPage previousPage;

    protected AbstractWizardPage(String pageId) {
        this(pageId, null, (Image)null);
    }

    protected AbstractWizardPage(String pageId, String title, Image titleImage) {
        super(title, titleImage);
        setId(pageId);
        this.addMessageListener(new MessageListener() {
            public void messageUpdated() {
                if (isCurrentPage()) {
                    getContainer().updateMessage();
                }
            }            
        });
    }

    public String getId() {
        return pageId;
    }

    private void setId(String pageId) {
        Assert.hasText(pageId);
        this.pageId = pageId;
    }

    public Image getImage() {
        Image image = super.getImage();
        if (image != null) { return image; }
        return wizard.getDefaultPageImage();
    }

    public WizardPage getNextPage() {
        if (wizard == null) { return null; }
        return wizard.getNextPage(this);
    }

    public WizardPage getPreviousPage() {
        if (previousPage != null) { return previousPage; }
        if (wizard == null) { return null; }
        return wizard.getPreviousPage(this);
    }

    public boolean canFlipToNextPage() {
        return isPageComplete() && getNextPage() != null;
    }

    protected boolean isCurrentPage() {
        return (getContainer() != null && this == getContainer()
                .getCurrentPage());
    }

    protected WizardContainer getContainer() {
        if (wizard == null) { return null; }
        return wizard.getContainer();
    }

    public Wizard getWizard() {
        return wizard;
    }

    public boolean isPageComplete() {
        return isPageComplete;
    }

   public void setDescription(String description) {
        super.setDescription(description);
        if (isCurrentPage()) {
            getContainer().updateTitleBar();
        }
    }

    public void setPageComplete(boolean complete) {
        if (isPageComplete != complete) {
            isPageComplete = complete;
            if (isCurrentPage()) {
                getContainer().updateButtons();
            }
        }
    }

    public void setPreviousPage(WizardPage page) {
        previousPage = page;
    }

    public void setVisible(boolean visible) {
        JComponent control = getControl();
        if (control != null) {
            control.setVisible(visible);
            control.requestFocusInWindow();
        }
    }

    public void setEnabled(boolean enabled) {
        setPageComplete(enabled);
    }

    public void setWizard(Wizard newWizard) {
        this.wizard = newWizard;
    }

    public void onAboutToShow() {

    }

    public String toString() {
        return new ToStringBuilder(this).appendProperties().toString();
    }
}