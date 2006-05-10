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

import org.springframework.beans.BeanUtils;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.Converter;
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
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.DefaultConversionService;
import org.springframework.richclient.util.Assert;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.CachingMapDecorator;

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

    private final FormModelMediatingValueModel formObjectHolder;

    private final MutablePropertyAccessStrategy propertyAccessStrategy;

    private HierarchicalFormModel parent;

    private final List children = new ArrayList();

    private boolean buffered = false;

    private boolean enabled = true;

    private boolean oldEnabled = true;

    private boolean oldDirty;

    private boolean oldCommittable = true;

    private ConversionService conversionService;

    private final CommitTrigger commitTrigger = new CommitTrigger();

    private final Map mediatingValueModels = new HashMap();

    private final Map propertyValueModels = new HashMap();

    private final Map convertingValueModels = new HashMap();

    private final Map propertyMetadata = new HashMap();

    private final Set dirtyValueAndFormModels = new HashSet();

    private final Map propertyConversionServices = new CachingMapDecorator() {
        public Object create(Object key) {
            return new DefaultConversionService() {
                protected void addDefaultConverters() {
                }
            };
        }
    };

    private FormPropertyFaceDescriptorSource formPropertyFaceDescriptorSource;

    private final PropertyChangeListener dirtyChangeHandler = new DirtyChangeHandler();

    private final PropertyChangeListener enabledChangeHandler = new EnabledChangeHandler();

    private final PropertyChangeListener committableChangeHandler = new CommittableChangeHandler();

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
        prepareValueModel(formObjectHolder);
        this.formObjectHolder = new FormModelMediatingValueModel(formObjectHolder, false);
        this.propertyAccessStrategy = new BeanPropertyAccessStrategy(formObjectHolder);
        this.buffered = buffered;        
    }

    protected AbstractFormModel(MutablePropertyAccessStrategy propertyAccessStrategy, boolean buffered) {
        this.formObjectHolder = new FormModelMediatingValueModel(propertyAccessStrategy.getDomainObjectHolder(), false);
        this.propertyAccessStrategy = propertyAccessStrategy;
        this.buffered = buffered;        
    }

    /**
     * Prepare the provided value model for use in this form model.
     * @param valueModel to prepare
     */
    protected void prepareValueModel(ValueModel valueModel) {
        if (valueModel instanceof BufferedValueModel) {
            ((BufferedValueModel)valueModel).setCommitTrigger(commitTrigger);
        }

        // If the value model that we were built on is "dirty trackable" then we
        // need to monitor it for changes in its dirty state
        if (valueModel instanceof DirtyTrackingValueModel) {
            ((DirtyTrackingValueModel)valueModel).addPropertyChangeListener(DIRTY_PROPERTY, dirtyChangeHandler);
        }
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

    public void setFormObject(Object formObject) {
        setDeliverValueChangeEvents(false);
        if (formObject == null) {
            handleSetNullFormObject();
        }
        else {
            getFormObjectHolder().setValue(formObject);
            setEnabled(true);
        }
        // this will cause all buffered value models to revert 
        // to the new form objects property values 
        commitTrigger.revert();
        setDeliverValueChangeEvents(true);
    }

    private void setDeliverValueChangeEvents(boolean disconectViewFromData) {
        formObjectHolder.setDeliverValueChangeEvents(disconectViewFromData);
        for (Iterator i = mediatingValueModels.values().iterator(); i.hasNext();) {
            FormModelMediatingValueModel valueModel = (FormModelMediatingValueModel)i.next();
            valueModel.setDeliverValueChangeEvents(disconectViewFromData);
        }        
    }

    protected void handleSetNullFormObject() {
        if (logger.isInfoEnabled()) {
            logger.info("New form object value is null; resetting to a new fresh object instance and disabling form");
        }
        getFormObjectHolder().setValue(BeanUtils.instantiateClass(getFormObject().getClass()));
        setEnabled(false);
    }

    /**
     * Returns the value model which holds the object currently backing this 
     * form.
     */
    public ValueModel getFormObjectHolder() {
        return formObjectHolder;
    }

    public HierarchicalFormModel getParent() {
        return parent;
    }

    public void setParent(HierarchicalFormModel parent) {
        Assert.required(parent, "parent");
        this.parent = parent;
        this.parent.addPropertyChangeListener(ENABLED_PROPERTY, enabledChangeHandler);
    }

    public void removeParent() {
        this.parent.removePropertyChangeListener(ENABLED_PROPERTY, enabledChangeHandler);
        this.parent = null;
    }

    public FormModel[] getChildren() {
        return (FormModel[])children.toArray(new FormModel[children.size()]);
    }

    /**
     * Add child to this FormModel. Dirty and committable changes are forwarded to
     * parent model.
     * @param child FormModel to add as child.
     */
    public void addChild(HierarchicalFormModel child) {
        Assert.required(child, "child");
        Assert.isTrue(child.getParent() == null, "Child form model '" + child + "' already has a parent");
        child.setParent(this);
        children.add(child);
        child.addPropertyChangeListener(DIRTY_PROPERTY, dirtyChangeHandler);
        child.addPropertyChangeListener(COMMITTABLE_PROPERTY, committableChangeHandler);
    }
    
    /**
     * Remove a child FormModel. Dirty and committable listeners are removed.
     * When child was dirty, remove the formModel from the dirty list and update the dirty state.
     * @param child FormModel to remove from childlist.
     */
    public void removeChild(HierarchicalFormModel child)
    {
        Assert.required(child, "child");
        child.removeParent();
        children.remove(child);
        child.removePropertyChangeListener(DIRTY_PROPERTY, dirtyChangeHandler);
        child.removePropertyChangeListener(COMMITTABLE_PROPERTY, committableChangeHandler);
        // when dynamically adding/removing childModels take care of dirtymessages:
        // removing child that was dirty: remove from dirty map and update dirty state
        if (dirtyValueAndFormModels.remove(child))
            dirtyUpdated();
    }

    public boolean hasValueModel(String formProperty) {
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

    /**
     * Creates a new value mode for the the given property. Usually delegates to the 
     * underlying property access strategy but subclasses may provide alternative
     * value model creation strategies.
     */
    protected ValueModel createValueModel(String formProperty) {
        Assert.required(formProperty, "formProperty");
        if (logger.isDebugEnabled()) {
            logger.debug("Creating " + (buffered ? "buffered" : "") + " value model for form property '" + formProperty
                    + "'.");
        }
        return buffered ? new BufferedValueModel(propertyAccessStrategy.getPropertyValueModel(formProperty))
                : propertyAccessStrategy.getPropertyValueModel(formProperty);
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
        ConversionExecutor convertTo = null;
        ConversionExecutor convertFrom = null;

        // Check for locally registered property converters
        if (propertyConversionServices.containsKey(formProperty)) {
            final DefaultConversionService propertyConversionService = (DefaultConversionService)propertyConversionServices.get(formProperty);

            if (propertyConversionService != null) {
                convertTo = propertyConversionService.getConversionExecutor(sourceClass, targetClass);
                convertFrom = propertyConversionService.getConversionExecutor(targetClass, sourceClass);
            }
        }

        // If we have nothing from the property level, then try the conversion service
        if (convertTo == null) {
            convertTo = conversionService.getConversionExecutor(sourceClass, targetClass);
        }
        Assert.notNull(convertTo, "conversionService returned null ConversionExecutor");

        if (convertFrom == null) {
            convertFrom = conversionService.getConversionExecutor(targetClass, sourceClass);
        }
        Assert.notNull(convertFrom, "conversionService returned null ConversionExecutor");

        ValueModel convertingValueModel = preProcessNewConvertingValueModel(formProperty, targetClass,
                new TypeConverter(sourceValueModel, convertTo, convertFrom));
        preProcessNewConvertingValueModel(formProperty, targetClass, convertingValueModel);
        return convertingValueModel;
    }

    /**
     * Register converters for a given property name.
     * @param propertyName name of property on which to register converters
     * @param toConverter Convert from source to target type
     * @param fromConverter Convert from target to source type
     */
    public void registerPropertyConverter(String propertyName, Converter toConverter, Converter fromConverter) {
        DefaultConversionService propertyConversionService = (DefaultConversionService)propertyConversionServices.get(propertyName);
        propertyConversionService.addConverter(toConverter);
        propertyConversionService.addConverter(fromConverter);
    }

    public ValueModel add(String propertyName) {
        return add(propertyName, createValueModel(propertyName));
    }

    public ValueModel add(String formProperty, ValueModel valueModel) {
        // XXX: this assert should be active but it breaks the 
        // code in SwingBindingFactory#createBoundListModel
        //Assert.isTrue(!hasValueModel(formProperty), "A property called '" + formProperty + "' already exists.");
        if (valueModel instanceof BufferedValueModel) {
            ((BufferedValueModel)valueModel).setCommitTrigger(commitTrigger);
        }

        // XXX: this is very broken as it assumes that the added value model was derived
        // from the property access strategy when this is not always the case. 
        PropertyMetadataAccessStrategy metadataAccessStrategy = getFormObjectPropertyAccessStrategy().getMetadataAccessStrategy();

        FormModelMediatingValueModel mediatingValueModel = new FormModelMediatingValueModel(valueModel,
                metadataAccessStrategy.isWriteable(formProperty));
        mediatingValueModels.put(formProperty, mediatingValueModel);

        PropertyMetadata metadata = new PropertyMetadataImpl(this, mediatingValueModel,
                metadataAccessStrategy.getPropertyType(formProperty),
                !metadataAccessStrategy.isWriteable(formProperty),
                metadataAccessStrategy.getAllUserMetadata(formProperty));
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

    /**
     * Provides a hook for subclasses to optionally decorate a new value model added to
     * this form model.
     */
    protected abstract ValueModel preProcessNewValueModel(String formProperty, ValueModel formValueModel);

    /**
     * Provides a hook for subclasses to perform some processing after a new value model
     * has been added to this form model.
     */
    protected abstract void postProcessNewValueModel(String formProperty, ValueModel valueModel);

    /**
     * Provides a hook for subclasses to optionally decorate a new converting value model added to
     * this form model.
     */
    protected abstract ValueModel preProcessNewConvertingValueModel(String formProperty, Class targetClass,
            ValueModel formValueModel);

    /**
     * Provides a hook for subclasses to perform some processing after a new converting value model
     * has been added to this form model.
     */
    protected abstract void postProcessNewConvertingValueModel(String formProperty, Class targetClass,
            ValueModel valueModel);

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
            formPropertyFaceDescriptorSource = (FormPropertyFaceDescriptorSource)ApplicationServicesLocator.services().getService(FormPropertyFaceDescriptorSource.class);
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
            conversionService = (ConversionService)ApplicationServicesLocator.services().getService(ConversionService.class);
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
        return new FormModelPropertyAccessStrategy(this);
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
        if (isCommittable()) {
            for (Iterator i = commitListeners.iterator(); i.hasNext();) {
                ((CommitListener)i.next()).preCommit(this);
            }
            preCommit();
            if (isCommittable()) {
                doCommit();
                postCommit();
                for (Iterator i = commitListeners.iterator(); i.hasNext();) {
                    ((CommitListener)i.next()).postCommit(this);
                }
            }
            else {
                throw new IllegalStateException("Form model '" + this
                        + "' became non-committable after preCommit phase");
            }
        }
        else {
            throw new IllegalStateException("Form model '" + this + "' is not committable");
        }
    }

    private void doCommit() {
        for (Iterator i = children.iterator(); i.hasNext();) {
            ((FormModel)i.next()).commit();
        }
        commitTrigger.commit();
        for (Iterator i = mediatingValueModels.values().iterator(); i.hasNext();) {
            ((DirtyTrackingValueModel)i.next()).clearDirty();
        }
    }

    /**
     * Hook for subclasses to intercept before a commit.
     */
    protected void preCommit() {
    }

    /**
     * Hook for subclasses to intercept after a successful commit
     * has finished.
     */
    protected void postCommit() {
    }

    /**
     * Revert state. If formModel has children, these will be reverted first.
     * CommitTrigger is used to revert bufferedValueModels while revertToOriginal() is
     * called upon FormMediatingValueModels.
     */
    public void revert() {
        for (Iterator i = children.iterator(); i.hasNext();) {
            ((FormModel)i.next()).revert();
        }
        // this will cause all buffered value models to revert
        commitTrigger.revert();
        // this will then go back and revert all unbuffered value models
        for (Iterator i = mediatingValueModels.values().iterator(); i.hasNext();) {
            ((DirtyTrackingValueModel)i.next()).revertToOriginal();
        }
    }

    /**
     * Complex forms with parent-child relations can use derived formModels.
     * Such a Hierarchical tree cannot have its children reset on its own as it
     * would break the top-down structure. see RCP-329 and the cvs maillist.
     * 
     * TODO add a unit test with such a complex use case
     * 
     * @see FormModel#reset()
     */
    public void reset() {
        setFormObject(null);
    }

    public boolean isBuffered() {
        return buffered;
    }

    public boolean isDirty() {
        return dirtyValueAndFormModels.size() > 0;
    }

    /**
     * Fires the necessary property change event for changes to the dirty 
     * property. Must be called whenever the value of dirty is changed.
     */
    protected void dirtyUpdated() {
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

    /**
     * Fires the necessary property change event for changes to the enabled 
     * property. Must be called whenever the value of enabled is changed.
     */
    protected void enabledUpdated() {
        boolean enabled = isEnabled();
        if (hasChanged(oldEnabled, enabled)) {
            oldEnabled = enabled;
            firePropertyChange(ENABLED_PROPERTY, !enabled, enabled);
        }
    }

    public boolean isCommittable() {
        for (Iterator i = children.iterator(); i.hasNext();) {
            final FormModel childFormModel = (FormModel)i.next();
            if (!childFormModel.isCommittable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fires the necessary property change event for changes to the committable 
     * property. Must be called whenever the value of committable is changed.
     */
    protected void committableUpdated() {
        boolean committable = isCommittable();
        if (hasChanged(oldCommittable, committable)) {
            oldCommittable = committable;
            firePropertyChange(COMMITTABLE_PROPERTY, !committable, committable);
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
            Object source = evt.getSource();

            if (source instanceof PropertyMetadata) {
                PropertyMetadata metadata = (PropertyMetadata)source;
                if (metadata.isDirty()) {
                    dirtyValueAndFormModels.add(metadata);
                }
                else {
                    dirtyValueAndFormModels.remove(metadata);
                }
            }
            else if (source instanceof FormModel) {
                FormModel formModel = (FormModel)source;
                if (formModel.isDirty()) {
                    dirtyValueAndFormModels.add(formModel);
                }
                else {
                    dirtyValueAndFormModels.remove(formModel);
                }
            }
            else {
                DirtyTrackingValueModel valueModel = (DirtyTrackingValueModel)source;
                if (valueModel.isDirty()) {
                    dirtyValueAndFormModels.add(valueModel);
                }
                else {
                    dirtyValueAndFormModels.remove(valueModel);
                }
            }
            dirtyUpdated();
        }
    }

    /**
     * Listens for changes to the enabled state of the parent form model
     */
    protected class EnabledChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            enabledUpdated();
        }
    }

    /**
     * Listens for changes to the committable state of the child form models
     */
    protected class CommittableChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            committableUpdated();
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