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

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.support.AbstractBinding;
import org.springframework.richclient.forms.SelectableButtonValueModel;

/**
 * @author Oliver Hutchison
 */
public class CheckBoxBinding extends AbstractBinding {
    
    private JCheckBox checkBox;    
    
    public CheckBoxBinding(FormModel formModel, String formPropertyPath) {
        this(null, formModel, formPropertyPath);
    }
    
    public CheckBoxBinding(JCheckBox checkBox, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath);
        this.checkBox = checkBox;
    }
    
    protected JComponent doCreateAndBindControl() {
        final ValueModel valueModel = getDisplayValueModel();
        if (checkBox == null) {
            checkBox = createCheckBox();
        }
        checkBox.setModel(new SelectableButtonValueModel(valueModel));
        return checkBox;
    }

    protected JCheckBox createCheckBox() {
        JCheckBox checkBox = getComponentFactory().createCheckBox("");
        // TODO: allow for custom FormPropertyFaceDescriptor properties such as
        // the checkBox text. 
        checkBox.setText(getFormPropertyFaceDescriptor().getDescription());
        return checkBox;
    }
    
    protected void readOnlyChanged() {
        checkBox.setEnabled(isEnabled() && ! isReadOnly());    
    }

    protected void enabledChanged() {
        checkBox.setEnabled(isEnabled() && ! isReadOnly());        
    }
}