package org.springframework.richclient.command.support;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ApplicationDialog;

/**
 * Open a dialog.
 *
 * @author Jan Hoskens
 *
 */
public class DialogCommand extends ActionCommand {

	private ApplicationDialog dialog;

	/**
	 * Set the dialog to open.
	 */
	public void setDialog(ApplicationDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doExecuteCommand() {
		dialog.showDialog();
	}
}