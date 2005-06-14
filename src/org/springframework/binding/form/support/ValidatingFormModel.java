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
package org.springframework.binding.form.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.form.ValidationEvent;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelWrapper;
import org.springframework.core.EventListenerListHelper;
import org.springframework.core.Styler;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.reporting.BeanValidationResultsCollector;
import org.springframework.rules.reporting.PropertyResults;
import org.springframework.rules.reporting.TypeResolvable;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class ValidatingFormModel extends DefaultFormModel implements PropertyAccessStrategy {

    private Map validationErrors = new HashMap();

    private EventListenerListHelper validationListeners = new EventListenerListHelper(ValidationListener.class);

    private BeanValidationResultsCollector validationResultsCollector = new BeanValidationResultsCollector(this);

    private String validationContextId;

    public ValidatingFormModel() {
    }

    public ValidatingFormModel(Object domainObject) {
        super(domainObject);
    }

    public ValidatingFormModel(ValueModel domainObjectHolder) {
        super(domainObjectHolder);
    }

    public ValidatingFormModel(MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        super(domainObjectAccessStrategy);
    }

    public ValidatingFormModel(MutablePropertyAccessStrategy domainObjectAccessStrategy, boolean bufferChanges) {
        super(domainObjectAccessStrategy, bufferChanges);
    }

    public void setValidationContextId(String contextId) {
        this.validationContextId = contextId;
    }

    public Object getPropertyValue(String propertyName) {
        return getValue(propertyName);
    }

    public Object getDomainObject() {
        return this;
    }

    public boolean getHasErrors() {
        return this.validationErrors.size() > 0;
    }

    public Map getErrors() {
        return Collections.unmodifiableMap(validationErrors);
    }

    protected void doClearErrors() {
        Iterator it = this.validationErrors.keySet().iterator();
        boolean hadErrorsBefore = getHasErrors();
        while (it.hasNext()) {
            PropertyConstraint exp = (PropertyConstraint)it.next();
            it.remove();
            fireConstraintSatisfied(exp);
        }
        Assert.state(getHasErrors() == false, "There should be no errors after a clear");
        if (hadErrorsBefore) {
            firePropertyChange(HAS_ERRORS_PROPERTY, true, false);
        }
    }

    protected void doValidate() {
        for (Iterator i = valueModelIterator(); i.hasNext();) {
            ValidatingFormValueModel vm = (ValidatingFormValueModel)i.next();
            vm.validate();
        }
    }

    public int getFormPropertiesWithErrorsCount() {
        return validationErrors.size();
    }

    public int getTotalErrorCount() {
        Iterator it = validationErrors.values().iterator();
        int totalErrors = 0;
        while (it.hasNext()) {
            totalErrors += ((PropertyResults)it.next()).getViolatedCount();
        }
        return totalErrors;
    }

    public void addValidationListener(ValidationListener validationListener) {
        validationListeners.add(validationListener);

    }

    public void removeValidationListener(ValidationListener validationListener) {
        validationListeners.remove(validationListener);

    }

    protected ValueModel preProcessNewFormValueModel(String domainObjectProperty, ValueModel formValueModel) {
        return new ValidatingFormValueModel(domainObjectProperty, formValueModel,
                getValidationRule(domainObjectProperty));
    }

    protected void postProcessNewFormValueModel(String domainObjectProperty, ValueModel valueModel) {
        // trigger validation to catch initial form errors
        if (valueModel instanceof ValidatingFormValueModel && isEnabled()) {
            // ((ValidatingFormValueModel)valueModel).validate();
        }
    }

    protected ValueModel preProcessNewFormValueModel(String domainObjectProperty, ValueModel formValueModel,
            Class targetClass) {
        if (formValueModel instanceof ValidatingFormValueModel) {
            return formValueModel;
        }
        else {
            return new ValidatingFormValueModel(domainObjectProperty, formValueModel, null);
        }
    }

    protected void postProcessNewFormValueModel(String domainObjectProperty, ValueModel valueModel, Class targetClass) {
        // trigger validation to catch initial form errors
        if (valueModel instanceof ValidatingFormValueModel && isEnabled()) {
            // ((ValidatingFormValueModel)valueModel).validate();
        }
    }

    protected PropertyConstraint getValidationRule(String domainObjectProperty) {
        PropertyConstraint constraint = null;
        // @TODO if form object changes, rules aren't updated...introduces
        // subtle bugs...
        // ... for rules dependent on instance...
        if (getFormObject() instanceof PropertyConstraintProvider) {
            constraint = ((PropertyConstraintProvider)getFormObject()).getPropertyConstraint(domainObjectProperty);
        }
        else {
            if (getRulesSource() != null) {
                constraint = getRulesSource().getPropertyConstraint(getFormObjectClass(), domainObjectProperty,
                        validationContextId);
            }
            else {
                logger.info("No rules source has been configured; "
                        + "please set a valid reference to enable rules-based validation.");
            }
        }
        return constraint;
    }

    protected class ValidatingFormValueModel extends AbstractValueModelWrapper {
        private final ValueSetterConstraint setterConstraint;

        private final PropertyConstraint validationRule;

        private final String property;

        private final ValueChangeHandler valueChangeHander;

        public ValidatingFormValueModel(String property, ValueModel model, PropertyConstraint validationRule) {
            super(model);
            this.setterConstraint = new ValueSetterConstraint(property);
            this.validationRule = validationRule;
            this.property = property;
            this.valueChangeHander = new ValueChangeHandler();
            addValueChangeListener(valueChangeHander);
        }

        public class ValueChangeHandler implements PropertyChangeListener {
            public void propertyChange(PropertyChangeEvent evt) {
                if (isEnabled()) {
                    validate();
                }
            }
        }

        public String getProperty() {
            return property;
        }

        public PropertyConstraint getPropertyConstraint() {
            return validationRule;
        }

        public boolean isCompoundRule() {
            return validationRule != null && validationRule.isCompoundRule();
        }

        public boolean tests(String propertyName) {
            return validationRule != null && validationRule.isDependentOn(propertyName);
        }

        public void setValueSilently(Object value, PropertyChangeListener listenerToSkip) {
            // @TODO this error handling needs work - message source resolvable?
            try {
                if (logger.isDebugEnabled()) {
                    Class valueClass = (value != null ? value.getClass() : null);
                    logger.debug("Setting '" + property + "' value to convert/validate '" + value + "', class="
                            + valueClass);
                }
                removeValueChangeListener(valueChangeHander);
                try {
                    super.setValueSilently(value, listenerToSkip);
                }
                finally {
                    addValueChangeListener(valueChangeHander);
                }
                if (isEnabled()) {
                    constraintSatisfied(setterConstraint);
                    validate();
                }
            }
            catch (Exception e) {
                logger.debug("Exception occurred setting value", e);
                if (isEnabled()) {
                    setterConstraint.setType(e);
                    PropertyResults results = new PropertyResults(getProperty(), value, setterConstraint);
                    constraintViolated(setterConstraint, results);
                }
            }
        }

        public void validate() {
            validatePropertyConstraint();
            Iterator it = valueModelIterator();
            while (it.hasNext()) {
                ValidatingFormValueModel vm = (ValidatingFormValueModel)it.next();
                if (vm != ValidatingFormValueModel.this && vm.isCompoundRule() && vm.tests(getProperty())) {
                    vm.validatePropertyConstraint();
                }
            }
        }

        public void validatePropertyConstraint() {
            if (validationRule == null) {
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("[Validating domain object property '" + getProperty() + "']");
            }
            PropertyResults results = (PropertyResults)validationResultsCollector.collectPropertyResults(validationRule);
            if (results == null) {
                constraintSatisfied(validationRule);
            }
            else {
                constraintViolated(validationRule, results);
            }
        }
    }

    private class ValueSetterConstraint implements PropertyConstraint, TypeResolvable {
        private String property;

        private String type = "typeMismatch";

        public ValueSetterConstraint(String property) {
            this.property = property;
        }

        public String getType() {
            return type;
        }

        public void setType(Exception e) {
            if (e instanceof NullPointerException) {
                type = "required";
            }
            else if (e instanceof TypeMismatchException) {
                type = "typeMismatch";
            }
            else if (e instanceof InvalidFormatException) {
                type = "typeMismatch";
            }
            else if (e instanceof IllegalArgumentException) {
                type = "typeMismatch";
            }
            else if (e.getCause() instanceof Exception) {
                setType((Exception)e.getCause());
            }
            else {
                type = "unknown";
            }
        }

        public String getPropertyName() {
            return property;
        }

        public boolean isCompoundRule() {
            return false;
        }

        public boolean isDependentOn(String propertyName) {
            return getPropertyName().equals(propertyName);
        }

        public boolean test(Object value) {
            return true;
        }
    }

    protected void constraintSatisfied(PropertyConstraint exp) {
        if (logger.isDebugEnabled()) {
            logger.debug("Value constraint '" + exp + "' [satisfied] for value model '" + exp.getPropertyName() + "']");
        }
        if (validationErrors.containsKey(exp)) {
            validationErrors.remove(exp);
            fireConstraintSatisfied(exp);
            if (!getHasErrors()) {
                firePropertyChange(HAS_ERRORS_PROPERTY, true, false);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Number of errors on form is now " + validationErrors.size() + "; errors="
                    + Styler.call(validationErrors));
        }
    }

    private void fireConstraintSatisfied(PropertyConstraint constraint) {
        validationListeners.fire("constraintSatisfied", new ValidationEvent(this, constraint));
    }

    protected void constraintViolated(PropertyConstraint exp, PropertyResults results) {
        if (logger.isDebugEnabled()) {
            logger.debug("Value constraint '" + exp + "' [rejected], results='" + results + "']");
        }
        boolean hadErrorsBefore = getHasErrors();
        validationErrors.put(exp, results);
        fireConstraintViolated(exp, results);
        firePropertyChange(HAS_ERRORS_PROPERTY, hadErrorsBefore, true);            
        if (logger.isDebugEnabled()) {
            logger.debug("Number of errors on form is now " + validationErrors.size() + "; errors="
                    + Styler.call(validationErrors));
        }
    }

    private void fireConstraintViolated(PropertyConstraint constraint, PropertyResults results) {
        validationListeners.fire("constraintViolated", new ValidationEvent(this, constraint, results));
    }

    public void validate() {
        Iterator it = valueModelIterator();
        while (it.hasNext()) {
            ValidatingFormValueModel vm = (ValidatingFormValueModel)it.next();
            vm.validatePropertyConstraint();
        }
    }

    public ValueModel findValueModel(String propertyPath, Class targetType) {
        ValueModel vm = super.findValueModel(propertyPath, targetType);
        if (vm instanceof ValidatingFormValueModel) {
            return ((ValidatingFormValueModel)vm).getWrappedValueModel();
        }
        else {
            return vm;
        }
    }

}