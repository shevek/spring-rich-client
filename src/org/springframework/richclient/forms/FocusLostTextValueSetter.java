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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import org.springframework.binding.value.ValueModel;

public class FocusLostTextValueSetter extends AbstractValueSetter implements
        FocusListener {
    private JTextComponent component;

    public FocusLostTextValueSetter(JTextComponent component) {
        this(component, null);
    }

    public FocusLostTextValueSetter(JTextComponent component,
            ValueModel valueModel) {
        super(valueModel);
        this.component = component;
        this.component.addFocusListener(this);
    }

    protected void setComponentValue(Object value) {
        component.setText((String)value);
    }

    public void focusLost(FocusEvent e) {
        componentValueChanged(component.getText());
    }

    public void focusGained(FocusEvent e) {

    }
}