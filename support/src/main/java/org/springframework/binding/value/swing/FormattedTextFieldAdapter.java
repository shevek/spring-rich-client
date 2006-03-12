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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelAdapter;

/**
 * Sets the value of the value model associated with a formatted text field when
 * the text field changes according to the value commit policy. 
 * 
 * This setter will also update the formatted text field value when the
 * underlying value model value changes.
 * 
 * @author Oliver Hutchison
 * @author Keith Donald
 */
public class FormattedTextFieldAdapter extends AbstractValueModelAdapter implements PropertyChangeListener,
        DocumentListener {

    private final JFormattedTextField component;

    private boolean settingValue;

    public FormattedTextFieldAdapter(JFormattedTextField component, ValueModel valueModel,
            ValueCommitPolicy commitPolicy) {
        super(valueModel);
        this.component = component;
        this.component.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        this.component.addPropertyChangeListener("value", this);
        if (commitPolicy == ValueCommitPolicy.AS_YOU_TYPE) {
            component.getDocument().addDocumentListener(this);
        }
        initalizeAdaptedValue();
    }

    protected void valueModelValueChanged(Object value) {
        settingValue = true;
        try {
            component.setValue(value);
        }
        finally {
            settingValue = false;
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (logger.isDebugEnabled()) {
            Class valueClass = (e.getNewValue() != null ? e.getNewValue().getClass() : null);
            logger.debug("Formatted text field property '" + e.getPropertyName() + "' changed; new value is '"
                    + e.getNewValue() + "', valueClass=" + valueClass);
        }
        adaptedValueChanged(component.getValue());
    }

    public void insertUpdate(DocumentEvent e) {
        tryToCommitEdit();
    }

    public void removeUpdate(DocumentEvent e) {
        tryToCommitEdit();
    }

    public void changedUpdate(DocumentEvent e) {
        tryToCommitEdit();
    }

    private void tryToCommitEdit() {
        if (!settingValue) {
            try {
                component.commitEdit();
            }
            catch (ParseException e) {
                // ignore
            }
        }
    }
}