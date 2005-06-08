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
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.CommitTrigger;
import org.springframework.binding.value.support.TypeConverter;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class DefaultFormModel extends AbstractFormModel implements ConfigurableFormModel {
    public static final String HAS_ERRORS_PROPERTY = "hasErrors";

    private CommitTrigger commitTrigger = new CommitTrigger();

    private Map valueModels = new HashMap();

    private Map convertingValueModels = new HashMap();

    public DefaultFormModel() {
    }

    public DefaultFormModel(Object domainObject) {
        this(new BeanPropertyAccessStrategy(domainObject));
    }

    public DefaultFormModel(ValueModel domainObjectHolder) {
        this(new BeanPropertyAccessStrategy(domainObjectHolder));
    }

    public DefaultFormModel(MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this(domainObjectAccessStrategy, true);
    }

    public DefaultFormModel(MutablePropertyAccessStrategy domainObjectAccessStrategy, boolean bufferChanges) {
        super(domainObjectAccessStrategy);
        setBufferChangesDefault(bufferChanges);
    }

    public void setFormProperties(String[] formPropertyPaths) {
        valueModels.clear();
        for (int i = 0; i < formPropertyPaths.length; i++) {
            add(formPropertyPaths[i]);
        }
    }

    protected void handleEnabledChange() {
        if (isEnabled()) {
            doValidate();
        }
        else {
            doClearErrors();
        }
    }

    protected void doValidate() {
    }

    protected void doClearErrors() {
    }

    protected Iterator valueModelIterator() {
        return this.valueModels.values().iterator();
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
            logger.debug("Adding new form value model for property '" + formPropertyPath + "'");
        }
        ValueModel formValueModel = getPropertyAccessStrategy().getPropertyValueModel(formPropertyPath);
        if (bufferChanges) {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating form value buffer for property '" + formPropertyPath + "'");
            }
            formValueModel = new BufferedValueModel(formValueModel, commitTrigger);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("No buffer created; value model updates will commit directly to the domain layer");
            }
        }
        return add(formPropertyPath, formValueModel);
    }

    public ValueModel add(String formPropertyPath, ValueModel formValueModel) {
        if (formValueModel instanceof BufferedValueModel) {
            ((BufferedValueModel)formValueModel).setCommitTrigger(commitTrigger);
        }
        else {
            ValueModel unwrapped = unwrap(formValueModel);
            if (unwrapped instanceof BufferedValueModel) {
                ((BufferedValueModel)unwrapped).setCommitTrigger(commitTrigger);
            }
        }
        formValueModel = preProcessNewFormValueModel(formPropertyPath, formValueModel);
        valueModels.put(formPropertyPath, formValueModel);
        if (logger.isDebugEnabled()) {
            logger.debug("Registering '" + formPropertyPath + "' form property, property value model=" + formValueModel);
        }
        postProcessNewFormValueModel(formPropertyPath, formValueModel);
        return formValueModel;
    }

    protected ValueModel preProcessNewFormValueModel(String formPropertyPath, ValueModel formValueModel) {
        return formValueModel;
    }

    protected void postProcessNewFormValueModel(String formPropertyPath, ValueModel formValueModel) {

    }

    public ValueModel getValueModel(String formPropertyPath) {
        return getValueModel(formPropertyPath, null);
    }

    protected ValueModel preProcessNewFormValueModel(String formPropertyPath, ValueModel formValueModel,
            Class targetClass) {
        return formValueModel;
    }

    protected void postProcessNewFormValueModel(String formPropertyPath, ValueModel formValueModel, Class targetClass) {
    }

    public ValueModel getValueModel(String formPropertyPath, Class targetClass) {
        final ConvertingValueModelKey key = new ConvertingValueModelKey(formPropertyPath, targetClass);
        ValueModel valueModel = (ValueModel)convertingValueModels.get(key);
        if (valueModel == null) {
            valueModel = getParent() != null ? getParent().findValueModel(formPropertyPath, targetClass) : null;
            if (valueModel != null) {
                valueModel = preProcessNewFormValueModel(formPropertyPath, valueModel, targetClass);
                postProcessNewFormValueModel(formPropertyPath, valueModel, targetClass);
                convertingValueModels.put(key, valueModel);
                return valueModel;
            }
            else {
                valueModel = createConvertingValueModel(formPropertyPath, targetClass);
                valueModel = preProcessNewFormValueModel(formPropertyPath, valueModel, targetClass);
                postProcessNewFormValueModel(formPropertyPath, valueModel, targetClass);
                convertingValueModels.put(key, valueModel);
                return valueModel;
            }
        }
        else {
            return valueModel;
        }
    }

    private ValueModel createConvertingValueModel(String propertyName, Class targetClass) {
        final Class sourceClass = getPropertyAccessStrategy().getMetadataAccessStrategy().getPropertyType(propertyName);
        if (targetClass == null) {
            return add(propertyName);
        }
        else if (sourceClass == targetClass) {
            return getValueModel(propertyName);
        }
        final ConversionService conversionService = getConversionService();
        ConversionExecutor convertTo = conversionService.getConversionExecutor(sourceClass, targetClass);
        ConversionExecutor convertFrom = conversionService.getConversionExecutor(targetClass, sourceClass);
        return new TypeConverter(getValueModel(propertyName), convertTo, convertFrom);
    }

    public void validate() {
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
            Iterator it = valueModels.values().iterator();
            while (it.hasNext()) {
                ValueModel model = unwrap((ValueModel)it.next());
                if (model instanceof BufferedValueModel) {
                    BufferedValueModel bufferable = (BufferedValueModel)model;
                    if (bufferable.isBuffering()) {
                        return true;
                    }
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
                logger.debug("Form is not enabled; committing null value.");
            }
            setFormObject(null);
            if (getFormObjectHolder() instanceof BufferedValueModel) {
                ((BufferedValueModel)getFormObjectHolder()).commit();
            }
            return;
        }
        if (getBufferChangesDefault()) {
            if (getHasErrors()) {
                throw new IllegalStateException("Form has errors; submit not allowed.");
            }
            if (preEditCommit()) {
                commitTrigger.commit();
                if (getFormObjectHolder() instanceof BufferedValueModel) {
                    ((BufferedValueModel)getFormObjectHolder()).commit();
                }
                postEditCommit();
            }
        }
    }

    public void revert() {
        if (getBufferChangesDefault()) {
            commitTrigger.revert();
        }
    }

    private static class ConvertingValueModelKey {

        private final String propertyName;

        private final Class targetClass;

        public ConvertingValueModelKey(String propertyName, Class targetClass) {
            Assert.notNull(propertyName, "propertyName must not be null.");
            //            Assert.notNull(targetClass, "targetClass must not be null.");
            this.propertyName = propertyName;
            this.targetClass = targetClass;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Class getTargetClass() {
            return targetClass;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ConvertingValueModelKey)) {
                return false;
            }
            final ConvertingValueModelKey key = (ConvertingValueModelKey)o;
            return propertyName.equals(key.propertyName)
                    && (targetClass == key.targetClass || (targetClass != null && targetClass.equals(key.targetClass)));
        }

        public int hashCode() {
            return (propertyName.hashCode() * 29) + (targetClass == null ? 7 : targetClass.hashCode());
        }
    }

    public ValueModel findValueModel(String propertyPath, Class targetType) {
        if (targetType == null) {
            return (ValueModel)valueModels.get(propertyPath);
        }
        else {
            return (ValueModel)convertingValueModels.get(new ConvertingValueModelKey(propertyPath, null));
        }
    }

}