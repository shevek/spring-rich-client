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

import javax.swing.JComponent;

import org.springframework.richclient.forms.AbstractFormPage;

/**
 * An implementation of WizardPage that delegates to an AbstractFormPage for its
 * control, pageComplete status and messages.
 * 
 * @author oliverh
 */
public class FormPageBackedWizardPage extends AbstractWizardPage {
    AbstractFormPage backingFormPage;

    /**
     * Creates a new FormPageBackedWizardPage. 
     * 
     * @param pageId
     *            the id of this wizard page. This will be used to configure
     *            page titles/description
     * @param backingFormPage
     *            the AbstractFormPage which will provide the control for this
     *            wizard page.
     */
    public FormPageBackedWizardPage(String pageId,
            AbstractFormPage backingFormPage) {
        super(pageId);
        this.backingFormPage = backingFormPage;
    }
    
    protected AbstractFormPage getBackingFormPage() {
        return backingFormPage;
    }

    public void onAboutToShow() {
        setEnabled(!backingFormPage.hasErrors());
    }

    public JComponent createControl() {
        backingFormPage.newSingleLineResultsReporter(this, this);
        return backingFormPage.getControl();
    }

    public void setEnabled(boolean enabled) {
        setPageComplete(enabled);
    }
}