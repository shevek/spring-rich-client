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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.NestableFormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.value.BoundValueModel;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.rules.RulesSource;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public abstract class AbstractFormModel extends AbstractPropertyChangePublisher
        implements NestableFormModel {

    protected final Log logger = LogFactory.getLog(getClass());

    private NestingFormModel parent;

    private RulesSource rulesSource;

    private boolean bufferChanges = true;

    private boolean enabled = true;

    private MutablePropertyAccessStrategy domainObjectAccessStrategy;

    private Set commitListeners;

    protected AbstractFormModel() {
    }

    protected AbstractFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this.domainObjectAccessStrategy = domainObjectAccessStrategy;
    }

    public Object getFormObject() {
        return getPropertyAccessStrategy().getDomainObject();
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

    public String getDisplayValue(String formPropertyPath) {
        ValueModel valueModel = getDisplayValueModel(formPropertyPath);
        assertValueModelNotNull(valueModel, formPropertyPath);
        Object o = valueModel.getValue();
        if (o == null) { return ""; }
        return String.valueOf(o);
    }

    public Object getValue(String formPropertyPath) {
        return getRequiredValueModel(formPropertyPath).getValue();
    }

    public void addFormValueChangeListener(String formPropertyPath,
            ValueChangeListener listener) {
        getRequiredValueModel(formPropertyPath)
                .addValueChangeListener(listener);
    }

    public void removeFormValueChangeListener(String formPropertyPath,
            ValueChangeListener listener) {
        getRequiredValueModel(formPropertyPath).removeValueChangeListener(
                listener);
    }

    public void addFormPropertyChangeListener(String formPropertyPath,
            PropertyChangeListener listener) {
        BoundValueModel valueModel = (BoundValueModel)getRequiredValueModel(formPropertyPath);
        valueModel.addPropertyChangeListener(BoundValueModel.VALUE_PROPERTY,
                listener);
    }

    public void removeFormPropertyChangeListener(String formPropertyPath,
            PropertyChangeListener listener) {
        BoundValueModel valueModel = (BoundValueModel)getRequiredValueModel(formPropertyPath);
        valueModel.removePropertyChangeListener(BoundValueModel.VALUE_PROPERTY,
                listener);
    }

    protected ValueModel getRequiredValueModel(String formPropertyPath) {
        ValueModel valueModel = getValueModel(formPropertyPath);
        assertValueModelNotNull(valueModel, formPropertyPath);
        return valueModel;
    }

    private void assertValueModelNotNull(ValueModel valueModel,
            String formProperty) {
        Assert
                .isTrue(
                        valueModel != null,
                        "The property '"
                                + formProperty
                                + "' has not been added to this form model (or to any parents.)");
    }

    public MutablePropertyAccessStrategy getPropertyAccessStrategy() {
        return domainObjectAccessStrategy;
    }

    public void setPropertyAccessStrategy(
            MutablePropertyAccessStrategy domainObjectAccessStrategy) {
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
            this.commitListeners = new HashSet(6);
        }
        return commitListeners;
    }

    protected boolean preEditCommit() {
        if (commitListeners == null) { return true; }
        for (Iterator i = commitListeners.iterator(); i.hasNext();) {
            CommitListener l = (CommitListener)i.next();
            if (!l.preEditCommitted(getFormObject())) { return false; }
        }
        return true;
    }

    protected void postEditCommit() {
        if (commitListeners == null) { return; }
        for (Iterator i = commitListeners.iterator(); i.hasNext();) {
            CommitListener l = (CommitListener)i.next();
            l.postEditCommitted(getFormObject());
        }
    }
}