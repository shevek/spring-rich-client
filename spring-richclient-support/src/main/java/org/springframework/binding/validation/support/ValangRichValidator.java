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

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.validation.RichValidator;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReflectiveVisitorHelper;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.core.Severity;
import org.springframework.util.Assert;
import org.springframework.util.CachingMapDecorator;
import org.springframework.util.StringUtils;
import org.springmodules.validation.valang.ValangValidator;
import org.springmodules.validation.valang.functions.AbstractFunction;
import org.springmodules.validation.valang.functions.AbstractMathFunction;
import org.springmodules.validation.valang.functions.BeanPropertyFunction;
import org.springmodules.validation.valang.functions.Function;
import org.springmodules.validation.valang.functions.MapEntryFunction;
import org.springmodules.validation.valang.predicates.BasicValidationRule;
import org.springmodules.validation.valang.predicates.GenericTestPredicate;

/**
 * Implementation of <code>RichValidator</code> that delegates to a
 * <code>ValangValidator</code> for validation.
 *   
 * @author Oliver Hutchison
 * @see ValangValidator
 */
public class ValangRichValidator implements RichValidator {

    //  map to lists of rules effecting a given property 
    private final Map propertyRules = new CachingMapDecorator(false) {
        protected Object create(Object key) {
            return new ArrayList();
        }
    };

    private final DefaultValidationResults results = new DefaultValidationResults();

    private final Map validationErrors = new HashMap();

    private final FormModel formModel;

    private final Collection allRules;

    private MessageSourceAccessor messageSourceAccessor;

    public ValangRichValidator(FormModel formModel, ValangValidator validator) {
        this.formModel = formModel;
        this.allRules = validator.getRules();
        initPropertyRules();
    }

    private void initPropertyRules() {
        for (Iterator i = allRules.iterator(); i.hasNext();) {
            BasicValidationRule rule = (BasicValidationRule)i.next();
            Set propertiesUsedByRule = getPropertiesUsedByRule(rule);
            for (Iterator j = propertiesUsedByRule.iterator(); j.hasNext();) {
                String propertyName = (String)j.next();
                ((List)propertyRules.get(propertyName)).add(rule);
            }
        }
    }

