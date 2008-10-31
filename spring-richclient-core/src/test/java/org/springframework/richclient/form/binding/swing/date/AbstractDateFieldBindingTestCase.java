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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FieldMetadata;
import org.springframework.richclient.form.binding.swing.BindingAbstractTests;

/**
 * Skeleton test for <code>AbstractDateFieldBinder</code> and
 * <code>AbstractDateFieldBinding</code> subclasses.
 * 
 * @author Geoffrey De Smet
 * @author Peter De Bruycker
 */
public abstract class AbstractDateFieldBindingTestCase extends BindingAbstractTests {

	private AbstractDateFieldBinder binder;

	private AbstractDateFieldBinding binding;

	private JComponent dateField;

	protected String setUpBinding() {
		Map context = new HashMap();

		binder = createBinder();
		binding = (AbstractDateFieldBinding) binder.bind(fm, "dateProperty", context);
		dateField = binding.getControl();

		return "dateProperty";
	}

	protected abstract AbstractDateFieldBinder createBinder();

	public void testInitialValue() {
		Date date = createDate(1981, 10, 16);
		vm.setValue(date);
		assertNotNull(vm.getValue());
	}

	private Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		Date date = calendar.getTime();
		return date;
	}

	public final void testComponentTracksEnabledChanges() {
		assertEquals(true, dateField.isEnabled());
		fm.setEnabled(false);
		assertEquals(false, dateField.isEnabled());
		fm.setEnabled(true);
		assertEquals(true, dateField.isEnabled());
	}

	public final void testComponentTracksReadOnlyChanges() {
		FieldMetadata state = fm.getFieldMetadata("dateProperty");
		assertEquals(true, !isReadOnly(dateField));
		state.setReadOnly(true);
		assertEquals(false, !isReadOnly(dateField));
		state.setReadOnly(false);
		assertEquals(true, !isReadOnly(dateField));
	}
	
	protected abstract boolean isReadOnly(JComponent dateField);

	protected abstract Date getValue(JComponent dateField);

	protected abstract void setValue(JComponent dateField, Date date);

	public final void testComponentUpdatesValueModel() {
		Date date1 = createDate(1981, 10, 16);
		setValue(dateField, date1);
		assertEquals(date1, (Date)vm.getValue());
		setValue(dateField, null);
		assertEquals(null, (Date)vm.getValue());
		Date date2 = createDate(1999, 11, 31);
		setValue(dateField, date2);
		assertEquals(date2, (Date)vm.getValue());
	}

	public final void testValueModelUpdatesComponent() {
		Date date1 = createDate(1981, 10, 16);
		vm.setValue(date1);
		assertEquals(date1, getValue(dateField));
		vm.setValue(null);
		assertEquals(null, getValue(dateField));
		Date date2 = createDate(1999, 11, 31);
		vm.setValue(date2);
		assertEquals(date2, getValue(dateField));
	}
	
	/*
	 * trim the dates to seconds
	 */
	protected void assertEquals(Date date1, Date date2) {
		if(date1 == null || date2 == null) {
			super.assertEquals(date1, date2);
			return;
		}
		
		long l1 = date1.getTime();
		l1 = ((long)Math.round(l1/1000))*1000;
		long l2 = date2.getTime();
		l2 = ((long)Math.round(l1/1000))*1000;
		
		assertEquals(l1, l2);
	}
}
