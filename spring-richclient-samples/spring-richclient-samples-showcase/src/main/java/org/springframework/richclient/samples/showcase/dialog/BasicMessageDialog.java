package org.springframework.richclient.samples.showcase.dialog;

import org.springframework.binding.validation.support.DefaultValidationMessage;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.dialog.MessageDialog;

public class BasicMessageDialog extends MessageDialog {

	public BasicMessageDialog() {
		super("Title", new DefaultValidationMessage("Property", Severity.ERROR, "message"));
	}
}
