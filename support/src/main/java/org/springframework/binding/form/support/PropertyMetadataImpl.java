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
import org.springframework.binding.form.PropertyMetadata;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.binding.value.support.DirtyTrackingValueModel;

/**
 * Default implementation of PropertyMetadata. 
 * <p>
 * NOTE: This is a framework internal class and should not be
 * instantiated in user code. 
 * 
 * @author Oliver Hutchison
 */
public class PropertyMetadataImpl extends AbstractPropertyChangePublisher implements PropertyMetadata {

    private final FormModel formModel;

    private final DirtyTrackingValueModel valueModel;

    private final Class propertyType;

    private final boolean forceReadOnly;

    private boolean oldReadOnly;

    private boolean readOnly;

    private boolean enabled = true;

    private boolean oldEnabled = true;
    
    private final DirtyChangeHandler dirtyChangeHandler = new DirtyChangeHandler();

    private final PropertyChangeListener formChangeHandler = new FormModelChangeHandler();

    /**
     * Constructs a new instance of DefaultFormPropertyMetadata. 
     * 
     * @param formModel the form model 
     * @param valueModel the value model for the property  
     * @param propertyType the type of the property
     * @param forceReadOnly should readOnly be forced to true; this is required if the 
     * property can not be modified. e.g. at the PropertyAccessStrategy level.
     */
    public PropertyMetadataImpl(FormModel formModel, DirtyTrackingValueModel valueModel, Class propertyType, boolean forceReadOnly) {
        this.formModel = formModel;
        this.valueModel = valueModel;
        this.valueModel.addPropertyChangeListener(DirtyTrackingValueModel.DIRTY_PROPERTY, dirtyChangeHandler);
        this.propertyType = propertyType;
        this.forceReadOnly = forceReadOnly;
        this.formModel.addPropertyChangeListener(ENABLED_PROPERTY, formChangeHandler);        
        this.oldReadOnly = isReadOnly();
        this.oldEnabled = isEnabled();
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        firePropertyChange(READ_ONLY_PROPERTY, oldReadOnly, isReadOnly());
        oldReadOnly = isReadOnly();
    }

    public boolean isReadOnly() {
        return forceReadOnly || readOnly;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        firePropertyChange(ENABLED_PROPERTY, oldEnabled, isEnabled());
        oldEnabled = isEnabled();
    }

    public boolean isEnabled() {
        return enabled && formModel.isEnabled();
    }

    public boolean isDirty() {
        return valueModel.isDirty();
    }

    public Class getPropertyType() {
        return propertyType;
    }

    public Object getUserMetadata(String key) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Propagates dirty changes from the value model on to 
     * the dirty change listeners attached to this class.
     */
    private class DirtyChangeHandler extends CommitListenerAdapter implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            firePropertyChange(DIRTY_PROPERTY, evt.getOldValue(), evt.getNewValue());
        }
    }

    /**
     * Responsible for listening for changes to the enabled 
     * property of the FormModel
     */
    private class FormModelChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (FormModel.ENABLED_PROPERTY.equals(evt.getPropertyName())) {
                firePropertyChange(ENABLED_PROPERTY, Boolean.valueOf(oldEnabled), Boolean.valueOf(isEnabled()));
                oldEnabled = isEnabled();
            }
        }
    }
}