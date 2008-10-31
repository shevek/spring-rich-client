package org.springframework.richclient.form.binding.swing.date;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;

import com.toedter.calendar.JDateChooser;

/**
 * Binds a <cod>Date</code> to a JCalendar <code>JDateChooser</code>
 * 
 * @author Peter De Bruycker
 */
public class JCalendarDateFieldBinding extends AbstractDateFieldBinding {

	private JDateChooser dateChooser;

	public JCalendarDateFieldBinding(JDateChooser dateChooser, FormModel formModel, String formPropertyPath) {
		super(formModel, formPropertyPath);
		this.dateChooser = dateChooser;
	}

	protected void valueModelChanged(Object newValue) {
		dateChooser.setDate((Date) newValue);
	}

	protected JComponent doBindControl() {
		if (getDateFormat() != null) {
			dateChooser.setDateFormatString(getDateFormat());
		}

		dateChooser.setDate((Date) getValue());

		dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				controlValueChanged(dateChooser.getDate());
			}
		});

		return dateChooser;
	}

	protected void readOnlyChanged() {
		dateChooser.setEnabled(isEnabled() && !isReadOnly());
	}

	protected void enabledChanged() {
		dateChooser.setEnabled(isEnabled() && !isReadOnly());
	}

}
