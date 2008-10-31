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
package org.springframework.rules.metadata;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.rules.constraint.Constraint;
import org.springframework.metadata.Attributes;
import org.springframework.rules.Rules;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.support.DefaultRulesSource;

/**
 * An implementation of RulesSource that loads rules using metadata.
 * 
 * @author Oliver Hutchison
 */
public class AttributesRulesSource extends DefaultRulesSource {

    private final Attributes attributes;

    public AttributesRulesSource(Attributes attributes) {
        this.attributes = attributes;
    }

    public synchronized Rules getRules(Class beanClass, String contextId) {
        Rules rules = super.getRules(beanClass, contextId);
        if (rules == null) {
            buildRules(beanClass);
            rules = super.getRules(beanClass, contextId);
        }
        return rules;
    }

    private void buildRules(Class beanClass) {
        Rules rules = new Rules(beanClass);
        PropertyDescriptor[] propertDescriptors = new BeanWrapperImpl(beanClass).getPropertyDescriptors();
        for (int i = 0; i < propertDescriptors.length; i++) {
            loadPropertyConstraints(rules, propertDescriptors[i]);
        }
        addRules(rules);
    }

    private void loadPropertyConstraints(Rules rules, PropertyDescriptor propertDescriptor) {
        String propertyName = propertDescriptor.getName();
        loadPropertyConstraintsForMethod(rules, propertyName, propertDescriptor.getReadMethod());
        loadPropertyConstraintsForMethod(rules, propertyName, propertDescriptor.getWriteMethod());
    }

    private void loadPropertyConstraintsForMethod(Rules rules, String propertyName, Method method) {
        if (method != null) {
            for (Iterator i = attributes.getAttributes(method).iterator(); i.hasNext();) {
                Object attribute = i.next();
                if (attribute instanceof Constraint) {
                    Constraint constraint = (Constraint)attribute;
                    if (constraint instanceof PropertyConstraint) {
                        rules.add((PropertyConstraint)constraint);
                    }
                    else if (constraint instanceof CompoundPropertyConstraint) {
                        rules.add((CompoundPropertyConstraint)constraint);
                    }
                    else {
                        rules.add(propertyName, constraint);
                    }
                }
            }
        }
    }
}