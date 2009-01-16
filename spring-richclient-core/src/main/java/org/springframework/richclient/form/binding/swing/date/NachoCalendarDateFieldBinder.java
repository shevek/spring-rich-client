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

import net.sf.nachocalendar.components.DateField;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Binds a <cod>Date</code> to a NachoCalendar <code>DateField</code>
 * 
 * @author Geoffrey De Smet
 * @author Benoit Xhenseval (added dateFormat setting)
 */
public class NachoCalendarDateFieldBinder extends AbstractDateFieldBinder {

	public static final String ALLOWS_INVALID_KEY = "allowsInvalid";
	public static final String ANTI_ALIASED_KEY = "antiAliased";
	public static final String FIRST_DAY_OF_WEEK_KEY = "firstDayOfWeek";
	public static final String HEADER_RENDERER_KEY = "headerRenderer";
	public static final String MODEL_KEY = "model";
	public static final String PRINT_MOON_KEY = "printMoon";
	public static final String RENDERER_KEY = "renderer";
	public static final String SHOW_OK_CANCEL_KEY = "showOkCancel";
	public static final String SHOW_TODAY_KEY = "showToday";
	public static final String TODAY_CAPTION_KEY = "todayCaption";
	public static final String WORKING_DAYS_KEY = "workingDays";
	public static final String SHOW_WEEKNUMBERS_KEY = "showWeekNumbers";

	public NachoCalendarDateFieldBinder() {
		super(new String[] { SHOW_OK_CANCEL_KEY, ALLOWS_INVALID_KEY, PRINT_MOON_KEY, FIRST_DAY_OF_WEEK_KEY,
				HEADER_RENDERER_KEY, MODEL_KEY, RENDERER_KEY, SHOW_TODAY_KEY, TODAY_CAPTION_KEY, ANTI_ALIASED_KEY,
				WORKING_DAYS_KEY, DATE_FORMAT, SHOW_WEEKNUMBERS_KEY });
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
        BeanWrapper wrapper = new BeanWrapperImpl(binding.getControl());
		Object dateFormat = context.get(DATE_FORMAT);
		// remove DATE_FORMAT temporarily since it is handled in the super class
		context.remove(DATE_FORMAT);
		wrapper.setPropertyValues(context);
		if (dateFormat != null) {
			// restore the original context
			context.put(DATE_FORMAT, dateFormat);
        }
	}

	protected JComponent createControl(Map context) {
		final int preferredHeight = getComponentFactory().createComboBox().getPreferredSize().height;

		boolean showWeekNumbers = false;
		if (context.containsKey(SHOW_WEEKNUMBERS_KEY)) {
			showWeekNumbers = (Boolean) context.get(SHOW_WEEKNUMBERS_KEY);
			context.remove(SHOW_WEEKNUMBERS_KEY);
		}


		DateField dateField = new DateField(showWeekNumbers) {

			private static final long serialVersionUID = 1L;

			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.height = preferredHeight;

				return size;
			}
		};

		return dateField;
	}
}