package org.springframework.richclient.samples.showcase.dialog;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.dialog.TitledApplicationDialog;

public class BasicTitledApplicationDialog extends TitledApplicationDialog {

	protected JComponent createTitledDialogContentPane() {
		JPanel contentPane = new JPanel();
		JLabel label = new JLabel(getMessage("basicTitledApplicationDialog.content.label"));
		contentPane.add(label);
		return contentPane;
	}

	protected boolean onFinish() {
		return true;
	}

}
