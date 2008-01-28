package org.springframework.richclient.form.binding.swing.date;


import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

import com.toedter.calendar.JDateChooser;

public class JCalendarDateFieldBinder extends AbstractDateFieldBinder {

	public JCalendarDateFieldBinder() {
		super(new String[] { DATE_FORMAT });
	}

	protected JComponent createControl(Map context) {
		return new JDateChooser();
	}

	protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
		Assert.isTrue(control instanceof JDateChooser, "Control must be an instance of JDateChooser.");
		JCalendarDateFieldBinding binding = new JCalendarDateFieldBinding((JDateChooser) control, formModel,
				formPropertyPath);
		applyContext(binding, context);

		return binding;
	}

}
