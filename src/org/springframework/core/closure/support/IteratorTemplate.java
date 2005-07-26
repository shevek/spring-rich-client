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
package org.springframework.core.closure.support;

import java.util.Collection;
import java.util.Iterator;

/**
 * Simple process template that iterates over elements.
 * @author Keith Donald
 */
public class IteratorTemplate extends AbstractElementGeneratorWorkflow {
	private Collection collection;

	private Iterator it;

	public IteratorTemplate(Collection collection) {
		this.collection = collection;
	}

	public IteratorTemplate(Iterator it) {
		super(true);
		this.it = it;
	}

	protected void doSetup() {
		if (this.collection != null) {
			this.it = this.collection.iterator();
		}
	}

	protected boolean hasMoreWork() {
		return it.hasNext();
	}

	protected Object doWork() {
		return it.next();
	}
}