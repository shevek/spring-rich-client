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

import javax.swing.Icon;

import org.springframework.core.enums.ShortCodedLabeledEnum;

public class Severity extends ShortCodedLabeledEnum {

	public static final Severity INFO = new Severity(0, "Info");

	public static final Severity WARNING = new Severity(1, "Warning");

	public static final Severity ERROR = new Severity(2, "Error");

	private Icon icon;

	private Severity(int magnitude, String label) {
		super(magnitude, label);
	}

	public Icon getIcon() {
		return icon;
	}
}