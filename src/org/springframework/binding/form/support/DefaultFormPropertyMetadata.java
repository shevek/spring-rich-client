/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyState;
import org.springframework.binding.form.NestableFormModel;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;

/**
 * Default implementation of FormPropertyState.
 * 
 * @author Oliver Hutchison
 */
public class DefaultFormPropertyMetadata extends AbstractPropertyChangePublisher implements FormPropertyState {

    private static final FormPropertyState NULL_FORM_PROPERTY_META_DATA = new NullFormPropertyMetadata();

    private final FormModel formModel;

    private final FormPropertyState parent;

    private boolean forceReadOnly;

    private boolean oldReadOnly;

    private boolean readOnly;

    private boolean oldEnabled;

    private boolean enabled = true;

    /**
     * Constructs a new instance of DefaultFormPropertyMetadata. 
     * 
     * @param formModel the form model 
     * @param property the property this metadata is for
     * @param forceReadOnly should readOnly be forced to true; this is required if the 
     * property can not be modified. e.g. at the PropertyAccessStrategy level.
     */
    public DefaultFormPropertyMetadata(NestableFormModel formModel, String property, boolean forceReadOnly) {
        this.formModel = formModel;
        this.forceReadOnly = forceReadOnly;
        if (formModel.getParent() == null) {
            this.parent = NULL_FORM_PROPERTY_META_DATA;
        }
        else {
            this.parent = formModel.getParent().getFormPropertyState(property);
        }
        final PropertyChangeListener listener = new FormModelAndParentChangeListener();
        this.formModel.addPropertyChangeListener(ENABLED_PROPERTY, listener);
        this.parent.addPropertyChangeListener(READ_ONLY_PROPERTY, listener);
        this.parent.addPropertyChangeListener(ENABLED_PROPERTY, listener);
        this.oldReadOnly = isReadOnly();
        this.oldEnabled = isEnabled();
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        firePropertyChange(READ_ONLY_PROPERTY, oldReadOnly, isReadOnly());
        oldReadOnly = isReadOnly();
    }

    public boolean isReadOnly() {
        return forceReadOnly || readOnly || parent.isReadOnly();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        firePropertyChange(ENABLED_PROPERTY, oldEnabled, isEnabled());
        oldEnabled = isEnabled();
    }

    public boolean isEnabled() {
        return enabled && formModel.isEnabled() && parent.isEnabled();
    }

    /**
     * This class is responsible for listening for changes to the enabled and readOnly
     * properties of the parent FormPropertyState and the FormModel
     */
    private class FormModelAndParentChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (ENABLED_PROPERTY.equals(evt.getPropertyName())) {
                firePropertyChange(ENABLED_PROPERTY, Boolean.valueOf(oldEnabled), Boolean.valueOf(isEnabled()));
                oldEnabled = isEnabled();
            }
            else if (READ_ONLY_PROPERTY.equals(evt.getPropertyName())) {
                firePropertyChange(READ_ONLY_PROPERTY, Boolean.valueOf(oldReadOnly), Boolean.valueOf(isReadOnly()));
                oldReadOnly = isReadOnly();
            }
        }
    }

    /**
     * A NULL implementation of FormPropertyState. Property is always enabled 
     * and never read only.
     */
    private static final class NullFormPropertyMetadata implements FormPropertyState {
        public void setReadOnly(boolean readOnly) {
        }

        public boolean isReadOnly() {
            return false;
        }

        public void setEnabled(boolean enabled) {
        }

        public boolean isEnabled() {
            return true;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        }
    }
}