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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.text.JTextComponent;

import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;


public class FocusLostTextValueSetter extends FocusAdapter {
    private JTextComponent component;

    private ValueModel valueModel;

    private boolean updating;

    public FocusLostTextValueSetter(JTextComponent component,
            ValueModel valueModel) {
        this.component = component;
        this.valueModel = valueModel;
        this.component.addFocusListener(this);
        valueModel.addValueListener(new ValueListener() {
            public void valueChanged() {
                if (!updating) {
                    FocusLostTextValueSetter.this.component
                            .setText((String)FocusLostTextValueSetter.this.valueModel
                                    .get());
                }
            }
        });
    }

    public void focusLost(FocusEvent e) {
        update();
    }
    
    protected void update() {
        updating = true;
        valueModel.set(component.getText());
        updating = false;
    }
}