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
package org.springframework.core.closure.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.core.closure.Constraint;

/**
 * Convenient class that uses the Algorithms on its own instance.
 */
public abstract class AbstractConstraint extends AlgorithmsAccessor implements Constraint, Serializable {

    /**
     * @see AlgorithmsAccessor#allTrue(Collection, Constraint)
     */
	public boolean allTrue(Collection collection) {
		return allTrue(collection, this);
	}

    /**
     * @see AlgorithmsAccessor#allTrue(Iterator, Constraint)
     */
	public boolean allTrue(Iterator it) {
		return allTrue(it, this);
	}

    /**
     * @see AlgorithmsAccessor#anyTrue(Collection, Constraint)
     */
	public boolean anyTrue(Collection collection) {
		return anyTrue(collection, this);
	}

    /**
     * @see AlgorithmsAccessor#anyTrue(Iterator, Constraint)
     */    
	public boolean anyTrue(Iterator it) {
		return anyTrue(it, this);
	}

    /**
     * @see AlgorithmsAccessor#findAll(Collection, Constraint)
     */
	public Collection findAll(Collection collection) {
		return findAll(collection, this);
	}

    /**
     * @see AlgorithmsAccessor#findAll(Iterator, Constraint)
     */
	public Collection findAll(Iterator it) {
		return findAll(it, this);
	}

    /**
     * @see AlgorithmsAccessor#findFirst(Collection, Constraint)
     */
	public Object findFirst(Collection collection) {
		return findFirst(collection, this);
	}

    /**
     * @see AlgorithmsAccessor#findFirst(Iterator, Constraint)
     */
	public Object findFirst(Iterator it) {
		return findFirst(it, this);
	}
}