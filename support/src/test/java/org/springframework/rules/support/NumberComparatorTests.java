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
package org.springframework.rules.support;

import junit.framework.TestCase;

/**
 * Testcase for NumberComparator
 * 
 * @author Peter De Bruycker
 */
public class NumberComparatorTests extends TestCase {

	public void testCompareIntegers() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(0, comparator.compare(Integer.valueOf(1), Integer.valueOf(1)));
		assertEquals(1, comparator.compare(Integer.valueOf(10), Integer.valueOf(1)));
		assertEquals(-1, comparator.compare(Integer.valueOf(1), Integer.valueOf(10)));
	}

	public void testCompareLongs() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(0, comparator.compare(Long.valueOf(1l), Long.valueOf(1l)));
		assertEquals(1, comparator.compare(Long.valueOf(10l), Long.valueOf(1l)));
		assertEquals(-1, comparator.compare(Long.valueOf(1l), Long.valueOf(10l)));
	}

	public void testCompareFloats() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(0, comparator.compare(Float.valueOf(1.5f), Float.valueOf(1.5f)));
		assertEquals(1, comparator.compare(Float.valueOf(10.22f), Float.valueOf(1.5f)));
		assertEquals(-1, comparator.compare(Float.valueOf(1.5f), Float.valueOf(10.22f)));
	}

	public void testCompareDoubles() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(0, comparator.compare(Double.valueOf(1.5), Double.valueOf(1.5)));
		assertEquals(1, comparator.compare(Double.valueOf(10.22), Double.valueOf(1.5)));
		assertEquals(-1, comparator.compare(Double.valueOf(1.5), Double.valueOf(10.22)));
	}

	public void testCompareDifferentTypesEqual() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(0, comparator.compare(Double.valueOf(1.0), Integer.valueOf(1)));
		assertEquals(0, comparator.compare(Double.valueOf(1.0), Long.valueOf(1l)));
		assertEquals(0, comparator.compare(Double.valueOf(1.0), Float.valueOf(1.0f)));

		assertEquals(0, comparator.compare(Integer.valueOf(1), Long.valueOf(1l)));
		assertEquals(0, comparator.compare(Integer.valueOf(1), Float.valueOf(1.0f)));
		assertEquals(0, comparator.compare(Integer.valueOf(1), Double.valueOf(1.0)));

		assertEquals(0, comparator.compare(Long.valueOf(1l), Integer.valueOf(1)));
		assertEquals(0, comparator.compare(Long.valueOf(1l), Float.valueOf(1.0f)));
		assertEquals(0, comparator.compare(Long.valueOf(1l), Double.valueOf(1.0)));

		assertEquals(0, comparator.compare(Float.valueOf(1.0f), Integer.valueOf(1)));
		assertEquals(0, comparator.compare(Float.valueOf(1.0f), Long.valueOf(1l)));
		assertEquals(0, comparator.compare(Float.valueOf(1.0f), Double.valueOf(1.0)));
	}

	public void testCompareDifferentTypesSmaller() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(-1, comparator.compare(Double.valueOf(1.0), Integer.valueOf(10)));
		assertEquals(-1, comparator.compare(Double.valueOf(1.0), Long.valueOf(10l)));
		assertEquals(-1, comparator.compare(Double.valueOf(1.0), Float.valueOf(10.0f)));

		assertEquals(-1, comparator.compare(Integer.valueOf(1), Long.valueOf(10l)));
		assertEquals(-1, comparator.compare(Integer.valueOf(1), Float.valueOf(10.0f)));
		assertEquals(-1, comparator.compare(Integer.valueOf(1), Double.valueOf(10.0)));

		assertEquals(-1, comparator.compare(Long.valueOf(1l), Integer.valueOf(10)));
		assertEquals(-1, comparator.compare(Long.valueOf(1l), Float.valueOf(10.0f)));
		assertEquals(-1, comparator.compare(Long.valueOf(1l), Double.valueOf(10.0)));

		assertEquals(-1, comparator.compare(Float.valueOf(1.0f), Integer.valueOf(10)));
		assertEquals(-1, comparator.compare(Float.valueOf(1.0f), Long.valueOf(10l)));
		assertEquals(-1, comparator.compare(Float.valueOf(1.0f), Double.valueOf(10.0)));
	}

	public void testCompareDifferentTypesLarger() {
		NumberComparator comparator = NumberComparator.INSTANCE;

		assertEquals(1, comparator.compare(Double.valueOf(10.0), Integer.valueOf(1)));
		assertEquals(1, comparator.compare(Double.valueOf(10.0), Long.valueOf(1l)));
		assertEquals(1, comparator.compare(Double.valueOf(10.0), Float.valueOf(1.0f)));

		assertEquals(1, comparator.compare(Integer.valueOf(10), Long.valueOf(1l)));
		assertEquals(1, comparator.compare(Integer.valueOf(10), Float.valueOf(1.0f)));
		assertEquals(1, comparator.compare(Integer.valueOf(10), Double.valueOf(1.0)));

		assertEquals(1, comparator.compare(Long.valueOf(10l), Integer.valueOf(1)));
		assertEquals(1, comparator.compare(Long.valueOf(10l), Float.valueOf(1.0f)));
		assertEquals(1, comparator.compare(Long.valueOf(10l), Double.valueOf(1.0)));

		assertEquals(1, comparator.compare(Float.valueOf(10.0f), Integer.valueOf(1)));
		assertEquals(1, comparator.compare(Float.valueOf(10.0f), Long.valueOf(1l)));
		assertEquals(1, comparator.compare(Float.valueOf(10.0f), Double.valueOf(1.0)));
	}

}
