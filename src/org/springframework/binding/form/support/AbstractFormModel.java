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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.binding.form.FormPropertyFaceDescriptorSource;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.PropertyMetadata;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.value.CommitTrigger;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractPropertyChangePublisher;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.DirtyTrackingValueModel;
import org.springframework.binding.value.support.MethodInvokingDerivedValueModel;
import org.springframework.binding.value.support.TypeConverter;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.util.Assert;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.richclient.util.EventListenerListHelper;

/**
 * Base implementation of HierarchicalFormModel and ConfigurableFormModel subclasses need only
 * implement the 4 value model interception methods.
 * 
 * @author  Keith Donald
 * @author  Oliver Hutchison
 */
public abstract class AbstractFormModel extends AbstractPropertyChangePublisher implements HierarchicalFormModel,
        ConfigurableFormModel {

    private String id;

    private final ValueModel formObjectHolder;

    private final MutablePropertyAccessStrategy propertyAccessStrategy;

    private HierarchicalFormModel parent;

    private List children = new ArrayList();

    private boolean buffered = false;

    private boolean enabled = true;

    private boolean oldEnabled = true;

    private boolean oldDirty;

    private ConversionService conversionService;

    private final CommitTrigger commitTrigger = new CommitTrigger();

    private final Map mediatingValueModels = new HashMap();

    private final Map propertyValueModels = new HashMap();

    private final Map convertingValueModels = new HashMap();

    private final Map propertyMetadata = new HashMap();

    private final Set dirtyValueAndFormModels = new HashSet();

    private FormPropertyFaceDescriptorSource formPropertyFaceDescriptorSource;

    private final PropertyChangeListener dirtyChangeHandler = new DirtyChangeHandler();

    private final PropertyChangeListener parentEnabledChangeHandler = new ParentEnabledChangeHandler();

    private final EventListenerListHelper commitListeners = new EventListenerListHelper(CommitListener.class);

    protected AbstractFormModel() {
        this(new ValueHolder());
    }

    protected AbstractFormModel(Object domainObject) {
        this(new ValueHolder(domainObject), true);
    }

    public AbstractFormModel(Object domainObject, boolean buffered) {
        this(new ValueHolder(domainObject), buffered);
    }

    protected AbstractFormModel(ValueModel formObjectHolder, boolean buffered) {
        this.formObjectHolder = formObjectHolder;
        this.propertyAccessStrategy = new BeanPropertyAccessStrategy(formObjectHolder);
    }

    protected AbstractFormModel(MutablePropertyAccessStrategy propertyAccessStrategy, boolean buffered) {
        this.formObjectHolder = propertyAccessStrategy.getDomainObjectHolder();
        this.propertyAccessStrategy = propertyAccessStrategy;
        this.buffered = buffered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getFormObject() {
        return getFormObjectHolder().getValue();
    }

    public ValueModel getFormObjectHolder() {
        return formObjectHolder;
    }

    public HierarchicalFormModel getParent() {
        return parent;
    }

    public void setParent(HierarchicalFormModel parent) {
        Assert.required(parent, "parent");
        this.parent = parent;
        this.parent.addPropertyChangeListener(ENABLED_PROPERTY, parentEnabledChangeHandler);
    }

    public FormModel[] getChildren() {
        return (FormModel[])children.toArray(new FormModel[children.size()]);
    }

    public void addChild(HierarchicalFormModel child) {
        Assert.required(child, "child");
        Assert.isTrue(child.getParent() == null, "Child form model '" + child + "' already has a parent");
        child.setParent(this);
        child.addPropertyChangeListener(DIRTY_PROPERTY, dirtyChangeHandler);
    }

    public boolean hasProperty(String formProperty) {
        return propertyValueModels.containsKey(formProperty);
    }

    public ValueModel getValueModel(String formProperty) {
        ValueModel propertyValueModel = (ValueModel)propertyValueModels.get(formProperty);
        if (propertyValueModel == null) {
            propertyValueModel = add(formProperty);
        }
        return propertyValueModel;
    }

    public ValueModel getValueModel(String formProperty, Class targetClass) {
        final ConvertingValueModelKey key = new ConvertingValueModelKey(formProperty, targetClass);
        ValueModel convertingValueModel = (ValueModel)convertingValueModels.get(key);
        if (convertingValueModel == null) {
            convertingValueModel = createConvertingValueModel(formProperty, targetClass);
            convertingValueModels.put(key, convertingValueModel);
        }
        return convertingValueModel;
    }

    protected ValueModel createConvertingValueModel(String formProperty, Class targetClass) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating converting value model for form property '" + formProperty
                    + "' converting to type '" + targetClass + "'.");
        }
        final ValueModel sourceValueModel = getValueModel(formProperty);
        Assert.notNull(sourceValueModel, "Form does not have a property called '" + formProperty + "'.");
        final Class sourceClass = ClassUtils.convertPrimitiveToWrapper(getPropertyMetadata(formProperty).getPropertyType());
        if (sourceClass == targetClass) {
            return sourceValueModel;
        }
        final ConversionService conversionService = getConversionService();
        ConversionExecutor convertTo = conversionService.getConversionExecutor(sourceClass, targetClass);
        Assert.notNull(convertTo, "conversionService returned null ConversionExecutor");
        ConversionExecutor convertFrom = conversionService.getConversionExecutor(targetClass, sourceClass);
        Assert.notNull(convertFrom, "conversionService returned null ConversionExecutor");

        ValueModel convertingValueModel = preProcessNewConvertingValueModel(formProperty, targetClass,
                new TypeConverter(sourceValueModel, convertTo, convertFrom));
        preProcessNewConvertingValueModel(formProperty, targetClass, convertingValueModel);
        return convertingValueModel;
    }

    protected abstract ValueModel preProcessNewConvertingValueModel(String formProperty, Class targetClass,
            ValueModel formValueModel);

    protected abstract void postProcessNewConvertingValueModel(String formProperty, Class targetClass,
            ValueModel valueModel);

    public ValueModel add(String propertyName) {
        return add(propertyName, createValueModel(propertyName));
    }

    protected ValueModel createValueModel(String formProperty) {
        Assert.required(formProperty, "formProperty");
        if (logger.isDebugEnabled()) {
            logger.debug("Creating " + (buffered ? "buffered" : "") + " value model for form property '" + formProperty
                    + "'.");
        }
        return buffered ? new BufferedValueModel(propertyAccessStrategy.getPropertyValueModel(formProperty))
                : propertyAccessStrategy.getPropertyValueModel(formProperty);
    }

    public ValueModel add(String formProperty, ValueModel valueModel) {
        // XXX: this assert should be active but it breaks the 
        // code in SwingBindingFactory#createBoundListModel
        //Assert.isTrue(!hasProperty(formProperty), "A property called '" + formProperty + "' already exists.");
        if (valueModel instanceof BufferedValueModel) {
            ((BufferedValueModel)valueModel).setCommitTrigger(commitTrigger);
        }

        FormModelMediatingValueModel mediatingValueModel = new FormModelMediatingValueModel(valueModel);
        mediatingValueModels.put(formProperty, mediatingValueModel);

        // XXX: this is very broken as it assumes that the added value model was derived
        // from the property access strategy when this is no always the case. 
        PropertyMetadataAccessStrategy metadataAccessStrategy = getFormObjectPropertyAccessStrategy().getMetadataAccessStrategy();
        PropertyMetadata metadata = new PropertyMetadataImpl(this, mediatingValueModel,
                metadataAccessStrategy.getPropertyType(formProperty), !metadataAccessStrategy.isWriteable(formProperty));
        metadata.addPropertyChangeListener(PropertyMetadata.DIRTY_PROPERTY, dirtyChangeHandler);
        propertyMetadata.put(formProperty, metadata);

        valueModel = preProcessNewValueModel(formProperty, mediatingValueModel);
        propertyValueModels.put(formProperty, valueModel);

        if (logger.isDebugEnabled()) {
            logger.debug("Registering '" + formProperty + "' form property, property value model=" + valueModel);
        }
        postProcessNewValueModel(formProperty, valueModel);
        return valueModel;
    }

    protected abstract ValueModel preProcessNewValueModel(String formProperty, ValueModel formValueModel);

    protected abstract void postProcessNewValueModel(String formProperty, ValueModel valueModel);

    public PropertyMetadata getPropertyMetadata(String propertyName) {
        PropertyMetadata metadata = (PropertyMetadata)propertyMetadata.get(propertyName);
        if (metadata == null) {
            add(propertyName);
            metadata = (PropertyMetadata)propertyMetadata.get(propertyName);
        }
        return metadata;
    }

    /**
     * Sets the FormPropertyFaceDescriptorSource that this will be used to resolve 
     * FormPropertyFaceDescriptors. 
     * <p>If this value is <code>null</code> the default FormPropertyFaceDescriptorSource from
     * <code>ApplicationServices</code> instance will be used.
     */
    public void setFormPropertyFaceDescriptorSource(FormPropertyFaceDescriptorSource formPropertyFaceDescriptorSource) {
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

    public ValueModel addMethod(String propertyMethodName, String derivedFromProperty) {
        return addMethod(propertyMethodName, new String[] {derivedFromProperty});
    }

    public ValueModel addMethod(String propertyMethodName, String[] derivedFromProperties) {
        ValueModel[] propertyValueModels = new ValueModel[derivedFromProperties.length];
        for (int i = 0; i < propertyValueModels.length; i++) {
            propertyValueModels[i] = getValueModel(derivedFromProperties[i]);
        }
        ValueModel valueModel = new MethodInvokingDerivedValueModel(this, propertyMethodName, propertyValueModels);
        return add(propertyMethodName, valueModel);
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

    public MutablePropertyAccessStrategy getFormObjectPropertyAccessStrategy() {
        return propertyAccessStrategy;
    }

    public PropertyAccessStrategy getPropertyAccessStrategy() {
        return new PropertyAccessStrategy() {

            public Object getPropertyValue(String propertyPath) throws BeansException {
                return getValueModel(propertyPath).getValue();
            }

            public PropertyMetadataAccessStrategy getMetadataAccessStrategy() {
                throw new UnsupportedOperationException("not implemented");
            }

            public Object getDomainObject() {
                return getFormObject();
            }
        };
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
            throw new IllegalStateException("Form is disabled; submit not allowed.");
        }
        if (preEditCommit()) {
            for (Iterator i = commitListeners.iterator(); i.hasNext();) {
                if (!((CommitListener)i.next()).preEditCommitted(getFormObject())) {
                    return;
                }
            }
            commitTrigger.commit();
            for (Iterator i = mediatingValueModels.values().iterator(); i.hasNext();) {
                ((DirtyTrackingValueModel)i.next()).clearDirty();
            }
            for (Iterator i = commitListeners.iterator(); i.hasNext();) {
                ((CommitListener)i.next()).postEditCommitted(getFormObject());
            }
            postEditCommit();
        }

    }

    /**
     * Hook for subclasses to intercept and possibly reject commit.
     * @return true if commit should comence; false to stop commit.
     */
    protected boolean preEditCommit() {
        return true;
    }

    /**
     * Hook for subclasses to intercept after a successfull commit
     * has finished.
     */
    protected void postEditCommit() {
    }

    public void revert() {
        // this will cause all buffered value models to revert
        commitTrigger.revert();
        // this will then go back and revert all unbuffered value models
        for (Iterator i = mediatingValueModels.values().iterator(); i.hasNext();) {
            ((DirtyTrackingValueModel)i.next()).revertToOriginal();
        }
    }

    public boolean isBuffered() {
        return buffered;
    }

    public boolean isDirty() {
        return dirtyValueAndFormModels.size() > 0;
    }

    private void dirtyUpdated() {
        boolean dirty = isDirty();
        if (hasChanged(oldDirty, dirty)) {
            oldDirty = dirty;
            firePropertyChange(DIRTY_PROPERTY, !dirty, dirty);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        enabledUpdated();
    }

    public boolean isEnabled() {
        return enabled && (parent == null || parent.isEnabled());
    }

    private void enabledUpdated() {
        boolean enabled = isEnabled();
        if (hasChanged(oldEnabled, enabled)) {
            oldEnabled = enabled;
            firePropertyChange(ENABLED_PROPERTY, !enabled, enabled);
        }
    }

    public void addCommitListener(CommitListener listener) {
        commitListeners.add(listener);
    }

    public void removeCommitListener(CommitListener listener) {
        commitListeners.remove(listener);
    }

    /**
     * Listens for changes to the dirty state of child form models and property meta data 
     */
    protected class DirtyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() instanceof PropertyMetadata) {
                PropertyMetadata metadata = (PropertyMetadata)evt.getSource();
                if (metadata.isDirty()) {
                    dirtyValueAndFormModels.add(metadata);
                }
                else {
                    dirtyValueAndFormModels.remove(metadata);
                }
            }
            else {
                FormModel formModel = (FormModel)evt.getSource();
                if (formModel.isDirty()) {
                    dirtyValueAndFormModels.add(formModel);
                }
                else {
                    dirtyValueAndFormModels.remove(formModel);
                }
            }
            dirtyUpdated();
        }
    }

    /**
     * Listens for changes to the enabled state of the parent form model
     */
    public class ParentEnabledChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            enabledUpdated();
        }
    }

    /**
     * Class for keys in the convertingValueModels map.
     */
    protected static class ConvertingValueModelKey {

        private final String propertyName;

        private final Class targetClass;

        public ConvertingValueModelKey(String propertyName, Class targetClass) {
            this.propertyName = propertyName;
            this.targetClass = targetClass;
        }

        public boolean equals(Object o) {
            final ConvertingValueModelKey key = (ConvertingValueModelKey)o;
            return propertyName.equals(key.propertyName) && (targetClass == key.targetClass);
        }

        public int hashCode() {
            return (propertyName.hashCode() * 29) + (targetClass == null ? 7 : targetClass.hashCode());
        }
    }
}