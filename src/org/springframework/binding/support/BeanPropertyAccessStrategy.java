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
package org.springframework.binding.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessExceptionsException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.core.enums.LabeledEnum;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapTemplate;

/**
 * An implementation of <code>MutablePropertyAccessStrategy</code> that provides access 
 * to the properties of a JavaBean.<p> 
 * 
 * This class supports <b>nested properties</b> enabling the setting/getting
 * of properties on subproperties to an unlimited depth.
 *   
 * @author Oliver Hutchison
 * @see org.springframework.beans.BeanWrapper
 */
public class BeanPropertyAccessStrategy implements MutablePropertyAccessStrategy {

    private final ValueModel domainObjectHolder;

    private final String basePropertyPath;

    private final BeanWrapper beanWrapper;

    private final ValueModelCache valueModelCache;

    private final PropertyMetadataAccessStrategy metaAspectAccessor;

    /**
     * Creates a new instance of BeanPropertyAccessStrategy that will provide access
     * to the properties of the provided JavaBean.
     * 
     * @param bean JavaBean to be accessed through this class. 
     */
    public BeanPropertyAccessStrategy(Object bean) {
        this(new ValueHolder(bean));
    }

    /**
     * Creates a new instance of BeanPropertyAccessStrategy that will provide access
     * to the JavaBean contained by the provided value model.  
     * 
     * @param domainObjectHolder value model that holds the JavaBean to 
     * be accessed through this class
     */
    public BeanPropertyAccessStrategy(final ValueModel domainObjectHolder) {
        Assert.notNull(domainObjectHolder, "domainObjectHolder must not be null.");
        this.domainObjectHolder = domainObjectHolder;
        this.domainObjectHolder.addValueChangeListener(new BeanWrapperUpdater());
        this.basePropertyPath = "";
        this.beanWrapper = new BeanWrapperImpl(domainObjectHolder.getValue());
        this.valueModelCache = new ValueModelCache();
        this.metaAspectAccessor = new BeanPropertyMetaAspectAccessor();
    }

    /**
     * Creates a child instance of BeanPropertyAccessStrategy that will delegate to its 
     * parent for property access.
     * 
     * @param parent BeanPropertyAccessStrategy which will be used to provide property access
     * @param basePropertyPath property path that will as a base when accessing the parent   
     * BeanPropertyAccessStrategy
     */
    protected BeanPropertyAccessStrategy(BeanPropertyAccessStrategy parent, String basePropertyPath) {
        this.domainObjectHolder = parent.getPropertyValueModel(basePropertyPath);
        this.basePropertyPath = basePropertyPath;
        this.beanWrapper = parent.beanWrapper;
        this.valueModelCache = parent.valueModelCache;
        this.metaAspectAccessor = new BeanPropertyMetaAspectAccessor();
    }

    public ValueModel getDomainObjectHolder() {
        return domainObjectHolder;
    }

    public ValueModel getPropertyValueModel(String propertyPath) throws BeansException {
        return (ValueModel)valueModelCache.get(getFullPropertyPath(propertyPath));
    }

    /**
     * Returns a property path that includes the base property path of the class.
     */
    private String getFullPropertyPath(String propertyPath) {
        return basePropertyPath == "" ? propertyPath : basePropertyPath + '.' + propertyPath;
    }

    /**
     * Extracts the property name from a propertyPath. 
     */
    private String getPropertyName(String propertyPath) {
        int lastSeparator = getLastPropertySeparatorIndex(propertyPath);
        if (lastSeparator == -1) {
            return propertyPath;
        }
        else {
            if (propertyPath.charAt(lastSeparator) == NESTED_PROPERTY_SEPARATOR_CHAR) {
                return propertyPath.substring(lastSeparator + 1);
            }
            else {
                return propertyPath.substring(lastSeparator);
            }
        }
    }

    /**
     * Returns the property name component of the provided property path. 
     */
    private String getParentPropertyPath(String propertyPath) {
        int lastSeparator = getLastPropertySeparatorIndex(propertyPath);
        return lastSeparator == -1 ? "" : propertyPath.substring(0, lastSeparator);
    }

    /**
     * Returns the index of the last nested property separator in
     * the given property path, ignoring dots in keys 
     * (like "map[my.key]").
     */
    private int getLastPropertySeparatorIndex(String propertyPath) {
        boolean inKey = false;
        for (int i = propertyPath.length() - 1; i >= 0; i--) {
            switch (propertyPath.charAt(i)) {
            case PROPERTY_KEY_SUFFIX_CHAR:
                inKey = true;
                break;
            case PROPERTY_KEY_PREFIX_CHAR:
                return i;
            case NESTED_PROPERTY_SEPARATOR_CHAR:
                if (!inKey) {
                    return i;
                }
                break;
            }
        }
        return -1;
    }

