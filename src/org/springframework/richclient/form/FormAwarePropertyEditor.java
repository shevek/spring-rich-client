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
package org.springframework.richclient.form;

import java.beans.PropertyEditor;

import org.springframework.binding.form.FormModel;

/**
 * Interface to be implemented by property editors that wish to be aware 
 * of their owning form model and of the name of the property they are editing.
 * 
 * @author Oliver Hutchison
 */
public interface FormAwarePropertyEditor extends PropertyEditor {

    /**
     * Callback that supplies the owning form model and property name that 
     * this property editor is responsible for editing.
     * <p>
     * This method must be called before the custom editor is requested via the 
     * <code>getCustomEditor</code> method. 
     * 
     * @param formModel 
     *      the owning form model
     * @param propertyName 
     *      the name of the property which this property editor 
     *      is responsible for editing
     */
    void setFormDetails(FormModel formModel, String propertyName);
}