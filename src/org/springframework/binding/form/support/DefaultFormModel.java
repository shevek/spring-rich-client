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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.form.SingleConfigurableFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.CommitTrigger;
import org.springframework.binding.value.support.ValueModelWrapper;

/**
 * @author Keith Donald
 */
public class DefaultFormModel extends AbstractFormModel implements
        SingleConfigurableFormModel {
    public static final String HAS_ERRORS_PROPERTY = "hasErrors";

    private CommitTrigger commitTrigger = new CommitTrigger();

    private Map formValueModels = new HashMap();

    public DefaultFormModel() {
    }

    public DefaultFormModel(Object domainObject) {
        this(new BeanPropertyAccessStrategy(domainObject));
    }

    public DefaultFormModel(ValueModel domainObjectHolder) {
        this(new BeanPropertyAccessStrategy(domainObjectHolder));
    }

    public DefaultFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this(domainObjectAccessStrategy, true);
    }

    public DefaultFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy,
            boolean bufferChanges) {
        super(domainObjectAccessStrategy);
        setBufferChangesDefault(bufferChanges);
    }

    public void setFormProperties(String[] formPropertyPaths) {
        formValueModels.clear();
        for (int i = 0; i < formPropertyPaths.length; i++) {
            add(formPropertyPaths[i]);
        }
    }

    protected void handleEnabledChange() {
        if (isEnabled()) {
            validate();
        }
        else {
            clearErrors();
        }
    }

    protected void validate() {
    }

    protected void clearErrors() {
    }

    protected Iterator valueModelIterator() {
        return this.formValueModels.values().iterator();
    }

    public void addValidationListener(ValidationListener listener) {
        throw new UnsupportedOperationException();
    }

    public void removeValidationListener(ValidationListener listener) {
        throw new UnsupportedOperationException();
    }

    public ValueModel add(String formPropertyPath) {
        return add(formPropertyPath, getBufferChangesDefault());
    }

    public ValueModel add(String formPropertyPath, boolean bufferChanges) {
        if (logger.isDebugEnabled()) {
            logger.debug("Adding new form value model for property '"
                    + formPropertyPath + "'");
        }
        ValueModel formValueModel = getPropertyAccessStrategy()
                .getPropertyValueModel(formPropertyPath);

        if (bufferChanges) {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating form value buffer for property '"
                        + formPropertyPath + "'");
            }
            formValueModel = new BufferedValueModel(formValueModel,
                    commitTrigger);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("No buffer created; value model updates will commit directly to the domain layer");
            }
        }
        return add(formPropertyPath, formValueModel);
    }

    public ValueModel add(String formPropertyPath, ValueModel formValueModel) {
        if (formValueModel instanceof BufferedValueModel) {
            ((BufferedValueModel)formValueModel)
                    .setCommitTrigger(commitTrigger);
        }
        formValueModel = preProcessNewFormValueModel(formPropertyPath,
                formValueModel);
        formValueModels.put(formPropertyPath, formValueModel);
        if (logger.isDebugEnabled()) {
            logger
                    .debug("Registering '" + formPropertyPath
                            + "' form property, property value model="
                            + formValueModel);
        }
        postProcessNewFormValueModel(formPropertyPath, formValueModel);
        return formValueModel;
    }

    protected ValueModel preProcessNewFormValueModel(String formPropertyPath,
            ValueModel formValueModel) {
        return formValueModel;
    }

    protected void postProcessNewFormValueModel(String formPropertyPath,
            ValueModel formValueModel) {

    }

    public ValueModel getDisplayValueModel(String formPropertyPath) {
        return getValueModel(formPropertyPath, true);
    }

    public ValueModel getValueModel(String formPropertyPath) {
        ValueModel valueModel = getDisplayValueModel(formPropertyPath);
        return recursiveGetWrappedModel(valueModel);
    }

    private ValueModel recursiveGetWrappedModel(ValueModel valueModel) {
        if (valueModel instanceof ValueModelWrapper) { return recursiveGetWrappedModel(((ValueModelWrapper)valueModel)
                .getWrappedModel()); }
        return valueModel;
    }

    public ValueModel getValueModel(String formPropertyPath, boolean queryParent) {
        ValueModel valueModel = (ValueModel)formValueModels
                .get(formPropertyPath);
        if (valueModel == null) {
            if (getParent() != null && queryParent) {
                valueModel = getParent().findValueModelFor(this,
                        formPropertyPath);
            }
        }
        return valueModel;
    }

    public boolean getHasErrors() {
        return false;
    }

    public Map getErrors() {
        return Collections.EMPTY_MAP;
    }

    public boolean isDirty() {
        if (getFormObject() instanceof FormObject) {
            return ((FormObject)getFormObject()).isDirty();
        }
        else if (getBufferChangesDefault()) {
            Iterator it = formValueModels.values().iterator();
            while (it.hasNext()) {
                ValueModel model = (ValueModel)it.next();
                if (model instanceof ValueModelWrapper) {
                    model = ((ValueModelWrapper)model).getWrappedModel();
                }
                if (model instanceof BufferedValueModel) {
                    BufferedValueModel bufferable = (BufferedValueModel)model;
                    if (bufferable.isDirty()) { return true; }
                }
            }
        }
        return false;
    }

    public void commit() {
        if (logger.isDebugEnabled()) {
            logger.debug("Commit requested for this form model " + this);
        }
        if (getFormObject() == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Form object is null; nothing to commit.");
            }
            return;
        }
        if (!isEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Form is not enabled; commiting null value.");
            }
            getFormObjectHolder().setValue(null);
            if (getFormObjectHolder() instanceof BufferedValueModel) {
                ((BufferedValueModel)getFormObjectHolder()).commit();
            }
            return;
        }
        if (getBufferChangesDefault()) {
            if (getHasErrors()) { throw new IllegalStateException(
                    "Form has errors; submit not allowed."); }
            if (preEditCommit()) {
                commitTrigger.setValue(Boolean.TRUE);
                commitTrigger.setValue(null);
                postEditCommit();
            }
        }
    }

    public void revert() {
        if (getBufferChangesDefault()) {
            commitTrigger.setValue(Boolean.FALSE);
            commitTrigger.setValue(null);
        }
    }
}