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
package org.springframework.richclient.forms;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.springframework.binding.value.ValueModel;
import org.springframework.util.Assert;

public class AsYouTypeTextValueSetter extends AbstractValueSetter implements
        DocumentListener {

    private JTextComponent component;
    
    private boolean settingText;

    protected AsYouTypeTextValueSetter(JTextComponent component) {
        this(component, null);
    }

    public AsYouTypeTextValueSetter(JTextComponent component,
            ValueModel valueModel) {
        super(valueModel);
        Assert.notNull(component);
        this.component = component;
        this.component.getDocument().addDocumentListener(this);
    }

    protected void setComponentValue(Object value) {
        // this try block will coalesce the 2 DocumentEvents that
        // JTextComponent.setText() fires into 1 call to 
        // componentValueChanged()
        try {
            settingText = true;
            component.setText((String)value);
        }
        finally {
            settingText = false;
        }
        componentValueChanged();
    }

    public void removeUpdate(DocumentEvent e) {
        componentValueChanged();
    }

    public void insertUpdate(DocumentEvent e) {
        componentValueChanged();
    }

    public void changedUpdate(DocumentEvent e) {
        componentValueChanged();
    }

    private void componentValueChanged() {
        if (!settingText) {
            componentValueChanged(component.getText());
        }
    }
}