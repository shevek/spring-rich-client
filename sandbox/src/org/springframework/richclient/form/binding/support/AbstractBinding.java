/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.binding.form.FormPropertyState;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.binding.Binding;

/**
 * @author Oliver Hutchison
 */
public abstract class AbstractBinding extends AbstractControlFactory implements Binding {

    protected final FormModel formModel;

    protected final String formPropertyPath;
    
    protected final FormPropertyState formPropertyState;

    protected AbstractBinding(FormModel formModel, String formPropertyPath) {
        this.formModel = formModel;        
        this.formPropertyPath = formPropertyPath;
        this.formPropertyState = this.formModel.getFormPropertyState(formPropertyPath);        
        FormPropertyValueModelListener listener = new FormPropertyValueModelListener();
        this.formPropertyState.addPropertyChangeListener(FormPropertyState.ENABLED_PROPERTY, listener);
        this.formPropertyState.addPropertyChangeListener(FormPropertyState.READ_ONLY_PROPERTY, listener);
    }

    public String getProperty() {
        return formPropertyPath;
    }

    public FormModel getFormModel() {
        return formModel;
    }

    protected boolean isReadOnly() {
        return formPropertyState.isReadOnly();
    }

    protected boolean isEnabled() {
        return formPropertyState.isEnabled();
    }
    
    protected FormPropertyFaceDescriptor getFormPropertyFaceDescriptor() {
        return formModel.getFormPropertyFaceDescriptor(formPropertyPath);
    }
    
    protected Class getPropertyType() {
        return getPropertyMetadataAccessStrategy().getPropertyType(formPropertyPath);
    }
    
    protected JComponent createControl() {
        JComponent control = doCreateAndBindControl();
        control.setName(getProperty());
        readOnlyChanged();
        enabledChanged();
        return control;
    }
    
    protected abstract JComponent doCreateAndBindControl();

    protected abstract void readOnlyChanged();
    
    protected abstract void enabledChanged();
    
    protected ValueModel getDisplayValueModel() {
        return formModel.getDisplayValueModel(formPropertyPath);
    }

    protected ComponentFactory getComponentFactory() {
        return Application.services().getComponentFactory();
    }
    
    private PropertyMetadataAccessStrategy getPropertyMetadataAccessStrategy() {
        return ((ConfigurableFormModel)formModel).getMetadataAccessStrategy();
    }
    
    private class FormPropertyValueModelListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (FormPropertyState.ENABLED_PROPERTY.equals(evt.getPropertyName())) {
                enabledChanged();
            } else if (FormPropertyState.READ_ONLY_PROPERTY.equals(evt.getPropertyName())) {
                readOnlyChanged();
            }
        }        
    }
}
