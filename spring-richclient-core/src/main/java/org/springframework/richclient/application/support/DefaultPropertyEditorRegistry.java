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
package org.springframework.richclient.application.support;

import java.beans.PropertyEditor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.util.Assert;

/**
 * This provides a default implementation of {@link PropertyEditorRegistry}
 * 
 * @author Jim Moore
 */
public class DefaultPropertyEditorRegistry 
//implements PropertyEditorRegistry 
{
    private static final Log logger = LogFactory.getLog(
        DefaultPropertyEditorRegistry.class);
    
    private Map propertyEditorByClass = new HashMap();
    
    private Map propertyEditorByClassAndProperty = new HashMap();
    
    public DefaultPropertyEditorRegistry() {
        initDefaultEditors();
    }
    
    /**
     * Initialize the default property editors, covering primitive and other basic value types.
     */
    protected void initDefaultEditors() {
        setPropertyEditor(int.class, DefaultIntegerEditor.class);
        setPropertyEditor(Integer.class, DefaultIntegerEditor.class);
        setPropertyEditor(long.class, DefaultLongEditor.class);
        setPropertyEditor(Long.class, DefaultLongEditor.class);
        setPropertyEditor(float.class, DefaultFloatEditor.class);
        setPropertyEditor(Float.class, DefaultFloatEditor.class);
        setPropertyEditor(double.class, DefaultDoubleEditor.class);
        setPropertyEditor(Double.class, DefaultDoubleEditor.class);
        setPropertyEditor(BigDecimal.class, DefaultBigDecimalEditor.class);
        setPropertyEditor(Date.class, DefaultDateEditor.class);
    }
    
    private static class DefaultIntegerEditor extends CustomNumberEditor {
        public DefaultIntegerEditor() {
            super(Integer.class, true);
        }
    }

    private static class DefaultLongEditor extends CustomNumberEditor {
        public DefaultLongEditor() {
            super(Long.class, true);
        }
    }

    private static class DefaultFloatEditor extends CustomNumberEditor {
        public DefaultFloatEditor() {
            super(Float.class, true);
        }
    }

    private static class DefaultDoubleEditor extends CustomNumberEditor {
        public DefaultDoubleEditor() {
            super(Double.class, true);
        }
    }
    
    private static class DefaultBigDecimalEditor extends CustomNumberEditor {
        public DefaultBigDecimalEditor() {
            super(BigDecimal.class, true);
        }
    }

    private static class DefaultBooleanEditor extends CustomBooleanEditor {
        public DefaultBooleanEditor() {
            super(true);
        }
    }

    private static class DefaultDateEditor extends CustomDateEditor {
        public DefaultDateEditor() {
            super(DateFormat.getDateInstance(), true);
        }
    }

    /**
     * Adds a list of property editors to the registry extracting the object
     * class, property name and property editor class from the properties
     * "objectClass", "propertyName" and "propertyEditorClass".
     * 
     * @param propertyEditors
     *            the list of property editors. Each element is expected to be
     *            an instance of <code>java.lang.Properties</code>.
     * @see DefaultPropertyEditorRegistry#setPropertyEditor(Properties)
     */
    public void setPropertyEditors(List propertyEditors) {
        for (Iterator i = propertyEditors.iterator(); i.hasNext();) {
            setPropertyEditor((Properties)i.next());            
        }
    }
    
    /**
     * Adds a property editor to the registry extracting the object class,
     * property name and property editor class from the properties
     * "objectClass", "propertyName" and "propertyEditorClass".
     * 
     * @param properties
     *            the properties
     */
    public void setPropertyEditor(Properties properties) {
        ClassEditor classEditor = new ClassEditor();
        
        Class objectClass = null;
        String propertyName = null;
        Class propertyEditorClass = null;
                
        if (properties.get("objectClass") != null) {
            classEditor.setAsText((String) properties.get("objectClass"));
            objectClass = (Class) classEditor.getValue();
        }
        propertyName = (String) properties.get("propertyName");
        if (properties.get("propertyEditorClass") != null) {
            classEditor.setAsText((String) properties.get("propertyEditorClass"));
            propertyEditorClass = (Class) classEditor.getValue();
        } else {
            throw new IllegalArgumentException("propertyEditorClass is required");
        }
        
        if (propertyName != null) {
            setPropertyEditor(objectClass, propertyName, propertyEditorClass);
        } else if (objectClass != null) {
            setPropertyEditor(objectClass, propertyEditorClass);
        } else {
            throw new IllegalArgumentException("objectClass and/or propertyName are required");
        }        
    }


    public void setPropertyEditor(final Class typeClass,
                                  final Class propertyEditorClass) {
        Assert.notNull(typeClass);
        verifyPropertyEditorClass(propertyEditorClass);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting " + propertyEditorClass +
                " as the property editor for " + typeClass);
        }
        this.propertyEditorByClass.put(typeClass, propertyEditorClass);
    }


    private void verifyPropertyEditorClass(final Class propertyEditorClass) {
        // do some checks so we "fail fast"
        Assert.notNull(propertyEditorClass);
        Assert.isTrue(PropertyEditor.class.isAssignableFrom(propertyEditorClass),
                propertyEditorClass + " is not a " + PropertyEditor.class);
        try {
            Assert.isTrue(Modifier.isPublic(propertyEditorClass.getConstructor(null).getModifiers()),
                    propertyEditorClass + " does not have a public no-arg constructor");
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(propertyEditorClass +
                " does not have a no-arg constructor");
        }
    }


    public void setPropertyEditor(final Class objectType,
                                  final String propertyName,
                                  final Class propertyEditorClass) {
        Assert.notNull(objectType);
        Assert.notNull(propertyName);
        Assert.isTrue(ClassUtils.isAProperty(objectType, propertyName), "'" + propertyName + "' is not a property of " + objectType);
        verifyPropertyEditorClass(propertyEditorClass);
        if (logger.isDebugEnabled()) {
            logger.debug("Setting " + propertyEditorClass +
                " as the property editor for the '" + propertyName +
                "' property of " + objectType);
        }
        final ClassAndPropertyKey key =
            new ClassAndPropertyKey(objectType, propertyName);
        this.propertyEditorByClassAndProperty.put(key, propertyEditorClass);
    }


    public PropertyEditor getPropertyEditor(final Class typeClass) {
        final Class editorClass = (Class)ClassUtils.getValueFromMapForClass(
            typeClass, this.propertyEditorByClass);

        if (editorClass == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not find a property editor for " +
                    typeClass);
            }
            return null;
        }

        return instantiatePropertyEditor(editorClass);
    }


    public PropertyEditor getPropertyEditor(final Class objectType,
                                            final String propertyName) {
        final ClassAndPropertyKey key = new ClassAndPropertyKey(objectType,
            propertyName);
        Class editorClass =
            (Class)this.propertyEditorByClassAndProperty.get(key);

        if (editorClass != null) {
            return instantiatePropertyEditor(editorClass);
        }

        // maybe it's registered under a different class...
        final Set keys = this.propertyEditorByClassAndProperty.keySet();
        final Map map = new HashMap();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            ClassAndPropertyKey propertyKey = (ClassAndPropertyKey)iterator.next();
            if (propertyName.equals(propertyKey.getPropertyName())) {
                map.put(propertyKey.getTheClass(),
                    this.propertyEditorByClassAndProperty.get(propertyKey));
            }
        }

        editorClass =
            (Class)ClassUtils.getValueFromMapForClass(objectType, map);
        if (editorClass != null) {
            // remember the lookup so it doesn't have to be discovered again
            setPropertyEditor(objectType, propertyName, editorClass);
            return instantiatePropertyEditor(editorClass);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Could not find a property editor for the " +
                propertyName + " property of " + objectType +
                ", so looking for it by class type");
        }

        // didn't find it directly, so look for it by the class
        final Class propertyClass =
            ClassUtils.getPropertyClass(objectType, propertyName);

        return getPropertyEditor(propertyClass);
    }


    private static PropertyEditor instantiatePropertyEditor(Class propEdClass) {
        try {
            return (PropertyEditor)propEdClass.newInstance();
        }
        catch (InstantiationException e) {
            IllegalStateException exp = new IllegalStateException(
                "Could not instantiate " + propEdClass);
            exp.initCause(e);
            throw exp;
        }
        catch (IllegalAccessException e) {
            IllegalStateException exp = new IllegalStateException(
                "Could not instantiate " + propEdClass);
            exp.initCause(e);
            throw exp;
        }
    }


    //***********************************************************************
    // INNER CLASSES
    //***********************************************************************
    
    private static class ClassAndPropertyKey {
        private Class theClass;
        private String propertyName;

        public ClassAndPropertyKey(Class theClass, String propertyName) {
            if (theClass == null || propertyName == null) throw new NullPointerException();
            this.propertyName = propertyName;
            this.theClass = theClass;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Class getTheClass() {
            return theClass;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassAndPropertyKey)) return false;

            final ClassAndPropertyKey classAndPropertyKey = (ClassAndPropertyKey)o;

            if (propertyName != null ?
                !propertyName.equals(classAndPropertyKey.propertyName) :
                classAndPropertyKey.propertyName != null)
                return false;
            if (theClass != null ?
                !theClass.equals(classAndPropertyKey.theClass) :
                classAndPropertyKey.theClass != null)
                return false;

            return true;
        }


        public int hashCode() {
            int result;
            result = (theClass != null ? theClass.hashCode() : 0);
            result = 29 * result +
                (propertyName != null ? propertyName.hashCode() : 0);
            return result;
        }

    }

}
