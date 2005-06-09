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
package org.springframework.binding.form;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.ValueModel;

/**
 * @author Keith Donald
 */
public interface FormModel extends PropertyChangePublisher {
    public static final String ENABLED_PROPERTY = "enabled";

    public static final String DIRTY_PROPERTY = "dirty";
    
    /**
     * Returns the ID of this form model (may be empty or <code>null</code>).
     */
    public String getId();

    public Object getFormObject();

    public void setFormObject(Object formObject);

    public ValueModel getFormObjectHolder();

    public ValueModel getValueModel(String formPropertyPath);
        
    /**
     * Returns a type converting value model for the given form property path. The 
     * type of the value returned from the provided value model is guaranteed to 
     * be of class targetClass.
     * @throws IllegalArgumentException if no suitable converter for the targetClass
     * can be found
     */
    public ValueModel getValueModel(String formPropertyPath, Class targetClass);

    public Object getValue(String formPropertyPath);

    public Map getErrors();
    
    /**
     * Returns the FormPropertyState for the specified formPropertyPath.
     */
    public FormPropertyState getFormPropertyState(String formPropertyPath);
    
    /**
     * Returns the FormPropertyFaceDescriptor for the specified formPropertyPath.
     */
    public FormPropertyFaceDescriptor getFormPropertyFaceDescriptor(String formPropertyPath);

    public boolean getHasErrors();

    public boolean getBufferChangesDefault();

    public boolean isDirty();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public void commit();

    public void revert();

    public void reset();

    public void addFormObjectChangeListener(PropertyChangeListener listener);

    public void removeFormObjectChangeListener(PropertyChangeListener listener);

    public void addFormValueChangeListener(String formPropertyPath, PropertyChangeListener listener);

    public void removeFormValueChangeListener(String formPropertyPath, PropertyChangeListener listener);

    public void addValidationListener(ValidationListener listener);

    public void removeValidationListener(ValidationListener listener);

    public void addCommitListener(CommitListener listener);

    public void removeCommitListener(CommitListener listener);

}