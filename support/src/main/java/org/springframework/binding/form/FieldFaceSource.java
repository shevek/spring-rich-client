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
 * Interface to be implemented by objects that can resolve a FieldFace 
 * for a given form model and form property path.
 *  
 * @author Oliver Hutchison
 * @see org.springframework.binding.form.support.MessageSourceFieldFaceSource 
 */
public interface FieldFaceSource {

    /**
     * Return the FieldFace for the given form model and form field.
     * 
     * @param formModel the form model for which the FieldFace is being resolved
     * @param field the form field
     * @return the FieldFace for the given form model and field (never null). 
     */
    FieldFace getFieldFace(FormModel formModel, String field);
}
