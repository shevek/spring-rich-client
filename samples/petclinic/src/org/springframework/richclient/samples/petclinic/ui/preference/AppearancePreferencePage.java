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
import org.springframework.richclient.forms.AbstractForm;
import org.springframework.richclient.forms.BeanFormBuilder;
import org.springframework.richclient.forms.Form;
import org.springframework.richclient.forms.JGoodiesBeanFormBuilder;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.richclient.preference.FormBackedPreferencePage;

import com.jgoodies.forms.layout.FormLayout;

public class AppearancePreferencePage extends FormBackedPreferencePage {

    private PetClinicAppearance appearance = new PetClinicAppearance();

    public AppearancePreferencePage() {
        super("appearancePreferencePage");
    }

    protected Form createForm() {
        // load the stored prefs in the PetClinicAppearance instance
        appearance.load(getPreferenceStore());

        // create formmodel and form
        FormModel formModel = SwingFormModel.createFormModel(appearance);
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
            FormLayout layout = new FormLayout("left:pref, 5dlu, pref:grow");
            BeanFormBuilder formBuilder = new JGoodiesBeanFormBuilder(getFormModel(), layout);
            formBuilder.add("dialogPageType");
            return formBuilder.getForm();
        }

    }
}