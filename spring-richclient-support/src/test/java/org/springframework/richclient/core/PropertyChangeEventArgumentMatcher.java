/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.core;

import java.beans.PropertyChangeEvent;

import org.easymock.IArgumentMatcher;
import org.springframework.util.ObjectUtils;

/**
 * Custom ArgumentMatcher for EasyMock.
 * 
 * @author Peter De Bruycker
 */
public class PropertyChangeEventArgumentMatcher implements IArgumentMatcher {

	private PropertyChangeEvent expected;

	public PropertyChangeEventArgumentMatcher(PropertyChangeEvent expected) {
		this.expected = expected;
	}
	
	public void appendTo(StringBuffer sb) {
		sb.append("java.beans.PropertyChangeEvent[");
		
		sb.append("source=").append(expected.getSource()).append(", ");
		sb.append("propertyName=").append(expected.getPropertyName()).append(", ");
		sb.append("oldValue=").append(expected.getOldValue()).append(", ");
		sb.append("newValue=").append(expected.getNewValue()).append("");
		
		sb.append("]");
	}

	public boolean matches(Object value) {
		if (!(value instanceof PropertyChangeEvent)) {
			return false;
		}

		PropertyChangeEvent actual = (PropertyChangeEvent) value;

		boolean matches = true;
		matches = matches && actual.getSource().equals(expected.getSource());
		matches = matches && actual.getPropertyName().equals(expected.getPropertyName());
		matches = matches && ObjectUtils.nullSafeEquals(actual.getOldValue(), expected.getOldValue());
		matches = matches && ObjectUtils.nullSafeEquals(actual.getNewValue(), expected.getNewValue());

		return matches;
	}

}
