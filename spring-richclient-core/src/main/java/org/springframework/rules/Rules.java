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
package org.springframework.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.rules.constraint.Constraint;
import org.springframework.core.style.ToStringCreator;
import org.springframework.rules.constraint.*;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.constraint.property.PropertyValueConstraint;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * A factory for creating rules.
 * 
 * @author Keith Donald
 */
public class Rules extends ConstraintsAccessor implements Constraint, PropertyConstraintProvider, Validator,
		InitializingBean {
	private static final Log logger = LogFactory.getLog(Rules.class);

	private Class domainObjectType;

    /** All constraints keyed by property name. */
	private Map propertiesConstraints = new HashMap();
    
    /** Used to track the order in which rules are added so they can be evaluated
     * in that same sequence.
     */
    private List orderedConstraints = new ArrayList();

	public Rules() {

	}

	public Rules(Class domainObjectClass) {
		setDomainObjectType(domainObjectClass);
	}

	public Rules(Class domainObjectClass, Map propertiesConstraints) {
		setDomainObjectType(domainObjectClass);
		setPropertiesConstraints(propertiesConstraints);
	}

	public void setDomainObjectType(Class domainObjectType) {
		Assert.notNull(domainObjectType, "The domainObjectType property is required");
		this.domainObjectType = domainObjectType;
	}

	public Class getDomainObjectType() {
		return domainObjectType;
	}

	public void afterPropertiesSet() {
		initRules();
	}

	protected void initRules() {

	}

	public void setPropertiesConstraints(Map propertiesConstraints) {
		for (Iterator i = propertiesConstraints.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
			String propertyName = (String)entry.getKey();
			Object value = entry.getValue();
			if (value instanceof List) {
				add(propertyName, (Constraint[])((List)value).toArray(new Constraint[0]));
			}
			else if (value instanceof PropertyConstraint) {
				add((PropertyConstraint)value);
			}
			else if (value instanceof Constraint) {
				add(propertyName, (Constraint)value);
			}
		}
	}

    /**
     * Put a constraint into the collection.  Store it in the map under the property
     * name and add it to the ordered list.
     * 
     * @param constraint to add
     */
	private void putPropertyConstraint(PropertyConstraint constraint) {
		And and = new And();
		and.add(constraint);
		if (logger.isDebugEnabled()) {
			logger.debug("Putting constraint for property '" + constraint.getPropertyName() + "', constraint -> ["
					+ constraint + "]");
		}
        PropertyConstraint compoundConstraint = new CompoundPropertyConstraint(and);
		propertiesConstraints.put(constraint.getPropertyName(), compoundConstraint);
        orderedConstraints.add( compoundConstraint );
	}

	public PropertyConstraint getPropertyConstraint(String property) {
		if (propertiesConstraints.isEmpty()) {
			initRules();
		}
		return (PropertyConstraint)propertiesConstraints.get(property);
	}

	public Iterator iterator() {
        if (orderedConstraints.isEmpty()) {
            initRules();
        }
		return orderedConstraints.iterator();
	}

	/**
	 * Adds the provided bean property expression (constraint) to the list of
	 * constraints for the constrained property.
	 * 
	 * @param constraint
	 *            the bean property expression
	 * @return this, to support chaining.
	 */
	public Rules add(PropertyConstraint constraint) {
		CompoundPropertyConstraint and = (CompoundPropertyConstraint)propertiesConstraints.get(constraint
				.getPropertyName());
		if (and == null) {
			putPropertyConstraint(constraint);
		}
		else {
			and.add(constraint);
		}
		return this;
	}

	/**
	 * Adds a value constraint for the specified property.
	 * 
	 * @param propertyName
	 *            The property name.
	 * @param valueConstraint
	 *            The value constraint.
	 */
	public void add(String propertyName, Constraint valueConstraint) {
		add(new PropertyValueConstraint(propertyName, valueConstraint));
	}

	/**
	 * Adds a value constraint for the specified property.
	 * 
	 * @param propertyName
	 *            The property name.
	 * @param valueConstraint
	 *            The value constraint.
	 */
	public void add(String propertyName, Constraint[] valueConstraints) {
		add(new PropertyValueConstraint(propertyName, all(valueConstraints)));
	}

	public void addRequired(String propertyName) {
		add(propertyName, required());
	}

	public void addRequired(String propertyName, Constraint otherConstraints) {
		add(propertyName, and(required(), otherConstraints));
	}

	public void addMaxLength(String propertyName, int maxLength) {
		add(propertyName, maxLength(maxLength));
	}

	public void addMinLength(String propertyName, int minLength) {
		add(propertyName, minLength(minLength));
	}

	public void addRange(String propertyName, Range range) {
		add(propertyName, range);
	}

	/**
	 * Adds the provided compound predicate, composed of BeanPropertyExpression
	 * objects, as a bean property constraint.
	 * 
	 * @param compoundPredicate
	 */
	public void add(CompoundConstraint compoundPredicate) {
		add(new CompoundPropertyConstraint(compoundPredicate));
	}

	public boolean test(Object bean) {
		for (Iterator i = orderedConstraints.iterator(); i.hasNext();) {
			PropertyConstraint propertyConstraint = (PropertyConstraint)i.next();
			if (!propertyConstraint.test(bean)) {
				return false;
			}
		}
		return true;
	}

	public boolean supports(Class type) {
		return this.domainObjectType.isAssignableFrom(type);
	}

	public void validate(final Object bean, final Errors errors) {

	}

	public String toString() {
		return new ToStringCreator(this).append("domainObjectClass", domainObjectType).append("propertyRules",
				propertiesConstraints).toString();
	}

}