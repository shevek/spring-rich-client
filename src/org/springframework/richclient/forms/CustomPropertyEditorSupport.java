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

import java.awt.Component;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.springframework.util.ObjectUtils;

/**
 * @author keith
 */
public abstract class CustomPropertyEditorSupport extends PropertyEditorSupport {

    public void setValue(Object value) {
        Object old = getValue();
        if (!ObjectUtils.nullSafeEquals(old, value)) {
            super.setValue(value);
        }
    }

    public final Component getCustomEditor() {
        return createCustomEditorControl();
    }

    protected abstract JComponent createCustomEditorControl();

    public boolean supportsCustomEditor() {
        return true;
    }

    public JComponent bind(JTextComponent component,
            PropertyEditor customPropertyEditor) {
        return bind(component, customPropertyEditor,
                ValueCommitPolicy.AS_YOU_TYPE);
    }

    public JComponent bind(JTextComponent component,
            final PropertyEditor customPropertyEditor, ValueCommitPolicy policy) {
        if (policy == ValueCommitPolicy.AS_YOU_TYPE) {
            new AsYouTypeTextValueSetter(component) {
                protected void componentValueChanged(Object newValue) {
                    customPropertyEditor.setAsText((String)newValue);
                }
            };
        }
        return component;
    }

}