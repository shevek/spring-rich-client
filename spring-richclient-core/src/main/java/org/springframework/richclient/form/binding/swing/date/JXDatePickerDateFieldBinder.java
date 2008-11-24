package org.springframework.richclient.form.binding.swing.date;

import org.jdesktop.swingx.JXDatePicker;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

import javax.swing.*;
import java.util.Map;

public class JXDatePickerDateFieldBinder extends AbstractDateFieldBinder {

	public JXDatePickerDateFieldBinder() {
		super(new String[] { DATE_FORMAT });
	}
	
	protected JComponent createControl(Map context) {
		return new JXDatePicker();
	}

	protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
		Assert.isTrue(control instanceof JXDatePicker, "Control must be an instance of JXDatePicker.");
		JXDatePickerDateFieldBinding binding = new JXDatePickerDateFieldBinding((JXDatePicker) control, formModel,
				formPropertyPath);
		applyContext(binding, context);

		return binding;
	}
}
