/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.rules.constraint;

import org.springframework.core.closure.Constraint;
import org.springframework.core.closure.support.AbstractConstraint;
import org.springframework.util.Assert;

public class IfTrue extends AbstractConstraint {

	private Constraint constraint;

	private Constraint mustBeTrueConstraint;

	private Constraint elseTrueConstraint;

	public IfTrue(Constraint constraint, Constraint mustAlsoBeTrue) {
		Assert.notNull(constraint, "The constraint that may be true is required");
		Assert.notNull(mustAlsoBeTrue, "The constraint that must be true IF the first constraint is true is required");
		this.constraint = constraint;
		this.mustBeTrueConstraint = mustAlsoBeTrue;
	}

	public IfTrue(Constraint constraint, Constraint mustAlsoBeTrue, Constraint elseMustAlsoBeTrue) {
		Assert.notNull(constraint, "The constraint that may be true is required");
		Assert.notNull(mustAlsoBeTrue, "The constraint that must be true IF the first constraint is true is required");
		this.constraint = constraint;
		this.mustBeTrueConstraint = mustAlsoBeTrue;
		this.elseTrueConstraint = elseMustAlsoBeTrue;
	}

	public boolean test(Object argument) {
		if (constraint.test(argument))
			return mustBeTrueConstraint.test(argument);

		if (elseTrueConstraint != null)
			return elseTrueConstraint.test(argument);

        return true;
	}

}