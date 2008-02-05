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
package org.springframework.richclient.core;

import org.springframework.core.enums.ShortCodedLabeledEnum;

/**
 * A typesafe enum representing different levels of severity. Each enum has an
 * associated label, which may be useful for defining resources such as messages
 * and icons in properties files.
 *
 */
public class Severity extends ShortCodedLabeledEnum {

	/** The label associated with the info level. */
	public static final String INFO_LABEL = "info";

	/** The label associated with the warning level. */
	public static final String WARNING_LABEL = "warning";

	/** The label associated with the error level. */
	public static final String ERROR_LABEL = "error";

	private static final long serialVersionUID = 86569930382195510L;

	/** Info-level severity. */
	public static final Severity INFO = new Severity(0, INFO_LABEL);

	/** Warning-level severity. */
	public static final Severity WARNING = new Severity(50, WARNING_LABEL);

	/** Error-level severity. */
	public static final Severity ERROR = new Severity(100, ERROR_LABEL);

	/**
	 * Constructor.
	 *
	 * @param magnitude how does it relate to other {@link Severity} levels, a
	 * higher magnitude means more severe.
	 * @param label label to associate with, may be used to access resources
	 * like messages/icons.
	 */
	protected Severity(int magnitude, String label) {
		super(magnitude, label);
	}

}