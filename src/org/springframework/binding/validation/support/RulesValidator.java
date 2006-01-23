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
package org.springframework.binding.validation.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.FormModelPropertyAccessStrategy;
import org.springframework.binding.validation.DefaultValidationMessage;
import org.springframework.binding.validation.DefaultValidationResults;
import org.springframework.binding.validation.RichValidator;
import org.springframework.binding.validation.Severity;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.richclient.application.Application;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.Rules;
import org.springframework.rules.RulesSource;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.reporting.BeanValidationResultsCollector;
import org.springframework.rules.reporting.PropertyResults;

public class RulesValidator implements RichValidator {

    private static final Log logger = LogFactory.getLog(RulesValidator.class);

    private final DefaultValidationResults results = new DefaultValidationResults();

    private final FormModelAwareMessageTranslator messageTranslator;

    private final Map validationErrors = new HashMap();

    private final FormModel formModel;

    private BeanValidationResultsCollector validationResultsCollector;

    private RulesSource rulesSource;

    private String rulesContextId = null;

    /**
     * Creates a RulesValidator for the given formModel. When no RulesSource is
     * given, a default/global RulesSource is retrieved by the ApplicationServices class.
     * 
     * @see org.springframework.richclient.application.ApplicationServices#getRulesSource()
     */
    public RulesValidator(FormModel formModel) {
        this(formModel, null);
    }

    /**
     * Create a RulesValidator which uses the supplied RulesSource on the FormModel. 
     */
    public RulesValidator(FormModel formModel, RulesSource rulesSource)
    {
        this.formModel = formModel;
        this.rulesSource = rulesSource;
        validationResultsCollector = new BeanValidationResultsCollector(new FormModelPropertyAccessStrategy(formModel));
        messageTranslator = new FormModelAwareMessageTranslator(formModel, Application.services());        
    }
    
    public ValidationResults validate(Object object) {
        return validate(object, null);
    }

    public ValidationResults validate(Object object, String propertyName) {
        Rules rules = null;
        if (object instanceof PropertyConstraintProvider) {
            PropertyConstraint validationRule = ((PropertyConstraintProvider)object).getPropertyConstraint(propertyName);
            if (validationRule != null) {
                checkRule(validationRule);
            }
        }
        else {
            if (getRulesSource() != null) {
                rules = getRulesSource().getRules(object.getClass(), getRulesContextId());
                if (rules != null) {
                    for (Iterator i = rules.iterator(); i.hasNext();) {
                        PropertyConstraint validationRule = (PropertyConstraint)i.next();
                        if (propertyName == null) {
                            if (formModel.hasProperty(validationRule.getPropertyName())) {
                                checkRule(validationRule);
                            }
                        }
                        else if (validationRule.isDependentOn(propertyName)) {
                            checkRule(validationRule);
                        }
                    }
                }
            }
            else {
                logger.debug("No rules source has been configured; "
                        + "please set a valid reference to enable rules-based validation.");
            }
        }
        return results;
    }

    private void checkRule(PropertyConstraint validationRule) {
        BeanValidationResultsCollector resultsCollector = takeResultsCollector();
        PropertyResults results = resultsCollector.collectPropertyResults(validationRule);
        returnResultsCollector(resultsCollector);
        if (results == null) {
            constraintSatisfied(validationRule);
        }
        else {
            constraintViolated(validationRule, results);
        }
    }

    protected void constraintSatisfied(PropertyConstraint exp) {
        ValidationMessage message = (ValidationMessage)validationErrors.remove(exp);
        if (message != null) {
            results.removeMessage(message);
        }
    }

    protected void constraintViolated(PropertyConstraint exp, PropertyResults propertyResults) {
        ValidationMessage message = new DefaultValidationMessage(exp.getPropertyName(), Severity.ERROR,
                messageTranslator.getMessage(propertyResults));

        ValidationMessage oldMessage = (ValidationMessage)validationErrors.get(exp);
        if (!message.equals(oldMessage)) {
            results.removeMessage(oldMessage);
            validationErrors.put(exp, message);
            results.addMessage(message);
        }
    }

    private RulesSource getRulesSource() {
        if (rulesSource == null) {
            rulesSource = Application.services().getRulesSource();
        }
        return rulesSource;
    }

    private BeanValidationResultsCollector takeResultsCollector() {
        BeanValidationResultsCollector resultsCollector = validationResultsCollector;
        if (resultsCollector != null) {
            validationResultsCollector = null;
        }
        else {
            resultsCollector = new BeanValidationResultsCollector(new FormModelPropertyAccessStrategy(formModel));
        }
        return resultsCollector;
    }

    private void returnResultsCollector(BeanValidationResultsCollector resultsCollector) {
        validationResultsCollector = resultsCollector;
    }

    /**
     * Get the rules context id set on this validator.
     * @return rules context id
     */
    public String getRulesContextId() {
        return rulesContextId;
    }

    /**
     * Set the rules context id.  This is passed in the call to
     * {@link RulesSource#getRules(Class, String)} to allow for context specific rules.
     * @param rulesContextId
     */
    public void setRulesContextId(String rulesContextId) {
        this.rulesContextId = rulesContextId;
    }
}
