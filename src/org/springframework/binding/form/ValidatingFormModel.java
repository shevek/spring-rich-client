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

import org.springframework.binding.validation.ValidationResultsModel;

/**
 * Sub-interface implemented by form models that can validatate the forms 
 * properties.
 * 
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public interface ValidatingFormModel extends ConfigurableFormModel, HierarchicalFormModel {
    
    public static final String VALIDATING_PROPERTY = "validating";
    
    /**
     * Returns the ValidationResultsModel which encapsulates the set of 
     * validation messages currently active against this form model. Will
     * be empty if validation is disabled.
     */
    ValidationResultsModel getValidationResults();
    
    /**
     * Is this form model currently validating
     */
    boolean isValidating();

    /**
     * Sets whether or not validation is currently enabled for this 
     * form model. If validatiuon is enabled the form model will 
     * imediatly validate all form properties; if validation 
     * is disabled all validation messages held by the 
     * ValidationResultsModel will be cleared.
     */
    void setValidating(boolean validating);

    /**
     * Forces the form model to validate its self. Is validation is dissabled 
     * does nothing.     
     */
    public void validate();
}