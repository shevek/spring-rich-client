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
package org.springframework.richclient.form.binding.swing.date;

import java.util.Date;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Testcase for <code>JXDatePickerDateFieldBinder</code> and
 * <code>JXDatePickerDateFieldBinding</code>
 * 
 * @author Peter De Bruycker
 */
public class JXDatePickerDateFieldBinderTests extends AbstractDateFieldBindingTestCase {

	protected AbstractDateFieldBinder createBinder() {
		return new JXDatePickerDateFieldBinder();
	}

	protected Date getValue(JComponent dateField) {
		return ((JXDatePicker) dateField).getDate();
	}

	protected void setValue(JComponent dateField, Date date) {
		((JXDatePicker) dateField).setDate(date);
	}

	protected boolean isReadOnly(JComponent dateField) {
		return !((JXDatePicker) dateField).isEditable();
	}
}
