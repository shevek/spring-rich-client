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

import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;


public class AsYouTypeTextValueSetter implements DocumentListener {
    private JTextComponent component;

    private ValueModel valueModel;

    private boolean updating;

    protected AsYouTypeTextValueSetter(JTextComponent component) {
        this.component = component;
        this.component.getDocument().addDocumentListener(this);
    }
    
    public AsYouTypeTextValueSetter(JTextComponent component,
            ValueModel valueModel) {
        this.component = component;
        this.component.getDocument().addDocumentListener(this);
        this.valueModel = valueModel;
        valueModel.addValueListener(new ValueListener() {
            public void valueChanged() {
                if (!updating) {
                    AsYouTypeTextValueSetter.this.component
                            .setText((String)AsYouTypeTextValueSetter.this.valueModel
                                    .get());
                }
            }
        });
    }

    protected String getText() {
        return component.getText();
    }
    
    public void removeUpdate(DocumentEvent e) {
        update();
    }

    public void insertUpdate(DocumentEvent e) {
        update();
    }

    public void changedUpdate(DocumentEvent e) {
        update();
    }

    protected void update() {
        updating = true;
        valueModel.set(getText());
        updating = false;
    }

}