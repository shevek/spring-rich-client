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

import java.util.Collection;
import java.util.Iterator;

import org.springframework.core.closure.Closure;
import org.springframework.core.closure.Constraint;

/**
 * Algorithms accessor support class, for convenient extending by subclasses.
 * 
 * @author Keith Donald
 */
public abstract class AlgorithmsAccessor {
	protected Algorithms getAlgorithms() {
		return Algorithms.instance();
	}

	public Object findFirst(Collection collection, Constraint constraint) {
		return getAlgorithms().findFirst(collection, constraint);
	}

	public Object findFirst(Iterator it, Constraint constraint) {
		return getAlgorithms().findFirst(it, constraint);
	}

	public Collection findAll(Collection collection, Constraint constraint) {
		return getAlgorithms().findAll(collection, constraint);
	}

	public Collection findAll(Iterator it, Constraint constraint) {
		return getAlgorithms().findAll(it, constraint);
	}

	public boolean allTrue(Collection collection, Constraint constraint) {
		return getAlgorithms().allTrue(collection, constraint);
	}

	public boolean allTrue(Iterator it, Constraint constraint) {
		return getAlgorithms().allTrue(it, constraint);
	}

	public boolean anyTrue(Collection collection, Constraint constraint) {
		return getAlgorithms().anyTrue(collection, constraint);
	}

	public boolean anyTrue(Iterator it, Constraint constraint) {
		return getAlgorithms().anyTrue(it, constraint);
	}

	public void forEach(Collection collection, Closure closure) {
		getAlgorithms().forEach(collection, closure);
	}

	public void forEach(Iterator it, Closure closure) {
		getAlgorithms().forEach(it, closure);
	}

}