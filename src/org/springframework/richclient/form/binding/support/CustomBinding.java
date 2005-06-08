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
package org.springframework.richclient.form.binding.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.binding.form.FormModel;

/**
 * A helper implementation for binding to custom controls.  
 * 
 * @author Oliver Hutchison
 */
public abstract class CustomBinding extends AbstractBinding {

    private final ValueModelChangeHandler valueModelChangeHandler;

    protected CustomBinding(FormModel formModel, String formPropertyPath, Class requiredSourceClass) {
        super(formModel, formPropertyPath, requiredSourceClass);
        valueModelChangeHandler = new ValueModelChangeHandler();
        getValueModel().addValueChangeListener(valueModelChangeHandler);
    }

    /**
     * Called when the underlying property's value model value changes. 
     */
    protected abstract void valueModelChanged(Object newValue);
    
    /**
     * Should be called when the bound component's value changes. 
     */
    protected final void controlValueChanged(Object newValue) {
        getValueModel().setValueSilently(newValue, valueModelChangeHandler);
    }

    private class ValueModelChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            valueModelChanged(getValue());
        }
    }  
}