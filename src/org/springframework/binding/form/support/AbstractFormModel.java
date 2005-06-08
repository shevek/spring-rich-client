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
package org.springframework.binding.form.support;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.binding.form.FormPropertyFaceDescriptorSource;
import org.springframework.binding.form.FormPropertyState;
import org.springframework.binding.form.NestableFormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.binding.value.support.ValueModelWrapper;
import org.springframework.richclient.application.Application;
import org.springframework.rules.RulesSource;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public abstract class AbstractFormModel extends AbstractPropertyChangePublisher implements NestableFormModel {
    private String id;

    private NestingFormModel parent;

    /* Holds all of the FormPropertyStates for this FormModel */
    private Map propertyStates = new HashMap();

    private RulesSource rulesSource;

    private boolean bufferChanges = true;

    private boolean enabled = true;

    private MutablePropertyAccessStrategy domainObjectAccessStrategy;

    private Set commitListeners;

    private FormPropertyFaceDescriptorSource formPropertyFaceDescriptorSource;
    
    private ConversionService conversionService;

    protected AbstractFormModel() {
    }

    protected AbstractFormModel(MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this.domainObjectAccessStrategy = domainObjectAccessStrategy;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public Object getFormObject() {
        return getPropertyAccessStrategy().getDomainObject();
    }

    public void setFormObject(Object formObject) {
        if (formObject == null) {
            handleSetNullFormObject();
        }
        else {
            getFormObjectHolder().setValue(formObject);
        }
    }

    protected void handleSetNullFormObject() {
        if (logger.isInfoEnabled()) {
            logger.info("New form object value is null; resetting to a new fresh object instance and disabling form");
        }
        reset();
        setEnabled(false);
    }

    public ValueModel getFormObjectHolder() {
        return getPropertyAccessStrategy().getDomainObjectHolder();
    }

    protected Class getFormObjectClass() {
        return getPropertyAccessStrategy().getDomainObject().getClass();
    }

    public NestingFormModel getParent() {
        return parent;
    }

    public void setParent(NestingFormModel parent) {
        this.parent = parent;
    }

    public RulesSource getRulesSource() {
        if (rulesSource == null) {
            rulesSource = Application.services().getRulesSource();
        }
        return rulesSource;
    }

    public void setRulesSource(RulesSource rulesSource) {
        this.rulesSource = rulesSource;
    }

    public boolean getBufferChangesDefault() {
        return bufferChanges;
    }

    public void setBufferChangesDefault(boolean bufferChanges) {
        this.bufferChanges = bufferChanges;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (hasChanged(this.enabled, enabled)) {
            this.enabled = enabled;
            handleEnabledChange();
            firePropertyChange("enabled", !this.enabled, enabled);
        }
    }

    protected void handleEnabledChange() {
    }

    public Object getValue(String formPropertyPath) {
        return getRequiredValueModel(formPropertyPath).getValue();
    }

    public FormPropertyState getFormPropertyState(String formPropertyPath) {
        FormPropertyState propertyState = (FormPropertyState)propertyStates.get(formPropertyPath);
        if (propertyState == null) {
            propertyState = new DefaultFormPropertyMetadata(this, formPropertyPath,
                    !getMetadataAccessStrategy().isWriteable(formPropertyPath));
            propertyStates.put(formPropertyPath, propertyState);
        }
        return propertyState;
    }

    /**
     * Sets the FormPropertyFaceDescriptorSource that this will be used to resolve 
     * FormPropertyFaceDescriptors. 
     * <p>If this value is <code>null</code> the default FormPropertyFaceDescriptorSource from
     * <code>ApplicationServices</code> instance will be used.
     */
    public void setFormPropertyFaceDescriptorSource(
            FormPropertyFaceDescriptorSource formPropertyFaceDescriptorSource) {
        this.formPropertyFaceDescriptorSource = formPropertyFaceDescriptorSource;
    }

    /**
     * Returns the FormPropertyFaceDescriptorSource that should be used to resolve 
     * FormPropertyFaceDescriptors for this form model.   
     */
    protected FormPropertyFaceDescriptorSource getFormPropertyFaceDescriptorSource() {
        if (formPropertyFaceDescriptorSource == null) {
            formPropertyFaceDescriptorSource = Application.services().getFormPropertyFaceDescriptorSource();
        }
        return formPropertyFaceDescriptorSource;
    }

    public FormPropertyFaceDescriptor getFormPropertyFaceDescriptor(String formPropertyPath) {
        return getFormPropertyFaceDescriptorSource().getFormPropertyFaceDescriptor(this, formPropertyPath);
    }

    public void addFormObjectChangeListener(PropertyChangeListener listener) {
        getFormObjectHolder().addValueChangeListener(listener);
    }

    public void removeFormObjectChangeListener(PropertyChangeListener listener) {
        getFormObjectHolder().removeValueChangeListener(listener);
    }

    public void addFormValueChangeListener(String formPropertyPath, PropertyChangeListener listener) {
        getRequiredValueModel(formPropertyPath).addValueChangeListener(listener);
    }

    public void removeFormValueChangeListener(String formPropertyPath, PropertyChangeListener listener) {
        getRequiredValueModel(formPropertyPath).removeValueChangeListener(listener);
    }

    protected ValueModel getRequiredValueModel(String formPropertyPath) {
        ValueModel valueModel = getValueModel(formPropertyPath);
        assertValueModelNotNull(valueModel, formPropertyPath);
        return valueModel;
    }

    protected ValueModel unwrap(ValueModel valueModel) {
        if (valueModel instanceof ValueModelWrapper) {
            return ((ValueModelWrapper)valueModel).getInnerMostWrappedValueModel();
        }
        else {
            return valueModel;
        }
    }

    private void assertValueModelNotNull(ValueModel valueModel, String formProperty) {
        Assert.isTrue(valueModel != null, "The property '" + formProperty
                + "' has not been added to this form model (or to any parents.)");
    }

    public MutablePropertyAccessStrategy getPropertyAccessStrategy() {
        return domainObjectAccessStrategy;
    }

    public void setPropertyAccessStrategy(MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this.domainObjectAccessStrategy = domainObjectAccessStrategy;
    }

    public PropertyMetadataAccessStrategy getMetadataAccessStrategy() {
        return domainObjectAccessStrategy.getMetadataAccessStrategy();
    }

    public void addCommitListener(CommitListener listener) {
        getOrCreateCommitListeners().add(listener);
    }

    public void removeCommitListener(CommitListener listener) {
        getOrCreateCommitListeners().remove(listener);
    }

    private Set getOrCreateCommitListeners() {
        if (this.commitListeners == null) {
            this.commitListeners = new LinkedHashSet(6);
        }
        return commitListeners;
    }

    protected boolean preEditCommit() {
        if (commitListeners == null) {
            return true;
        }
        Object formObject = getFormObject();
        for (Iterator i = commitListeners.iterator(); i.hasNext();) {
            CommitListener l = (CommitListener)i.next();
            if (!l.preEditCommitted(formObject)) {
                return false;
            }
        }
        return true;
    }

    protected void postEditCommit() {
        if (commitListeners == null) {
            return;
        }
        Object formObject = getFormObject();
        for (Iterator i = commitListeners.iterator(); i.hasNext();) {
            CommitListener l = (CommitListener)i.next();
            l.postEditCommitted(formObject);
        }
    }

    public void reset() {
        getFormObjectHolder().setValue(BeanUtils.instantiateClass(getFormObjectClass()));
    }

    public ConversionService getConversionService() {
        if (conversionService == null) {
            conversionService = Application.services().getConversionService();
        }
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
}