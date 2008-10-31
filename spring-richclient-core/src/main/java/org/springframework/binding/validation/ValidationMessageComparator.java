/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.validation;

import java.util.Comparator;

import org.springframework.util.comparator.NullSafeComparator;

/**
 * Comparator that compares ValidationMessages. Comparison is done by timestamp
 * (desc) then property name then severity then message.
 *
 * @author Oliver Hutchison
 */
public class ValidationMessageComparator implements Comparator {

	/**
	 * A shared default instance of this comparator.
	 */
	public static Comparator INSTANCE = new NullSafeComparator(new ValidationMessageComparator(), true);

	protected ValidationMessageComparator() {
	}

	public int compare(Object o1, Object o2) {
		ValidationMessage m1 = (ValidationMessage) o1;
		ValidationMessage m2 = (ValidationMessage) o2;
		int c;
		if (m1.getTimestamp() == m2.getTimestamp()) {
			c = NullSafeComparator.NULLS_HIGH.compare(m1.getProperty(), m2.getProperty());
			if (c == 0) {
				c = m1.getSeverity().compareTo(m2.getSeverity());
				if (c == 0) {
					c = m1.getMessage().compareTo(m2.getMessage());
				}
			}
		}
		else {
			c = (m1.getTimestamp() > m2.getTimestamp()) ? -1 : 1;
		}
		return c;
	}

}