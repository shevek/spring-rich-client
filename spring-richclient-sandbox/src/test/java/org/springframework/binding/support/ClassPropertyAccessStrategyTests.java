/*
 * Copyright 2007 the original author or authors.
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
package org.springframework.binding.support;

import java.util.List;
import java.util.Map;

import org.springframework.binding.PropertyMetadataAccessStrategy;

/**
 * @author Arne Limburg
 */
public class ClassPropertyAccessStrategyTests extends AbstractPropertyAccessStrategyTests {

	protected AbstractPropertyAccessStrategy createPropertyAccessStrategy(Object target) {
		return new ClassPropertyAccessStrategy(target.getClass());
	}

	protected boolean isStrictNullHandlingEnabled() {
		return false;
	}

	protected void doSetUp() throws Exception {
		super.doSetUp();
		pas.getDomainObjectHolder().setValue(testBean);
	}

	/**
	 * Test the metadata on type/readability/writeability.
	 */
	public void testMetaData() {
		PropertyMetadataAccessStrategy mas = pas.getMetadataAccessStrategy();

		assertPropertyMetadata(mas, "simpleProperty", String.class, true, true);
		assertPropertyMetadata(mas, "mapProperty", Map.class, true, true);
		assertPropertyMetadata(mas, "listProperty", List.class, true, true);
		assertPropertyMetadata(mas, "readOnly", Object.class, true, false);
		assertPropertyMetadata(mas, "writeOnly", Object.class, false, true);

		// type/readable/writeable depend not on the property being not null
		assertPropertyMetadata(mas, "nestedProperty.simpleProperty", String.class, true, true);

	}

	public void testBeanThatImplementsPropertyChangePublisher() {
		pas = new ClassPropertyAccessStrategy(TestBeanWithPCP.class);
		super.testBeanThatImplementsPropertyChangePublisher();
	}
}