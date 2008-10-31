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
package org.springframework.rules.constraint.property;

import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.rules.constraint.Constraint;
import org.springframework.util.Assert;

/**
 * A constraint that returns the result of a <code>boolean</code>
 * expression that tests a variable bean property value against a predicate
 * (constraint). For example: <code>pet.age is required</code>
 *
 * @author Keith Donald
 */
public class PropertyValueConstraint extends AbstractPropertyConstraint implements Constraint {

	private Constraint valueConstraint;

	/**
	 * Creates a BeanPropertyValueConstraint.
	 *
	 * @param propertyName
	 *            The constrained property.
	 * @param valueConstraint
	 *            The property value constraint (tester).
	 */
	public PropertyValueConstraint(String propertyName, Constraint valueConstraint) {
		super(propertyName);
		Assert.notNull(valueConstraint, "valueConstraint is required");
		Assert.isTrue(!(valueConstraint instanceof PropertyConstraint),
				"valueConstraint cannot be a PropertyConstraint - it must be a plain constraint that tests property values!");
		this.valueConstraint = valueConstraint;
	}

	protected boolean test(PropertyAccessStrategy domainObjectAccessStrategy) {
		return valueConstraint.test(domainObjectAccessStrategy.getPropertyValue(getPropertyName()));
	}

	public Constraint getConstraint() {
		return valueConstraint;
	}

	public String toString() {
		return valueConstraint.toString();
	}
}