    public MessageSourceAccessor getMessageSourceAccessor() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor)ApplicationServicesLocator.services().getService(MessageSourceAccessor.class);
        }
        return messageSourceAccessor;
    }

    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    private Set getPropertiesUsedByRule(BasicValidationRule rule) {
        PropertiesUsedByRuleCollector collector = new PropertiesUsedByRuleCollector(rule);
        return collector.getPropertiesUsedByRule();
    }

    public ValidationResults validate(Object object) {
        return validate(object, null);
    }

    public ValidationResults validate(Object object, String propertyName) {
        Collection rulesToCheck = getRulesEffectedByProperty(propertyName);
        for (Iterator i = rulesToCheck.iterator(); i.hasNext();) {
            checkRule((BasicValidationRule)i.next());
        }
        return null;
    }

    protected Collection getRulesEffectedByProperty(String propertyName) {
        return (propertyName == null) ? allRules : (Collection)propertyRules.get(propertyName);
    }

    private void checkRule(BasicValidationRule rule) {
        if (rule.getPredicate().evaluate(getSourceObject())) {
            ruleSatisfied(rule);
        }
        else {
            ruleViolated(rule);
        }
    }

    protected void ruleSatisfied(BasicValidationRule rule) {
        ValidationMessage message = (ValidationMessage)validationErrors.remove(rule);
        if (message != null) {
            results.removeMessage(message);
        }
    }

    protected void ruleViolated(BasicValidationRule rule) {
        ValidationMessage message = getValidationMessage(rule);
        ValidationMessage oldMessage = (ValidationMessage)validationErrors.get(rule);
        if (!message.equals(oldMessage)) {
            results.removeMessage(oldMessage);
            validationErrors.put(rule, message);
            results.addMessage(message);
        }
    }

    protected ValidationMessage getValidationMessage(BasicValidationRule rule) {
        String translatedMessage;
        String field = rule.getField();
        String errorMessage = rule.getErrorMessage();
        String errorKey = rule.getErrorKey();
        if (StringUtils.hasLength(errorKey)) {
            Collection errorArgs = rule.getErrorArgs();
            if (errorArgs != null && !errorArgs.isEmpty()) {
                Collection arguments = new ArrayList();
                for (Iterator iter = errorArgs.iterator(); iter.hasNext();) {
                    arguments.add(((Function)iter.next()).getResult(getSourceObject()));
                }
                translatedMessage = getMessageSourceAccessor().getMessage(errorKey, arguments.toArray(), errorMessage);
            }
            else {
                translatedMessage = getMessageSourceAccessor().getMessage(errorKey, errorMessage);
            }
        }
        else {
            translatedMessage = getMessageSourceAccessor().getMessage(field, errorMessage);
        }
        return new DefaultValidationMessage(field, Severity.ERROR, translatedMessage);
    }

    protected Object getSourceObject() {
        return new FormModel2BeanWrapperAdapter();
    }

    /** 
     *  Visitor that collects the names of all properties that are used by a single Valang
     *  validation rule. 
     */
    private static class PropertiesUsedByRuleCollector {

        private static final ReflectiveVisitorHelper reflectiveVisitorHelper = new ReflectiveVisitorHelper();

        private final BasicValidationRule rule;

        private Set propertiesUsedByRule;

        public PropertiesUsedByRuleCollector(BasicValidationRule rule) {
            this.rule = rule;
        }

        public Set getPropertiesUsedByRule() {
            if (propertiesUsedByRule == null) {
                propertiesUsedByRule = new HashSet();
                doVisit(rule.getPredicate());
                Collection errorArgs = rule.getErrorArgs();
                if (errorArgs != null && !errorArgs.isEmpty()) {
                    for (Iterator iter = errorArgs.iterator(); iter.hasNext();) {
                        doVisit(iter.next());
                    }
                }
            }
            return propertiesUsedByRule;
        }

        protected void doVisit(Object value) {
            reflectiveVisitorHelper.invokeVisit(this, value);
        }

        void visit(BeanPropertyFunction f) {
            propertiesUsedByRule.add(f.getField());
        }

        void visitNull() {
        }

        void visit(Function f) {
        }

        void visit(AbstractFunction f) {
            Function[] arguments = f.getArguments();
            for (int i = 0; i < arguments.length; i++) {
                doVisit(arguments[i]);
            }
        }

        void visit(NotPredicate p) {
            Assert.isTrue(p.getPredicates().length == 1);
            doVisit(p.getPredicates()[0]);
        }

        void visit(AndPredicate p) {
            for (int i = 0; i < p.getPredicates().length; i++) {
                doVisit(p.getPredicates()[i]);
            }
        }

        void visit(OrPredicate p) {
            for (int i = 0; i < p.getPredicates().length; i++) {
                doVisit(p.getPredicates()[i]);
            }
        }

        void visit(GenericTestPredicate p) {
            doVisit(p.getLeftFunction());
            doVisit(p.getRightFunction());
        }

        void visit(MapEntryFunction f) {
            doVisit(f.getMapFunction());
            doVisit(f.getKeyFunction());
        }

        void visit(AbstractMathFunction f) {
            doVisit(f.getLeftFunction());
            doVisit(f.getRightFunction());
        }
    }

    /**
     * Adapts the FormModel interface to the BeanWrapper interface
     * so that the Valang rules evaluator can access the form models 
     * properties.
     */
    private class FormModel2BeanWrapperAdapter implements BeanWrapper {

        public Object getPropertyValue(String propertyName) throws BeansException {
            return formModel.getValueModel(propertyName).getValue();
        }

        public void setWrappedInstance(Object obj) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Object getWrappedInstance() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Class getWrappedClass() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void registerCustomEditor(Class requiredType, PropertyEditor propertyEditor) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void registerCustomEditor(Class requiredType, String propertyPath, PropertyEditor propertyEditor) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
            throw new UnsupportedOperationException("Not implemented");
        }

        public PropertyDescriptor[] getPropertyDescriptors() throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Class getPropertyType(String propertyName) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public boolean isReadableProperty(String propertyName) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public boolean isWritableProperty(String propertyName) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setPropertyValue(String propertyName, Object value) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setPropertyValue(PropertyValue pv) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setPropertyValues(Map map) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setPropertyValues(PropertyValues pvs) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setPropertyValues(PropertyValues propertyValues, boolean ignoreUnknown) throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setPropertyValues(PropertyValues propertyValues, boolean ignoreUnknown, boolean ignoreInvalid)
                throws BeansException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public void setExtractOldValueForEditor(boolean extractOldValueForEditor){
            throw new UnsupportedOperationException("Not implemented");
        }

        public boolean isExtractOldValueForEditor() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Object convertIfNecessary(Object object, Class aClass) throws TypeMismatchException {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Object convertIfNecessary(Object object, Class aClass, MethodParameter methodParameter)
                throws TypeMismatchException {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}