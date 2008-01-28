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
package org.springframework.rules.constraint;

import org.springframework.rules.support.NumberComparator;

import junit.framework.TestCase;

/**
 * Testcase for Range
 * 
 * @author Peter De Bruycker
 */
public class RangeTests extends TestCase {
	public void testWithCustomComparator() {
		Range range = new Range(Integer.valueOf(0), Integer.valueOf(10), NumberComparator.INSTANCE);
		
		assertTrue(range.test(Double.valueOf(5.5)));
	}
}
