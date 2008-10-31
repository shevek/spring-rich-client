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
package org.springframework.rules.factory;

import org.springframework.rules.closure.Closure;
import org.springframework.rules.constraint.Constraint;
import org.springframework.rules.closure.support.AlgorithmsAccessor;
import org.springframework.rules.closure.support.ClosureChain;
import org.springframework.rules.closure.support.IfBlock;
import org.springframework.util.Assert;

/**
 * A factory for easing the construction and composition of closure (blocks of
 * executable code).
 * 
 * @author Keith Donald
 */
public class Closures extends AlgorithmsAccessor {

	private static Closures INSTANCE = new Closures();

	public Closures() {
	}

	public static Closures instance() {
		return INSTANCE;
	}

	public static void load(Closures sharedInstance) {
		Assert.notNull(sharedInstance, "The global closures factory cannot be null");
		INSTANCE = sharedInstance;
	}

	public Closure chain(Closure firstFunction, Closure secondFunction) {
		return new ClosureChain(firstFunction, secondFunction);
	}

	public Closure chain(Closure[] functionsToChain) {
		return new ClosureChain(functionsToChain);
	}

	public Closure ifTrue(Constraint constraint, Closure closure) {
		return new IfBlock(constraint, closure);
	}

}