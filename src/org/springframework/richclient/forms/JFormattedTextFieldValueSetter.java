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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;

import org.springframework.binding.value.ValueModel;

/**
 * Sets the value of the value model associated with a formatted text field when
 * the text field changes according to the value commit policy. The set value
 * model is expected to use the text field's formatter to convert the edited
 * text to the correct type of value, as well as to actually commit the edit to
 * the text fields own backing value model (a bit of a hack, but formatted text
 * field is not very easy to work with.)
 * 
 * This setter will also update the formatted text field value when the
 * underlying value model value changes.
 * 
 * @author Oliver Hutchison
 * @author Keith Donald
 */
public class JFormattedTextFieldValueSetter extends AbstractValueSetter implements PropertyChangeListener {
    private JFormattedTextField component;

    private AbstractValueSetter setter;

    public JFormattedTextFieldValueSetter(JFormattedTextField component, ValueModel valueModel,
            ValueCommitPolicy commitPolicy) {
        super(valueModel);
        this.component = component;
        this.component.setFocusLostBehavior(JFormattedTextField.PERSIST);
        if (this.component.isEditable()) {
            if (commitPolicy == ValueCommitPolicy.AS_YOU_TYPE) {
                this.setter = new AsYouTypeTextValueSetter(this.component) {
                    public void componentValueChanged(Object newValue) {
                        JFormattedTextFieldValueSetter.this.componentValueChanged(newValue);
                    }
                };
            }
            else if (commitPolicy == ValueCommitPolicy.FOCUS_LOST) {
                this.setter = new FocusLostTextValueSetter(this.component) {
                    public void componentValueChanged(Object newValue) {
                        JFormattedTextFieldValueSetter.this.componentValueChanged(newValue);
                    }
                };
            }
        }
    }

    public void valueChanged() {
        setControlValue(getInnerMostValue());
    }

    protected void setControlValue(Object value) {
        component.setValue(value);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (logger.isDebugEnabled()) {
            Class valueClass = (e.getNewValue() != null ? e.getNewValue().getClass() : null);
            logger.debug("Formatted text field property '" + e.getPropertyName() + "' changed; new value is '"
                    + e.getNewValue() + "', valueClass=" + valueClass);
        }
        componentValueChanged(component.getValue());
    }

}