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
package org.springframework.richclient.settings;

import org.springframework.core.enums.StringCodedLabeledEnum;

/**
 * @author Peter De Bruycker
 */
public class TestEnum extends StringCodedLabeledEnum {

	public static final TestEnum ENUM1 = new TestEnum("1", "enum1");

	public static final TestEnum ENUM2 = new TestEnum("2", "enum2");

	private TestEnum(String code, String label) {
		super(code, label);
	}
}
