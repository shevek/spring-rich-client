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

import org.springframework.binding.value.ValueModel;


/**
 * @author Keith Donald
 */
public interface NestingFormModel extends FormModel {

    /**
     * Called by a nestable form model contained by this form model, where the
     * calling child form model is searching for a value model associated with
     * the provided form property path. The nesting form model will then query
     * the other form models it nests in an effort to find the value model. If
     * no value model exists for the <code>formPropertyPath</code> after
     * searching all nested models, <code>null</code> is returned.
     * 
     * @param delegatingChild
     *            The delegating nestable form model.
     * @param formPropertyPath
     *            The form property path to search for.
     * @return The value model if one exists at the provided formPropertyPath
     */
    public ValueModel findDisplayValueModelFor(FormModel delegatingChild,
            String formPropertyPath);

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * the same form object managed by the parent.
     * 
     * @param childFormModelName
     * @return The child for model.
     */
    public SingleConfigurableFormModel createChild(String childFormModelName);

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * the value model at the specified parent property path.
     * 
     * @param childFormModelName
     * @param childFormObjectPropertyPath
     * @return The child form model
     */
    public SingleConfigurableFormModel createChild(String childFormModelName,
            String childFormObjectPropertyPath);

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * the value model at the specified parent property path.
     * 
     * @param childFormModelName
     * @param childFormObjectPropertyPath
     * @return The child form model
     */
    public NestingFormModel createCompoundChild(String childFormModelName,
            String childFormObjectPropertyPath);

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * accessed via the provided value model.
     * 
     * @param childFormModelName
     * @param childFormObjectHolder
     * @return The child form model
     */
    public SingleConfigurableFormModel createChild(String childFormModelName,
            ValueModel childFormObjectHolder);

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * accessed via the provided value model.
     * 
     * @param childFormModelName
     * @param childFormObjectHolder
     * @return The child form model
     */
    public NestingFormModel createCompoundChild(String childFormModelName,
            ValueModel childFormObjectHolder);

    /**
     * Retrieve a child form model by name.
     * 
     * @param childModelName
     *            the contained form model's name
     * @return the child form model, or <code>null</code> if none found.
     */
    public FormModel getChildFormModel(String childModelName);

}