    public MutablePropertyAccessStrategy getPropertyAccessStrategyForPath(String propertyPath) throws BeansException {
        return new BeanPropertyAccessStrategy(this, getFullPropertyPath(propertyPath));
    }

    public MutablePropertyAccessStrategy newPropertyAccessStrategy(ValueModel domainObjectHolder) {
        return new BeanPropertyAccessStrategy(domainObjectHolder);
    }

    public Object getDomainObject() {
        return domainObjectHolder.getValue();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    public void addPropertyChangeListener(String propertyPath, PropertyChangeListener listener) {
        getPropertyValueModel(propertyPath).addValueChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyPath, PropertyChangeListener listener) {
        getPropertyValueModel(propertyPath).addValueChangeListener(listener);
    }

    public PropertyMetadataAccessStrategy getMetadataAccessStrategy() {
        return metaAspectAccessor;
    }

    public void registerCustomEditor(Class propertyType, PropertyEditor propertyEditor) {
        beanWrapper.registerCustomEditor(propertyType, propertyEditor);
    }

    public void registerCustomEditor(String propertyPath, PropertyEditor propertyEditor) {
        beanWrapper.registerCustomEditor(getMetadataAccessStrategy().getPropertyType(propertyPath),
                getFullPropertyPath(propertyPath), propertyEditor);
    }

    public PropertyEditor findCustomEditor(String propertyPath) {
        return beanWrapper.findCustomEditor(getMetadataAccessStrategy().getPropertyType(propertyPath),
                getFullPropertyPath(propertyPath));
    }

    public Object getPropertyValue(String propertyPath) throws BeansException {
        return getPropertyValueModel(propertyPath).getValue();
    }

    public void setPropertyValue(String propertyPath, Object value) throws BeansException {
        getPropertyValueModel(propertyPath).setValue(value);
    }

    public void setPropertyValue(PropertyValue pv) throws BeansException {
        setPropertyValue(pv.getName(), pv.getValue());
    }

    public void setPropertyValues(Map map) throws BeansException {
        setPropertyValues(new MutablePropertyValues(map));
    }

    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        setPropertyValues(pvs, false);
    }

    public void setPropertyValues(PropertyValues propertyValues, boolean ignoreUnknown) throws BeansException {
        List propertyAccessExceptions = new ArrayList();
        PropertyValue[] pvs = propertyValues.getPropertyValues();
        for (int i = 0; i < pvs.length; i++) {
            try {
                // This method may throw any BeansException, which won't be caught
                // here, if there is a critical failure such as no matching field.
                // We can attempt to deal only with less serious exceptions.
                setPropertyValue(pvs[i]);
            }
            catch (NotWritablePropertyException ex) {
                if (!ignoreUnknown) {
                    throw ex;
                }
                // otherwise, just ignore it and continue...
            }
            catch (PropertyAccessException ex) {
                propertyAccessExceptions.add(ex);
            }
        }

        // If we encountered individual exceptions, throw the composite exception.
        if (!propertyAccessExceptions.isEmpty()) {
            Object[] paeArray = propertyAccessExceptions.toArray(new PropertyAccessException[propertyAccessExceptions.size()]);
            throw new PropertyAccessExceptionsException(beanWrapper, (PropertyAccessException[])paeArray);
        }
    }

