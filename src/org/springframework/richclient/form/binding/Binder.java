/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding;

import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;

/**
 * A Binder is responsible for creating a binding between a form model's property and
 * a control that may be used to visualize and/or edit that property.
 * 
 * @author Oliver Hutchison
 */
public interface Binder {
    
    /**
     * Returns a binding between a form model's property and a control that will be
     * created by this Binder.
     * @param formModel the form model that this binding is for
     * @param formModel the property that this binding is for
     * @param context additional context that may be used by this binder. 
     * @return a Binding (never null). 
     * @throws UnsupportedOperationException if this binder is unable to create its 
     * own control
     */
    Binding bind(FormModel formModel, String formPropertyPath, Map context);
    
    /**
     * Returns a binding between a form model's property and the provided control.
     * @param control the visual control that will be bound to the form model's property. 
     * @param formModel the form model that this binding is for
     * @param formModel the property that this binding is for
     * @param context additional context that may be used by this binder
     * @return a Binding (never null). 
     * @throws UnsupportedOperationException if this binder is unable to bind the 
     * provided control or if this binder is unable to bind a provided control
     */
    Binding bind(JComponent control, FormModel formModel, String formPropertyPath, Map context);
}