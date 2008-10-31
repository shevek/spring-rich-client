package org.springframework.richclient.form.binding.swing.date;


import java.util.Date;

import javax.swing.JComponent;

import com.toedter.calendar.JDateChooser;

/**
 * Testcase for <code>JCalendarDateFieldBinder</code> and
 * <code>JCalendarDateFieldBinding</code>
 * 
 * @author Peter De Bruycker
 */
public class JCalendarDateFieldBinderTests extends AbstractDateFieldBindingTestCase {

	protected AbstractDateFieldBinder createBinder() {
		return new JCalendarDateFieldBinder();
	}

	protected Date getValue(JComponent dateField) {
		return ((JDateChooser) dateField).getDate();
	}

	protected boolean isReadOnly(JComponent dateField) {
		return !((JDateChooser) dateField).isEnabled();
	}

	protected void setValue(JComponent dateField, Date date) {
		((JDateChooser) dateField).setDate(date);
	}

}
