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

import java.text.ParseException;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueModelWrapper;
import org.springframework.richclient.control.PatchedJFormattedTextField;

class ValueModelTextField extends PatchedJFormattedTextField {
    private ValueModel valueModel;

    private boolean committingEdit;

    public ValueModelTextField(ValueModel valueModel,
            AbstractFormatterFactory factory) {
        super();
        this.valueModel = valueModel;
        this.setFormatterFactory(factory);
        this.valueModel.addValueChangeListener(new ValueChangeListener() {
            public void valueChanged() {
                if (!committingEdit) {
                    ValueModelTextField.super.setValue(getValue());
                }
            }
        });
    }

    public Object getValue() {
        if (valueModel instanceof ValueModelWrapper) {
            return ((ValueModelWrapper)valueModel).getWrappedValue();
        }
        else {
            return valueModel.getValue();
        }
    }

    public void setValue(Object value) {
        valueModel.setValue(value);
        super.setValue(value);
    }

    public void commitEdit() throws ParseException {
        committingEdit = true;
        AbstractFormatter format = getFormatter();
        if (format != null) {
            this.valueModel.setValue(format.stringToValue(super.getText()));
        }
        committingEdit = false;
    }
}