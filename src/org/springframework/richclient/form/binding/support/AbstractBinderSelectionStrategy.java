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
package org.springframework.richclient.form.binding.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.util.Assert;

/**
 * Default implementation of <code>BinderSelectionStrategy</code>. Provides for 
 * registering of binders by control type, property type and property name.  
 * 
 * @author Oliver Hutchison
 * @author Jim Moore
 */
public abstract class AbstractBinderSelectionStrategy implements BinderSelectionStrategy {

    private final Map controlTypeBinders = new HashMap();

    private final Map propertyTypeBinders = new HashMap();

    private final Map propertyNameBinders = new HashMap();

    public AbstractBinderSelectionStrategy() {        
        registerDefaultBinders();
    }

    public Binder selectBinder(FormModel formModel, String propertyName) {
        Binder binder = findBinderByPropertyName(formModel.getFormObject().getClass(), propertyName);
        if (binder == null) {
            binder = findBinderByPropertyType(getPropertyType(formModel, propertyName));
        }
        if (binder != null) {
            return binder;
        }
        else {
            throw new UnsupportedOperationException("Unable to select a binder for form model [" + formModel
                    + "] property [" + propertyName + "]");
        }
    }

    public Binder selectBinder(Class controlType, FormModel formModel, String propertyName) {
        Binder binder = findBinderByControlType(controlType);
        if (binder == null) {
            binder = selectBinder(formModel, propertyName);
        }
        if (binder != null) {
            return binder;
        }
        else {
            throw new UnsupportedOperationException("Unable to select a binder for form model [" + formModel
                    + "] property [" + propertyName + "]");
        }
    }

    /**
     * Register the default set of binders. This method is called on construction.
     * 
     * @see #registerBinderForPropertyName(Class, String, Binder)
     * @see #registerBinderForPropertyType(Class, Binder)
     * @see #registerBinderForControlType(Class, Binder)
     */
    protected abstract void registerDefaultBinders();

    /**
     * Try to find a binder for the provided parentObjectType and propertyName. If no 
     * direct match found try to find binder for any superclass of the provided 
     * objectType which also has the same propertyName.
     */
    protected Binder findBinderByPropertyName(Class parentObjectType, String propertyName) {
        PropertyNameKey key = new PropertyNameKey(parentObjectType, propertyName);
        Binder binder = (Binder)propertyNameBinders.get(key);
        if (binder == null) {
            // if no direct match was found try to find a match in any super classes
            final Map potentialMatchingBinders = new HashMap();
            for (Iterator i = propertyNameBinders.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry)i.next();
                if (((PropertyNameKey)entry.getKey()).getPropertyName().equals(propertyName)) {
                    potentialMatchingBinders.put(((PropertyNameKey)entry.getKey()).getParentObjectType(),
                            entry.getValue());
                }
            }
            binder = (Binder)ClassUtils.getValueFromMapForClass(parentObjectType, potentialMatchingBinders);
            if (binder != null) {
                // remember the lookup so it doesn't have to be discovered again 
                registerBinderForPropertyName(parentObjectType, propertyName, binder);
            }
        }
        return binder;
    }

    /**
     * Try to find a binder for the provided propertyType. If no direct match found,
     * try to find binder for closest superclass of the given control type.
     */
    protected Binder findBinderByPropertyType(Class propertyType) {
        return (Binder)ClassUtils.getValueFromMapForClass(propertyType, propertyTypeBinders);
    }

    /**
     * Try to find a binder for the provided controlType. If no direct match found,
     * try to find binder for closest superclass of the given control type.
     */
    protected Binder findBinderByControlType(Class controlType) {
        return (Binder)ClassUtils.getValueFromMapForClass(controlType, controlTypeBinders);
    }

    protected void registerBinderForPropertyName(Class parentObjectType, String propertyName, Binder binder) {
        propertyNameBinders.put(new PropertyNameKey(parentObjectType, propertyName), binder);
    }
    
    protected void registerBinderForPropertyType(Class propertyType, Binder binder) {
        propertyTypeBinders.put(propertyType, binder);
    }
    
    protected void registerBinderForControlType(Class controlType, Binder binder) {
        controlTypeBinders.put(controlType, binder);
    }

    protected Class getPropertyType(FormModel formModel, String formPropertyPath) {
        return getPropertyMetadataAccessStrategy(formModel).getPropertyType(formPropertyPath);
    }

    protected boolean isEnumeration(FormModel formModel, String formPropertyPath) {
        return getPropertyMetadataAccessStrategy(formModel).isEnumeration(formPropertyPath);
    }

    protected PropertyMetadataAccessStrategy getPropertyMetadataAccessStrategy(FormModel formModel) {
        return ((ConfigurableFormModel)formModel).getMetadataAccessStrategy();
    }

    private static class PropertyNameKey {
        private final Class parentObjectType;

        private final String propertyName;

        public PropertyNameKey(Class parentObjectType, String propertyName) {
            Assert.notNull(parentObjectType, "parentObjectType must not be null.");
            Assert.notNull(propertyName, "propertyName must not be null.");
            this.parentObjectType = parentObjectType;
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Class getParentObjectType() {
            return parentObjectType;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PropertyNameKey)) {
                return false;
            }
            final PropertyNameKey propertyNameKey = (PropertyNameKey)o;
            return propertyName.equals(propertyNameKey.propertyName)
                    && parentObjectType.equals(propertyNameKey.parentObjectType);
        }

        public int hashCode() {
            return (propertyName.hashCode() * 29) + parentObjectType.hashCode();
        }
    }
}