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
package org.springframework.binding.value.support;

import javax.swing.event.ListDataEvent;

import org.easymock.IArgumentMatcher;

/**
 * Custom ArgumentMatcher for EasyMock.
 * 
 * @author Peter De Bruycker
 */
public class ListDataEventArgumentMatcher implements IArgumentMatcher {

	private ListDataEvent expected;

	public ListDataEventArgumentMatcher(ListDataEvent expected) {
		this.expected = expected;
	}
	
	public void appendTo(StringBuffer sb) {
		sb.append("javax.swing.event.ListDataEvent[");
		
		sb.append("type=").append(expected.getType()).append(", ");
		sb.append("index0=").append(expected.getIndex0()).append(", ");
		sb.append("index1=").append(expected.getIndex1()).append(", ");
		
		sb.append("]");
	}

	public boolean matches(Object value) {
		if (!(value instanceof ListDataEvent)) {
			return false;
		}

		ListDataEvent actual = (ListDataEvent) value;

		boolean matches = true;
		matches = matches && actual.getSource().equals(expected.getSource());
		matches = matches && actual.getType() == expected.getType();
		matches = matches && actual.getIndex0() == expected.getIndex0();
		matches = matches && actual.getIndex1() == expected.getIndex1();

		return matches;
	}

}