    /**
     * Keeps beanWrapper up to date with the value held 
     * by domainObjectHolder.
     */
    private class BeanWrapperUpdater implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            beanWrapper.setWrappedInstance(domainObjectHolder.getValue());
        }

    }

    /**
     * A cache of value models generated for specific property paths. 
     */
    private class ValueModelCache extends CachingMapTemplate {

        protected Object create(Object propertyPath) {
            String fullPropertyPath = getFullPropertyPath((String)propertyPath);
            String parentPropertyPath = getParentPropertyPath(fullPropertyPath);
            ValueModel parentValueModel = parentPropertyPath == "" ? domainObjectHolder
                    : (ValueModel)valueModelCache.get(parentPropertyPath);
            return new BeanPropertyValueModel(parentValueModel, fullPropertyPath);
        }
    }

    /**
     * A value model that wraps a single JavaBean property. Delegates to the beanWrapperr for getting and 
     * setting the value. If the wrapped JavaBean supports publishing property change events this class will
     * also register a property change listener so that changes to the property made outside of this
     * value model may also be detected and notified to any value change listeners registered with 
     * this class.
     */
    private class BeanPropertyValueModel extends AbstractValueModel {

        private final ValueModel parentValueModel;

        private final String propertyPath;

        private final String propertyName;

        private PropertyChangeListener beanPropertyChangeListener;

        private Object savedParentObject;

        private Object savedPropertyValue;

        private boolean settingBeanProperty;

        public BeanPropertyValueModel(ValueModel parentValueModel, String propertyPath) {
            this.parentValueModel = parentValueModel;
            this.parentValueModel.addValueChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    parentValueChanged();
                }
            });            
            this.propertyPath = propertyPath;
            this.propertyName = getPropertyName(propertyPath);
            if (beanWrapper.isReadableProperty(propertyPath)) {
                this.savedPropertyValue = beanWrapper.getPropertyValue(propertyPath);
            }
            updateBeanPropertyChangeListener();
        }

        public Object getValue() {
            savedPropertyValue = beanWrapper.getPropertyValue(propertyPath);
            return savedPropertyValue;
        }

        public void setValue(Object value) {
            // TODO: make this thread safe
            try {
                settingBeanProperty = true;
                beanWrapper.setPropertyValue(propertyPath, value);
            }
            finally {
                settingBeanProperty = false;
            }
            fireValueChange(savedPropertyValue, getValue());
        }

        /**
         * Called when the parent JavaBean changes.
         */
        private void parentValueChanged() {
            updateBeanPropertyChangeListener();
            if (savedParentObject == null) {
                String parentProperyPath = getParentPropertyPath(propertyPath);
                throw new NullValueInNestedPathException(
                        getMetadataAccessStrategy().getPropertyType(parentProperyPath), parentProperyPath,
                        "Parent object has changed to null. The property this value model encapsulates no longer exists!");
            }
            fireValueChange(savedPropertyValue, getValue());
        }

        /**
         * Called by the parent JavaBean if it supports PropertyChangeEvent 
         * notifications and the property wrapped by this value model
         * has changed.
         */
        private void propertyValueChanged() {
            if (!settingBeanProperty) {
                fireValueChange(savedPropertyValue, getValue());
            }
        }

        /**
         * If the parent JavaBean supports property change notification register this class 
         * as a property change listener.
         */
        private synchronized void updateBeanPropertyChangeListener() {
            final Object currentParentObject = parentValueModel.getValue();
            if (currentParentObject != savedParentObject) {
                // remove PropertyChangeListener from old parent 
                if (beanPropertyChangeListener != null) {
                    PropertyChangeSupportUtils.removePropertyChangeListener(savedParentObject, propertyName, beanPropertyChangeListener);
                    beanPropertyChangeListener = null;
                }
                // install PropertyChangeListener on new parent
                if (currentParentObject != null && PropertyChangeSupportUtils.supportsBoundProperties(currentParentObject.getClass())) {
                    beanPropertyChangeListener = new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            propertyValueChanged();
                        }
                    };
                    PropertyChangeSupportUtils.addPropertyChangeListener(currentParentObject, propertyName,
                            beanPropertyChangeListener);
                }
                savedParentObject = currentParentObject;
            }
        }

        private void removeBeanPropertyChangeListener() {
            
        }
    }

    /**
     * Implementation of PropertyMetadataAccessStrategy that 
     * simply delegates to the beanWrapper.
     */
    private class BeanPropertyMetaAspectAccessor implements PropertyMetadataAccessStrategy {

        public Class getPropertyType(String propertyPath) {
            return beanWrapper.getPropertyDescriptor(getFullPropertyPath(propertyPath)).getPropertyType();
        }

        public boolean isNumber(String propertyPath) {
            Class propertyType = getPropertyType(propertyPath);
            return Number.class.isAssignableFrom(propertyType) || propertyType.isPrimitive();
        }

        public boolean isDate(String propertyPath) {
            return Date.class.isAssignableFrom(getPropertyType(propertyPath));
        }

        public boolean isEnumeration(String propertyPath) {
            return LabeledEnum.class.isAssignableFrom(getPropertyType(propertyPath));
        }

        public boolean isReadable(String propertyPath) {
            return beanWrapper.isReadableProperty(getFullPropertyPath(propertyPath));
        }

        public boolean isWriteable(String propertyPath) {
            return beanWrapper.isWritableProperty(getFullPropertyPath(propertyPath));
        }
    }

}