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
package org.springframework.binding.value.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelAdapter;
import org.springframework.util.Assert;

public class AsYouTypeTextComponentAdapter extends AbstractValueModelAdapter implements DocumentListener {

    private final JTextComponent control;

    private boolean settingText;

    public AsYouTypeTextComponentAdapter(JTextComponent control, ValueModel valueModel) {
        super(valueModel);
        Assert.notNull(control);
        this.control = control;
        this.control.getDocument().addDocumentListener(this);
        initalizeAdaptedValue();
    }

    public void removeUpdate(DocumentEvent e) {
        controlTextValueChanged();
    }

    public void insertUpdate(DocumentEvent e) {
        controlTextValueChanged();
    }

    public void changedUpdate(DocumentEvent e) {
        controlTextValueChanged();
    }

    private void controlTextValueChanged() {
        if (!settingText) {
            adaptedValueChanged(control.getText());
        }
    }

    protected void valueModelValueChanged(Object value) {
        // this try block will coalesce the 2 DocumentEvents that
        // JTextComponent.setText() fires into 1 call to
        // componentValueChanged()
        try {
            settingText = true;
            control.setText((String)value);
        }
        finally {
            settingText = false;
        }
    }

}