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
package org.springframework.richclient.samples.petclinic.ui.preference;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.preference.FormBackedPreferencePage;

public class AppearancePreferencePage extends FormBackedPreferencePage {

    private PetClinicAppearance appearance = new PetClinicAppearance();

    public AppearancePreferencePage() {
        super("appearancePreferencePage");
    }

    protected Form createForm() {
        // load the stored prefs in the PetClinicAppearance instance
        appearance.load(getPreferenceStore());

        // create formmodel and form
        FormModel formModel = FormModelHelper.createFormModel(appearance);
        AppearancePreferenceForm form = new AppearancePreferenceForm(formModel);
        return form;
    }

    public void onDefaults() {
        // put the default value back in the form
        getForm().getValueModel("dialogPageType").setValue(CompositeDialogPageType.TREE);
    }

    public boolean onFinish() {
        // first commit the form, so the changes are copied to the appearance
        // instance
        getForm().commit();

        // store the preferences into the PreferenceStore
        getPreferenceStore().setValue(PetClinicAppearance.DIALOG_PAGE_TYPE, appearance.getDialogPageType());
        return super.onFinish();
    }

    /**
     * Form used in PreferencePage
     */
    public class AppearancePreferenceForm extends AbstractForm {

        public AppearancePreferenceForm(FormModel formModel) {
            super(formModel, "appearancePreferenceForm");
        }

        protected JComponent createFormControl() {
            TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
            formBuilder.add("dialogPageType");
            return formBuilder.getForm();
        }

    }
}