/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.swing;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.swing.FormattedTextFieldAdapter;
import org.springframework.binding.value.swing.ValueCommitPolicy;
import org.springframework.richclient.form.binding.support.AbstractBinding;

/**
 * TODO: this is probably very broken. Need to do extensive testing.
 *  
 * @author Oliver Hutchison
 */
public class FormattedTextFieldBinding extends AbstractBinding {

    private final JFormattedTextField formattedTextField;

    public FormattedTextFieldBinding(JFormattedTextField formattedTextField, FormModel formModel,
            String formPropertyPath, Class requiredSourceClass) {
        super(formModel, formPropertyPath, requiredSourceClass);
        this.formattedTextField = formattedTextField;
    }

    protected JComponent doBindControl() {
        final ValueModel valueModel = getValueModel();
        formattedTextField.setValue(valueModel.getValue());
        // TODO: implement ValueCommitPolicies
        new FormattedTextFieldAdapter(formattedTextField, valueModel, ValueCommitPolicy.AS_YOU_TYPE);
        return formattedTextField;
    }

    protected void readOnlyChanged() {
        formattedTextField.setEditable(!isReadOnly());
    }

    protected void enabledChanged() {
        formattedTextField.setEnabled(isEnabled());
    }
}