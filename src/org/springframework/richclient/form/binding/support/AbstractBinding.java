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

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.binding.form.PropertyMetadata;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

/**
 * Default base implementation of <code>Binding</code>. Provides helper methods for 
 * access to commonly needed properties. 
 * 
 * @author Oliver Hutchison
 */
public abstract class AbstractBinding extends AbstractControlFactory implements Binding {

    protected final FormModel formModel;

    protected final String formPropertyPath;

    protected final PropertyMetadata propertyMetadata;

    private final Class requiredSourceClass;

    protected AbstractBinding(FormModel formModel, String formPropertyPath, Class requiredSourceClass) {
        this.formModel = formModel;
        this.formPropertyPath = formPropertyPath;
        this.propertyMetadata = this.formModel.getPropertyMetadata(formPropertyPath);
        this.requiredSourceClass = requiredSourceClass;
        PropertyMetadataHandler propertyMetadataHandler = new PropertyMetadataHandler();
        this.propertyMetadata.addPropertyChangeListener(PropertyMetadata.ENABLED_PROPERTY, propertyMetadataHandler);
        this.propertyMetadata.addPropertyChangeListener(PropertyMetadata.READ_ONLY_PROPERTY, propertyMetadataHandler);
    }

    public String getProperty() {
        return formPropertyPath;
    }

    public FormModel getFormModel() {
        return formModel;
    }

    protected FormPropertyFaceDescriptor getFormPropertyFaceDescriptor() {
        return formModel.getFormPropertyFaceDescriptor(formPropertyPath);
    }

    protected Class getPropertyType() {
        return propertyMetadata.getPropertyType();
    }

    protected JComponent createControl() {
        JComponent control = doBindControl();
        control.setName(getProperty());
        readOnlyChanged();
        enabledChanged();
        return control;
    }

    protected abstract JComponent doBindControl();

    /**
     * Called when the read only state of the bound property changes.
     * @see FormPropertyState
     */
    protected abstract void readOnlyChanged();

    /**
     * Called when the enabled state of the bound property changes.
     * @see FormPropertyState
     */
    protected abstract void enabledChanged();

    /**
     * Is the bound property in the read only state.
     * @see FormPropertyState
     */
    protected boolean isReadOnly() {
        return propertyMetadata.isReadOnly();
    }

    /**
     * Is the bound property in the enabled state.
     * @see FormPropertyState
     */
    protected boolean isEnabled() {
        return propertyMetadata.isEnabled();
    }

    protected ValueModel getValueModel() {
        ValueModel valueModel = (requiredSourceClass == null) ? formModel.getValueModel(formPropertyPath)
                : formModel.getValueModel(formPropertyPath, requiredSourceClass);
        Assert.notNull(valueModel, "Unable to locate value model for property '" + formPropertyPath + "'.");
        return valueModel;
    }

    protected Object getValue() {
        return getValueModel().getValue();
    }


    private class PropertyMetadataHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (PropertyMetadata.ENABLED_PROPERTY.equals(evt.getPropertyName())) {
                enabledChanged();
            }
            else if (PropertyMetadata.READ_ONLY_PROPERTY.equals(evt.getPropertyName())) {
                readOnlyChanged();
            }
        }
    }
}
