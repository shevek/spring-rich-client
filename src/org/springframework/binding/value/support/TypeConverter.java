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
package org.springframework.binding.value.support;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.text.ParseException;

import javax.swing.JFormattedTextField;

import org.springframework.binding.value.ValueModel;
import org.springframework.core.closure.Closure;

/**
 * @author Keith Donald
 */
public class TypeConverter extends AbstractValueModelWrapper {
    private Closure convertFrom;

    private Closure convertTo;

    public TypeConverter(ValueModel wrappedModel, Closure convertTo, Closure convertFrom) {
        super(wrappedModel);
        this.convertFrom = convertTo;
        this.convertTo = convertFrom;
    }

    public TypeConverter(ValueModel wrappedModel, final PropertyEditor propertyEditor) {
        super(wrappedModel);
        this.convertFrom = new Closure() {
            public Object call(Object o) {
                propertyEditor.setValue(o);
                return propertyEditor.getAsText();
            }
        };
        this.convertTo = new Closure() {
            public Object call(Object o) {
                if (o instanceof String) {
                    propertyEditor.setAsText((String)o);
                    return propertyEditor.getValue();
                }
                else {
                    return o;
                }
            }
        };
    }

    public TypeConverter(ValueModel wrappedModel, final JFormattedTextField textField) {
        super(wrappedModel);
        this.convertFrom = new Closure() {
            public Object call(Object o) {
                try {
                    return textField.getFormatter().valueToString(o);
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        };
        this.convertTo = new Closure() {
            public Object call(Object o) {
                try {
                    textField.commitEdit();
                    return textField.getValue();
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        };
    }

    public Object getValue() throws IllegalArgumentException {
        return convertFrom.call(super.getValue());
    }

    public void setValue(Object value) throws IllegalArgumentException {
        super.setValue(convertTo.call(value));
    }

    public void setValueSilently(Object value, PropertyChangeListener listenerToSkip) throws IllegalArgumentException {
        super.setValueSilently(convertTo.call(value), listenerToSkip);
    }
}