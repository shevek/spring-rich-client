package org.springframework.richclient.samples.showcase.command;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ApplicationDialog;

public class ToolbarSample extends ApplicationDialog {

	@Override
	protected JComponent createDialogContentPane() {
		JPanel panel = new JPanel();
		CommandGroup commandGroup = new CommandGroup();
		commandGroup.add(createCommand("first"));
		panel.add(commandGroup.createToolBar());
		return panel;
	}

	private AbstractCommand createCommand(final String name) {
		return new ActionCommand(name) {

			@Override
			protected void doExecuteCommand() {
				System.out.println("Execute command: " + name);
			}

		};
	}

	@Override
	protected boolean onFinish() {
		return true;
	}

}