package org.springframework.richclient.samples.showcase.util;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.util.Assert;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

public abstract class AbstractReporterTitledApplicationDialog extends TitledApplicationDialog {

	private ActionCommand clearTextAreaCommand;

	private JTextArea messageArea;

	/**
	 * Returns the textArea to append info.
	 */
	public JTextArea getMessageArea() {
		return messageArea;
	}

	public ActionCommand getClearTextAreaCommand() {
		if (this.clearTextAreaCommand == null) {
			this.clearTextAreaCommand = new ActionCommand(getClearTextAreaCommandFaceDescriptorId()) {

				protected void doExecuteCommand() {
					getMessageArea().setText(null);
				}
			};
			getCommandConfigurer().configure(this.clearTextAreaCommand);
		}
		return this.clearTextAreaCommand;
	}

	protected String getClearTextAreaCommandFaceDescriptorId() {
		return "reporterDialog.clearTextAreaCommand";
	}

	abstract protected Reporter getReporter();

	@Override
	protected JComponent createTitledDialogContentPane() {
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		Reporter reporter = getReporter();
		Assert.notNull(reporter);
		reporter.setMessageArea(messageArea);

		JPanel panel = new JPanel(new FormLayout(new ColumnSpec[] {
				new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
				FormFactory.UNRELATED_GAP_COLSPEC, new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW) },
				new RowSpec[] { new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW), }));
		CellConstraints cc = new CellConstraints();
		panel.add(reporter.getControl(), cc.xy(1, 1));
		AbstractCommand[] reporterCommands = reporter.getReporterCommands();
		AbstractCommand[] commandStack = new AbstractCommand[reporterCommands.length + 1];
		System.arraycopy(reporterCommands, 0, commandStack, 0, reporterCommands.length);
		commandStack[reporterCommands.length] = getClearTextAreaCommand();
		CommandGroup commandGroup = CommandGroup.createCommandGroup(commandStack);
		panel.add(commandGroup.createButtonStack(), cc.xy(3, 1));
		JScrollPane scrollPane = new JScrollPane(messageArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, scrollPane);
		scrollPane.setPreferredSize(new Dimension(200, 100));
		return splitPane;
	}

	@Override
	protected boolean onFinish() {
		return true;
	}

}