/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JLabel;

import org.springframework.richclient.core.DescribedElement;
import org.springframework.richclient.core.LabelInfo;
import org.springframework.richclient.core.VisualizedElement;

/**
 * Provides metadata related to the visualization of a form property and
 * convenience methods for configuring GUI components using the metadata.
 *
 * @author Oliver Hutchison
 */
public interface FieldFace extends DescribedElement, VisualizedElement {

	/**
	 * The name of the property in human readable form, typically used for
	 * validation messages.
	 */
	String getDisplayName();

	/**
	 * A short caption describing the property, typically used for tool tips.
	 */
	String getCaption();

	/**
	 * A longer caption describing the property.
	 */
	String getDescription();

	/**
	 * The text, mnemonic and mnemonicIndex for any labels created for the
	 * property.
	 */
	LabelInfo getLabelInfo();

	/**
	 * The icon that is used for any labels created for this property.
	 */
	Icon getIcon();

	/**
	 * Configures the supplied JLabel using LabelInfo and Icon.
	 */
	void configure(JLabel label);

	/**
	 * Configures the supplied button using LabelInfo and Icon.
	 */
	void configure(AbstractButton button);
}