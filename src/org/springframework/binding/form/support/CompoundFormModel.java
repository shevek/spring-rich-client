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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NestableFormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.PropertyAdapter;
import org.springframework.rules.closure.Block;
import org.springframework.rules.constraint.AbstractConstraint;
import org.springframework.rules.support.Algorithms;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class CompoundFormModel extends AbstractFormModel implements
        NestingFormModel {

    private Map childFormModels = new LinkedHashMap(9);

    public CompoundFormModel() {

    }

    public CompoundFormModel(Object domainObject) {
        this(new BeanPropertyAccessStrategy(domainObject));
    }

    public CompoundFormModel(ValueModel domainObjectHolder) {
        this(new BeanPropertyAccessStrategy(domainObjectHolder));
    }

    public CompoundFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this(domainObjectAccessStrategy, true);
    }

    public CompoundFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy,
            boolean bufferChanges) {
        super(domainObjectAccessStrategy);
        setBufferChangesDefault(bufferChanges);
    }

    public ConfigurableFormModel createChild(String childFormModelName) {
        ValidatingFormModel childModel = new ValidatingFormModel(
                getPropertyAccessStrategy(), getBufferChangesDefault());
        childModel.setRulesSource(getRulesSource());
        addChildModel(childFormModelName, childModel);
        return childModel;
    }

    public ConfigurableFormModel createChild(String childFormModelName,
            String childFormObjectPath) {
        return (ConfigurableFormModel)createChildInternal(
                new ValidatingFormModel(), childFormModelName,
                childFormObjectPath);
    }

    public NestingFormModel createCompoundChild(String childFormModelName,
            String childFormObjectPath) {
        return (NestingFormModel)createChildInternal(new CompoundFormModel(),
                childFormModelName, childFormObjectPath);
    }

    private FormModel createChildInternal(AbstractFormModel childFormModel,
            String childFormModelName, String childFormObjectPath) {
        ValueModel valueHolder = new PropertyAdapter(
                getPropertyAccessStrategy(), childFormObjectPath);
        if (getBufferChangesDefault()) {
            valueHolder = new BufferedValueModel(valueHolder);
        }
        boolean enabledDefault = valueHolder.getValue() != null;
        Class valueClass = getMetadataAccessStrategy().getPropertyType(
                childFormObjectPath);
        if (valueHolder.getValue() == null) {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("Backing form object set to null; instantiating fresh instance to prevent null pointer exceptions");
            }
            valueHolder.setValue(BeanUtils.instantiateClass(valueClass));
        }
        return createChildInternal(childFormModel, childFormModelName,
                valueHolder, enabledDefault);
    }

    public ConfigurableFormModel createChild(String childFormModelName,
            ValueModel childFormObjectHolder) {
        return createChild(childFormModelName, childFormObjectHolder, true);
    }

    public NestingFormModel createCompoundChild(String childFormModelName,
            ValueModel childFormObjectHolder) {
        return createCompoundChild(childFormModelName, childFormObjectHolder,
                true);
    }

    public ConfigurableFormModel createChild(String childFormModelName,
            ValueModel childFormObjectHolder, boolean enabled) {
        return (ConfigurableFormModel)createChildInternal(
                new ValidatingFormModel(), childFormModelName,
                childFormObjectHolder, enabled);
    }

    public NestingFormModel createCompoundChild(String childFormModelName,
            ValueModel childFormObjectHolder, boolean enabled) {
        return (NestingFormModel)createChildInternal(new CompoundFormModel(),
                childFormModelName, childFormObjectHolder, enabled);
    }

    private FormModel createChildInternal(AbstractFormModel childModel,
            String childFormModelName, ValueModel childFormObjectHolder,
            boolean enabled) {
        MutablePropertyAccessStrategy childObjectAccessStrategy = getPropertyAccessStrategy()
                .newPropertyAccessStrategy(childFormObjectHolder);
        childModel.setPropertyAccessStrategy(childObjectAccessStrategy);
        childModel.setEnabled(enabled);
        childModel.setBufferChangesDefault(getBufferChangesDefault());
        childModel.setRulesSource(getRulesSource());
        addChildModel(childFormModelName, childModel);
        return childModel;
    }

    public NestableFormModel addChildModel(String childFormModelName,
            NestableFormModel childModel) {
        Assert.isTrue(getChildFormModel(childFormModelName) == null,
                "Child model by name '" + childFormModelName
                        + "' already exists");
        childModel.setParent(this);
        if (logger.isDebugEnabled()) {
            logger.debug("Adding new nested form model '" + childFormModelName
                    + "', value=" + childModel);
        }
        childFormModels.put(childFormModelName, childModel);
        return childModel;
    }

    public void addValidationListener(final ValidationListener listener) {
        Algorithms.instance().forEach(childFormModels.values(), new Block() {
            public void handle(Object formModel) {
                ((FormModel)formModel).addValidationListener(listener);
            }
        });
    }

    public void addValidationListener(ValidationListener listener,
            String childFormModelName) {
        FormModel model = getChildFormModel(childFormModelName);
        Assert.notNull(model, "No child model by name " + childFormModelName
                + "exists; unable to add listener");
        model.addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener,
            String childFormModelName) {
        FormModel model = getChildFormModel(childFormModelName);
        Assert.notNull(model, "No child model by name " + childFormModelName
                + "exists; unable to remove listener");
        model.removeValidationListener(listener);
    }

    public FormModel getChildFormModel(String childFormModelName) {
        return (FormModel)childFormModels.get(childFormModelName);
    }

    public void removeValidationListener(final ValidationListener listener) {
        Algorithms.instance().forEach(childFormModels.values(), new Block() {
            public void handle(Object childFormModel) {
                ((FormModel)childFormModel).removeValidationListener(listener);
            }
        });
    }

    public ValueModel getDisplayValueModel(String formProperty) {
        // todo
        return null;
    }

    public ValueModel getValueModel(String formPropertyPath) {
        return unwrap(getDisplayValueModel(formPropertyPath, true));
    }

    public ValueModel findDisplayValueModelFor(FormModel delegatingChild,
            String formPropertyPath) {
        Iterator it = childFormModels.values().iterator();
        while (it.hasNext()) {
            NestableFormModel formModel = (NestableFormModel)it.next();
            if (delegatingChild != null && formModel == delegatingChild) {
                continue;
            }
            ValueModel valueModel = formModel.getDisplayValueModel(
                    formPropertyPath, false);
            if (valueModel != null) { return valueModel; }
        }
        if (logger.isInfoEnabled()) {
            logger.info("No value model by name '" + formPropertyPath
                    + "' found on any nested form models... returning [null]");
        }
        return null;
    }

    public ValueModel getDisplayValueModel(String formPropertyPath,
            boolean queryParent) {
        ValueModel valueModel = findDisplayValueModelFor(null, formPropertyPath);
        if (valueModel == null) {
            if (getParent() != null && queryParent) {
                valueModel = getParent().findDisplayValueModelFor(this,
                        formPropertyPath);
            }
        }
        return valueModel;
    }

    protected void handleEnabledChange() {
        new Block() {
            protected void handle(Object childFormModel) {
                ((FormModel)childFormModel).setEnabled(isEnabled());
            }
        }.forEach(childFormModels.values());
    }

    public boolean getHasErrors() {
        return new AbstractConstraint() {
            public boolean test(Object childFormModel) {
                return ((FormModel)childFormModel).getHasErrors();
            }
        }.any(childFormModels.values());
    }

    public Map getErrors() {
        final Map allErrors = new HashMap();
        new Block() {
            public void handle(Object childFormModel) {
                allErrors.putAll(((FormModel)childFormModel).getErrors());
            }
        }.forEach(childFormModels.values());
        return allErrors;
    }

    public boolean isDirty() {
        return new AbstractConstraint() {
            public boolean test(Object childFormModel) {
                return ((FormModel)childFormModel).isDirty();
            }
        }.any(childFormModels.values());
    }

    public boolean hasErrors(String childModelName) {
        FormModel model = getChildFormModel(childModelName);
        Assert.notNull(model, "No child model by name " + childModelName
                + "exists.");
        return model.getHasErrors();
    }

    public void commit() {
        if (preEditCommit()) {
            new Block() {
                protected void handle(Object childFormModel) {
                    ((FormModel)childFormModel).commit();
                }
            }.forEach(childFormModels.values());

            if (getFormObjectHolder() instanceof BufferedValueModel) {
                ((BufferedValueModel)getFormObjectHolder()).commit();
            }
            postEditCommit();
        }
    }

    public void revert() {
        new Block() {
            protected void handle(Object childFormModel) {
                ((FormModel)childFormModel).revert();
            }
        }.forEach(childFormModels.values());
    }
}