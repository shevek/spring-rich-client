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
package org.springframework.binding.form;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.convert.Converter;
import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.ValueModel;

/**
 * A form model represents the state and behavior of a form independently from 
 * the UI used to present the form.
 * 
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public interface FormModel extends PropertyChangePublisher {

    /**
     * The name of the bound property <em>dirty</em>.
     */
    String DIRTY_PROPERTY = "dirty";

    /**
     * The name of the bound property <em>enabled</em>.       
     */
    String ENABLED_PROPERTY = "enabled";
    
    /**
     * The name of the bound property <em>committable</em>.       
     */
    String COMMITTABLE_PROPERTY = "committable";
    
    /**
     * Returns the id that is used to identify this form model.
     */
    String getId();

    /**
     * Returns the object currently backing this form. This object is held by the 
     * FormObjectHolder. 
     */
    Object getFormObject();

    /**
     * Sets the object currently backing ths form.
     */
    void setFormObject(Object formObject);
    

    /**
     * Returns the value model which holds the object currently backing this 
     * form.
     */
    ValueModel getFormObjectHolder();

    /**
     * Returns a value model that holds the value of the specified 
     * form property.
     * 
     * @throws InvalidPropertyException if the form has no such property
     */
    ValueModel getValueModel(String formProperty);

    /**
     * Returns a type converting value model for the given form property. The 
     * type of the value returned from the returned value model is guaranteed to
     * be of class targetClass.
     * @throws InvalidPropertyException if the form has no such property
     * @throws IllegalArgumentException if no suitable converter from the original 
     * property class to the targetClass can be found 
     */
    ValueModel getValueModel(String formProperty, Class targetClass);
    
    /**
     * Returns the metadata for the given form property.
     */
    PropertyMetadata getPropertyMetadata(String formProperty);
    
    /**
     * Register converters for a given property name.
     * @param propertyName name of property on which to register converters
     * @param toConverter Convert from source to target type
     * @param fromConverter Convert from target to source type
     */
    public void registerPropertyConverter( String propertyName, Converter toConverter, Converter fromConverter );

    /**
     * Returns true if the form has a value model for the provided property name.
     */
    boolean hasProperty(String formProperty);

    /**
     * Commits any changes buffered by the form property value models into the
     * current form backing object.
     * 
     * @throws IllegalStateException if the form model is not committable
     * @see #isCommittable()
     */
    void commit();

    /**
     * Reverts any dirty value models back to the original values that were loaded 
     * from the current form backing object since last call to either commit or revert 
     * or since the last change of the form backing object. 
     */
    void revert();
    
    /**
     * Reset the form by replacing the form object with a newly instantiated object of the
     * type of the current form object. Note that this may lead to NPE's if the newly
     * created object has null sub-objects and this form references any of these objects.
     */
    void reset();

    /**
     * Does this form model buffer changes.
     */
    boolean isBuffered();

    /**
     * Returns true if any of the value models holding properties of this form
     * have been modified since the last call to either commit or revert or since 
     * the last change of the form backing object. 
     */
    boolean isDirty();

    /**
     * Returns true if this form is enabled (an enabled form is one which is able to be 
     * modified).
     */
    boolean isEnabled();
    
    /**
     * Returns true if the changes held by this form are able to be commited.
     */
    boolean isCommittable();
    
    /**
     * Adds the specified listener to the list if listeners notified when a commit 
     * happens.
     */
    void addCommitListener(CommitListener listener);

    /**
     * Removes the specified listener to the list if listeners notified when a commit 
     * happens.
     */
    void removeCommitListener(CommitListener listener);

    /**
     * FIXME: this should be on the PropertyMetadata class
     */
    FormPropertyFaceDescriptor getFormPropertyFaceDescriptor(String propertyName);
    
    /**
     * Returns a PropertyAccessStrategy that allows for access to the properties of 
     * this form. 
     * <p>
     * NOTE: this is not the same as the MutablePropertyAccessStrategy used to access
     * properties on the backing form object.
     * 
     * FIXME: this needs to work some other way...
     */
    PropertyAccessStrategy getPropertyAccessStrategy();
}