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
package org.springframework.richclient.forms;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;

import org.springframework.enums.ShortCodedEnum;

/**
 * @author Keith Donald
 */
public abstract class ValueCommitPolicy extends ShortCodedEnum {
	public static final ValueCommitPolicy AS_YOU_TYPE = new ValueCommitPolicy(0) {
		public void configure(JFormattedTextField textField, DefaultFormatter formatter) {
			textField.setFocusLostBehavior(JFormattedTextField.PERSIST);
			formatter.setOverwriteMode(false);
			formatter.setAllowsInvalid(true);
			formatter.setCommitsOnValidEdit(true);
		}
	};

	public static final ValueCommitPolicy FOCUS_LOST = new ValueCommitPolicy(1) {
		public void configure(JFormattedTextField textField, DefaultFormatter formatter) {
			textField.setFocusLostBehavior(JFormattedTextField.COMMIT);
			formatter.setOverwriteMode(false);
			formatter.setAllowsInvalid(true);
			formatter.setCommitsOnValidEdit(false);
		}
	};

	public static final ValueCommitPolicy ON_SUBMIT = new ValueCommitPolicy(2) {
		public void configure(JFormattedTextField textField, DefaultFormatter formatter) {
			textField.setFocusLostBehavior(JFormattedTextField.PERSIST);
			formatter.setOverwriteMode(false);
			formatter.setAllowsInvalid(true);
			formatter.setCommitsOnValidEdit(false);
		}
	};

	private ValueCommitPolicy(int value) {
		super(value);
	}

	public abstract void configure(JFormattedTextField textField, DefaultFormatter formatter);
}