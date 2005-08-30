/*
 * Copyright 2005 (C) Our Community Pty. Ltd. All Rights Reserved
 * 
 * $Id$
 */

package org.springframework.binding.validation.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
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

    private final FormModel formModel;

    private BeanValidationResultsCollector validationResultsCollector;

    private RulesSource rulesSource;

    private final DefaultValidationResults results = new DefaultValidationResults();

    private final FormModelAwareMessageTranslator messageTranslator;

    private final Map validationErrors = new HashMap();

    public RulesValidator(FormModel formModel) {
        this.formModel = formModel;
        validationResultsCollector = new BeanValidationResultsCollector(formModel.getPropertyAccessStrategy());
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
                rules = getRulesSource().getRules(object.getClass(), null);
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
        PropertyResults results = (PropertyResults)resultsCollector.collectPropertyResults(validationRule);
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
            resultsCollector = new BeanValidationResultsCollector(formModel.getPropertyAccessStrategy());
        }
        return resultsCollector;
    }

    private void returnResultsCollector(BeanValidationResultsCollector resultsCollector) {
        validationResultsCollector = resultsCollector;
    }
}
