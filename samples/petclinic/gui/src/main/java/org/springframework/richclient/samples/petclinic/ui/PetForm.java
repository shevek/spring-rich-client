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

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class PetForm extends AbstractForm {
    public static final String PET_FORM_PAGE = "petProperties";

    private JComponent nameField;

    public PetForm(FormModel formModel, boolean canEditType) {
        super(formModel, PET_FORM_PAGE);
        getFormModel().getFieldMetadata("type").setReadOnly(!canEditType);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        formBuilder.add("type");
        formBuilder.row();
        this.nameField = formBuilder.add("name")[1];
        formBuilder.row();
        formBuilder.add("birthDate");
        return formBuilder.getForm();
    }

    public boolean requestFocusInWindow() {
        return nameField.requestFocusInWindow();
    }

}