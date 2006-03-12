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
package org.springframework.binding.form;

/**
 * Interface to be implemented by objects that can resolve a FormPropertyFaceDescriptor 
 * for a given form model and form property path.
 *  
 * @author Oliver Hutchison
 * @see org.springframework.binding.form.support.MessageSourceFormPropertyFaceDescriptorSource 
 */
public interface FormPropertyFaceDescriptorSource {

    /**
     * Return the FormPropertyFaceDescriptor for the given form model and form property path.
     * 
     * @param formModel the form model for which the FormPropertyFaceDescriptor is being resolved
     * @param formPropertyPath the form property path
     * @return the FormPropertyFaceDescriptor for the given form model and form property path (never null). 
     */
    FormPropertyFaceDescriptor getFormPropertyFaceDescriptor(FormModel formModel, String formPropertyPath);
}
