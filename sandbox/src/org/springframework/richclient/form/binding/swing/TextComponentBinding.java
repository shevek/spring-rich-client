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
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.support.AbstractBinding;
import org.springframework.richclient.forms.AsYouTypeTextValueSetter;

/**
 * @author Oliver Hutchison
 */
public class TextComponentBinding extends AbstractBinding  {

    private JTextComponent textComponent;
    

    public TextComponentBinding(FormModel formModel, String formPropertyPath) {
        this(null, formModel, formPropertyPath);
    }
    
    public TextComponentBinding(JTextComponent textComponent, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath);
        this.textComponent = textComponent; 
    }

    protected JComponent doCreateAndBindControl() {
        if (textComponent == null) {
            textComponent = createTextComponent();
        }
        final ValueModel valueModel = getDisplayValueModel();
        try {
            textComponent.setText((String) valueModel.getValue());
        }
        catch (ClassCastException e) {
            IllegalArgumentException ex = new IllegalArgumentException("Class cast exception converting '"
                    + getProperty() + "' property value to string - did you install a type converter?");
            ex.initCause(e);
            throw ex;
        }
        // TODO: implement ValueCommitPolicies
        new AsYouTypeTextValueSetter(textComponent, valueModel);    
        return textComponent;
    }

    protected JTextComponent createTextComponent() {
        return getComponentFactory().createTextField();
    }

    protected void readOnlyChanged() {
        textComponent.setEditable(! isReadOnly());
    }

    protected void enabledChanged() {
        textComponent.setEnabled(isEnabled());
    }
}