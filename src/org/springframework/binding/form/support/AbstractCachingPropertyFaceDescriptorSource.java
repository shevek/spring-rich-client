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
package org.springframework.binding.form.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.binding.form.FormPropertyFaceDescriptorSource;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;

/**
 * A convenience superclass for FormPropertyFaceDescriptorSource's that require caching 
 * to improve the performance of FormPropertyFaceDescriptor lookup.
 *  
 * <p>FormPropertyFaceDescriptor retrieval is delegated to subclasses
 * using the <code>loadPropertyFaceDescriptor</code> method.  
 * 
 * @author Oliver Hutchison
 */
public abstract class AbstractCachingPropertyFaceDescriptorSource implements FormPropertyFaceDescriptorSource {

    /*
     * A cache with FormModel keys and Map from formPropertyPath to FormPropertyFaceDescriptor 
     * values. The keys are held with week references so this class will not prevent 
     * GC of FormModels.
     */
    private CachingMapDecorator formModelPropertyFaceDescriptors = new CachingMapDecorator(true) {
        public Object create(Object key) {
            return new HashMap();
        }
    };

    protected AbstractCachingPropertyFaceDescriptorSource() {
    }

    public FormPropertyFaceDescriptor getFormPropertyFaceDescriptor(FormModel formModel, String formPropertyPath) {
        Map faceDescriptors = (Map)formModelPropertyFaceDescriptors.get(formModel);
        FormPropertyFaceDescriptor propertyFaceDescriptor = (FormPropertyFaceDescriptor)faceDescriptors.get(formPropertyPath);
        if (propertyFaceDescriptor == null) {
            propertyFaceDescriptor = loadFormPropertyFaceDescriptor(formModel, formPropertyPath);
            Assert.notNull(propertyFaceDescriptor, "FormPropertyFaceDescriptor must not be null.");
            faceDescriptors.put(formPropertyPath, propertyFaceDescriptor);
        }
        return propertyFaceDescriptor;
    }

    /**
     * Loads the FormPropertyFaceDescriptor for the given form model and form property path. This value 
     * will be cached so performance need not be a concern of this method.
     * 
     * @param formModel the form model for which the FormPropertyFaceDescriptor is being resolved
     * @param formPropertyPath the form property path
     * @return the FormPropertyFaceDescriptor for the given form model and form property path (never null). 
     */
    protected abstract FormPropertyFaceDescriptor loadFormPropertyFaceDescriptor(FormModel formModel,
            String formPropertyPath);
}