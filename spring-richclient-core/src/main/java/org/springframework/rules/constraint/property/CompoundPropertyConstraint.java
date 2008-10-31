/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.rules.constraint.property;

import java.util.Iterator;

import org.springframework.rules.constraint.Constraint;
import org.springframework.rules.constraint.CompoundConstraint;
import org.springframework.rules.constraint.AbstractConstraint;

/**
 * Abstract base class for unary predicates which compose other predicates.
 * 
 * @author Keith Donald
 */
public class CompoundPropertyConstraint implements PropertyConstraint {

	private CompoundConstraint compoundConstraint;

	public String getPropertyName() {
		PropertyConstraint e = (PropertyConstraint)compoundConstraint.iterator().next();
		return e.getPropertyName();
	}

	public boolean isDependentOn(final String propertyName) {
		return new AbstractConstraint() {
			public boolean test(Object o) {
				return ((PropertyConstraint)o).isDependentOn(propertyName);
			}
		}.anyTrue(compoundConstraint.iterator());
	}
	
	public boolean isCompoundRule() {
		return true;
	}

	public Constraint getPredicate() {
		return compoundConstraint;
	}

	/**
	 * Constructs a compound predicate with no initial members. It is expected
	 * the client will call "add" to add individual predicates.
	 */
	public CompoundPropertyConstraint(CompoundConstraint compoundConstraint) {
		this.compoundConstraint = compoundConstraint;
		this.compoundConstraint.validateTypeSafety(PropertyConstraint.class);
	}

	/**
	 * Add the specified predicate to the set of predicates aggregated by this
	 * compound predicate.
	 * 
	 * @param predicate
	 *            the predicate to add
	 * @return A reference to this, to support chaining.
	 */
	public CompoundPropertyConstraint add(PropertyConstraint constraint) {
		this.compoundConstraint.add(constraint);
		return this;
	}

	/**
	 * Return an iterator over the aggregated predicates.
	 * 
	 * @return An iterator
	 */
	public Iterator iterator() {
		return compoundConstraint.iterator();
	}

	public boolean test(Object bean) {
		return compoundConstraint.test(bean);
	}

	public String toString() {
		return compoundConstraint.toString();
	}

}