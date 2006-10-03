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
package org.springframework.binding.validation;

import org.springframework.binding.value.PropertyChangePublisher;

/**
 * @author  Oliver Hutchison
 */
public interface ValidationResultsModel extends ValidationResults, PropertyChangePublisher {
    
    /** 
     * The name of the bound property <em>hasErrors</em>.       
     */
    String HAS_ERRORS_PROPERTY = "hasErrors";
    
    /** 
     * The name of the bound property <em>hasWarnings</em>.       
     */
    String HAS_WARNINGS_PROPERTY = "hasWarnings";
    
    /** 
     * The name of the bound property <em>hasInfos</em>.       
     */
    String HAS_INFO_PROPERTY = "hasInfo";
    
    /**
     * Adds a listener that will be notified when there is any change to the set of 
     * validation messages. 
     */
    void addValidationListener(ValidationListener listener);

    /**
     * Removes the provided validation listener.
     */
    void removeValidationListener(ValidationListener listener);    
    
    /**
     * Adds a listener that will be notified when there is any change to the set validation 
     * messages for the specified property.
     */
    void addValidationListener(String propertyName, ValidationListener listener);

    /**
     * Removes the provided validation listener.
     */
    void removeValidationListener(String propertyName, ValidationListener listener);    
}