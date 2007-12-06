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

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JComponent;

import net.sf.nachocalendar.components.DateField;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

/**
 * Binds a <cod>Date</code> to a NachoCalendar <code>DateField</code>
 * 
 * @author Geoffrey De Smet
 * @author Benoit Xhenseval (added dateFormat setting)
 */
public class NachoCalendarDateFieldBinder extends AbstractDateFieldBinder {

	public static final String SHOW_OK_CANCEL_KEY = "showOkCancel";

	public NachoCalendarDateFieldBinder() {
		super(new String[] { SHOW_OK_CANCEL_KEY, DATE_FORMAT });
	}

	protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
		Assert.isTrue(control instanceof DateField, "Control must be an instance of DateField.");
		NachoCalendarDateFieldBinding binding = new NachoCalendarDateFieldBinding((DateField) control, formModel,
				formPropertyPath);
		applyContext(binding, context);
		return binding;
	}

	protected void applyContext(NachoCalendarDateFieldBinding binding, Map context) {
		super.applyContext(binding, context);
		
		if (context.containsKey(SHOW_OK_CANCEL_KEY)) {
			binding.setShowOkCancel((Boolean) context.get(SHOW_OK_CANCEL_KEY));
		}
	}

	protected JComponent createControl(Map context) {
		final int preferredHeight = getComponentFactory().createComboBox().getPreferredSize().height;

		// FIXME dirty hack so the DateField has the correct height
		DateField dateField = new DateField() {
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.height = preferredHeight;

				return size;
			}
		};

		return dateField;
	}
}