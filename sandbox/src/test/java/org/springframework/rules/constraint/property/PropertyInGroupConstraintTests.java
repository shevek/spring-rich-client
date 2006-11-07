/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.rules.constraint.property;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * @author Mathias Broekelmann
 * 
 */
public class PropertyInGroupConstraintTests extends TestCase {

	public void testConstraintWithObjectArray() {
		MyTestBean bean = new MyTestBean();
		AbstractPropertyConstraint constraint = new PropertyInGroupConstraint("property", "arrayvalues");
		assertFalse(constraint.test(bean));
		bean.setProperty("value1");
		assertFalse(constraint.test(bean));
		bean.setArrayvalues(new String[] { "value1", "value2" });
		assertTrue(constraint.test(bean));
		bean.setArrayvalues(new String[] { "value2" });
		assertFalse(constraint.test(bean));
		bean.setProperty("value3");
		assertFalse(constraint.test(bean));
		bean.setArrayvalues(new String[] { "value3" });
		assertTrue(constraint.test(bean));
		bean.setArrayvalues(null);
		assertFalse(constraint.test(bean));
	}

	public void testConstraintWithObjectCollection() {
		MyTestBean bean = new MyTestBean();
		AbstractPropertyConstraint constraint = new PropertyInGroupConstraint("property", "collectionvalues");
		assertFalse(constraint.test(bean));
		bean.setProperty("value1");
		assertFalse(constraint.test(bean));
		bean.setCollectionvalues(Arrays.asList(new String[] { "value1", "value2" }));
		assertTrue(constraint.test(bean));
		bean.setCollectionvalues(Arrays.asList(new String[] { "value2" }));
		assertFalse(constraint.test(bean));
		bean.setProperty("value3");
		assertFalse(constraint.test(bean));
		bean.setCollectionvalues(Arrays.asList(new String[] { "value3" }));
		assertTrue(constraint.test(bean));
		bean.setCollectionvalues(null);
		assertFalse(constraint.test(bean));
	}

	private static class MyTestBean {
		private String property;

		private String[] arrayvalues;

		private Collection collectionvalues;

		/**
		 * @return the collectionvalues
		 */
		public Collection getCollectionvalues() {
			return collectionvalues;
		}

		/**
		 * @param collectionvalues the collectionvalues to set
		 */
		public void setCollectionvalues(Collection collectionvalues) {
			this.collectionvalues = collectionvalues;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public String[] getArrayvalues() {
			return arrayvalues;
		}

		public void setArrayvalues(String[] values) {
			this.arrayvalues = values;
		}
	}
}
