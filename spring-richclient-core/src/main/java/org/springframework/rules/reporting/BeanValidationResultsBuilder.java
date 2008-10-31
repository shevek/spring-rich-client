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
package org.springframework.rules.reporting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.rules.constraint.Constraint;
import org.springframework.rules.constraint.property.PropertyConstraint;

/**
 * @author Keith Donald
 */
public class BeanValidationResultsBuilder extends ValidationResultsBuilder
		implements BeanValidationResults {

	private String currentProperty;

	private Object currentPropertyValue;

	private Map beanResults = new HashMap();

	private PropertyAccessStrategy beanPropertyAccessStrategy;

	public BeanValidationResultsBuilder(Object bean) {
		super();
		if (bean instanceof PropertyAccessStrategy) {
			this.beanPropertyAccessStrategy = (PropertyAccessStrategy) bean;
		}
		else {
			this.beanPropertyAccessStrategy = new BeanPropertyAccessStrategy(
					bean);
		}
	}

	public Map getResults() {
		return Collections.unmodifiableMap(beanResults);
	}

	public PropertyResults getResults(String propertyName) {
		return (PropertyResults) beanResults.get(propertyName);
	}

	public int getViolatedCount() {
		int count = 0;
		Iterator it = beanResults.values().iterator();
		while (it.hasNext()) {
			count += ((PropertyResults) it.next()).getViolatedCount();
		}
		return count;
	}

	protected void constraintViolated(Constraint constraint) {
		if (logger.isDebugEnabled()) {
			logger.debug("[Done] collecting results for property '"
					+ getCurrentPropertyName() + "'.  Constraints violated: ["
					+ constraint + "]");
		}
		PropertyResults results = new PropertyResults(getCurrentPropertyName(),
				getCurrentPropertyValue(), constraint);
		beanResults.put(getCurrentPropertyName(), results);
	}

	protected void constraintSatisfied() {
		if (logger.isDebugEnabled()) {
			logger.debug("[Done] collecting results for property '"
					+ getCurrentPropertyName() + "'.  All constraints met.");
		}
	}

	public String getCurrentPropertyName() {
		return currentProperty;
	}

	public Object getCurrentPropertyValue() {
		return currentPropertyValue;
	}

	public void setCurrentBeanPropertyExpression(
			PropertyConstraint expression) {
		this.currentProperty = expression.getPropertyName();
		this.currentPropertyValue = getPropertyValue(this.currentProperty);
		super.clear();
	}

	private Object getPropertyValue(String propertyName) {
		return beanPropertyAccessStrategy.getPropertyValue(propertyName);
	}

}