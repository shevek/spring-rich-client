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
package org.springframework.richclient.samples.petclinic.ui;

import javax.swing.JComponent;

import org.springframework.richclient.application.events.LifecycleApplicationEvent;
import org.springframework.richclient.command.CommandDelegate;
import org.springframework.richclient.dialog.CompoundForm;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.AbstractWizardPage;
import org.springframework.richclient.wizard.WizardDialog;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.util.Assert;

public class NewOwnerWizard extends AbstractWizard implements CommandDelegate {
    private WizardDialog wizardDialog;

    private CompoundForm wizardForm;

    private Clinic clinic;

    private OwnerGeneralPanel ownerGeneralPanel;

    public void setClinic(Clinic clinic) {
        Assert.notNull(clinic);
        this.clinic = clinic;
    }

    public void addPages() {
        addPage(new OwnerGeneralWizardPage());
    }

    public class OwnerGeneralWizardPage extends AbstractWizardPage {
        public static final String ID = "newOwnerWizard.general";

        public OwnerGeneralWizardPage() {
            super(ID);
        }

        public void setVisible(boolean bool) {
            super.setVisible(bool);
            if (bool) {
                ownerGeneralPanel.requestFocusInWindow();
            }
        }

        public void onAboutToShow() {
            setEnabled(!ownerGeneralPanel.hasErrors());
        }

        public JComponent createControl() {
            ownerGeneralPanel = new OwnerGeneralPanel(wizardForm.getFormModel());
            ownerGeneralPanel.newSingleLineResultsReporter(this, this);
            JComponent form = ownerGeneralPanel.getControl();
            return form;
        }

        public void setEnabled(boolean enabled) {
            setPageComplete(enabled);
        }

    }

    public void execute() {
        if (wizardDialog == null) {
            wizardDialog = new WizardDialog(this);
            wizardDialog.setResetMessagePaneOnDisplay(true);
            wizardForm = new CompoundForm(new Owner());
        }
        else {
            wizardForm.setFormObject(new Owner());
        }
        wizardDialog.showDialog();
    }

    protected boolean onFinish() {
        Owner newOwner = (Owner)getNewOwner();
        clinic.storeOwner(newOwner);
        getApplicationContext().publishEvent(
                new LifecycleApplicationEvent(
                        LifecycleApplicationEvent.CREATED, newOwner));
        return true;
    }

    private Owner getNewOwner() {
        wizardForm.commit();
        return (Owner)wizardForm.getFormObject();
    }

}