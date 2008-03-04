package org.springframework.richclient.samples.showcase.dialog;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.dialog.ApplicationDialog;

/**
 * <p>
 * Very simple dialog showing only the basics. We are using all default settings
 * so the {@link JDialog} internally used should be disposed on closing.
 * </p>
 *
 * <p>
 * Note that we create a {@link JPanel} in the
 * {@link #createDialogContentPane()} without saving a reference in the class.
 * If we would have a reference field pointing at the panel, we should implement
 * the {@link #disposeDialogContentPane()} method.
 * </p>
 *
 * @author Jan Hoskens
 *
 */
public class BasicApplicationDialog extends ApplicationDialog {

	protected JComponent createDialogContentPane() {
		JPanel contentPane = new JPanel();
		JLabel label = new JLabel(getMessage("basicApplicationDialog.content.label"));
		contentPane.add(label);
		return contentPane;
	}

	protected boolean onFinish() {
		return true;
	}

}