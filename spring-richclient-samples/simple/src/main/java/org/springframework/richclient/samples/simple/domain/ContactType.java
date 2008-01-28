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
package org.springframework.richclient.samples.simple.domain;

import org.springframework.core.enums.ShortCodedLabeledEnum;

/**
 * This class provides the Enums to indicate the type of contact, personal or business.
 * @author Larry Streepy
 */
public class ContactType extends ShortCodedLabeledEnum {

	/** Indicates a personal contact. */
	public static final ContactType PERSONAL = new ContactType(0, "Personal");

	/** Indicates a business contact. */
	public static final ContactType BUSINESS = new ContactType(1, "Business");

	/**
	 * Private constructor because this is a typesafe enum!
	 */
	private ContactType(int code, String label) {
		super(code, label);
	}

	/**
	 * Override this to provide a better name in the combobox.
	 */
	public String toString() {
		return getLabel();
	}
}
