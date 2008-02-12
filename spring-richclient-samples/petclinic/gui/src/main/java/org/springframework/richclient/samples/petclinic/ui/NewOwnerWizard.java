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

import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.form.CompoundForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.FormBackedWizardPage;
import org.springframework.richclient.wizard.WizardDialog;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.util.Assert;

public class NewOwnerWizard extends AbstractWizard implements ActionCommandExecutor {
    private WizardDialog wizardDialog;

    private CompoundForm wizardForm;

    private Clinic clinic;

    public NewOwnerWizard() {
        super("newOwnerWizard");
    }

    public void setClinic(Clinic clinic) {
        Assert.notNull(clinic, "The clinic property is required");
        this.clinic = clinic;
    }

    public void addPages() {
        addPage(new FormBackedWizardPage(new OwnerGeneralForm(FormModelHelper.createChildPageFormModel(wizardForm.getFormModel()))));
        addPage(new FormBackedWizardPage(new OwnerAddressForm(FormModelHelper.createChildPageFormModel(wizardForm.getFormModel()))));
    }

    public void execute() {
        if (wizardDialog == null) {
            wizardForm = new CompoundForm();
            wizardForm.setFormObject(new Owner());
            wizardDialog = new WizardDialog(this);
        }
        wizardForm.setFormObject(new Owner());
        wizardDialog.showDialog();
    }

    protected boolean onFinish() {
        Owner newOwner = getNewOwner();
        clinic.storeOwner(newOwner);
        getApplicationContext()
                .publishEvent(new LifecycleApplicationEvent(LifecycleApplicationEvent.CREATED, newOwner));
        return true;
    }

    private Owner getNewOwner() {
        wizardForm.commit();
        return (Owner)wizardForm.getFormObject();
    }

}