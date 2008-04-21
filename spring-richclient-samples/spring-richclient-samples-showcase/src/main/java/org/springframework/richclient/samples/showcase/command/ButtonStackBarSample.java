package org.springframework.richclient.samples.showcase.command;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.CommandGroupFactoryBean;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.dialog.ApplicationDialog;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

public class ButtonStackBarSample extends ApplicationDialog {

	private String[] commandIds = new String[] { "basicApplicationDialogCommand", "basicConfirmationDialogCommand",
			"basicInputApplicationDialogCommand" };

	@Override
	protected JComponent createDialogContentPane() {
		JPanel panel = new JPanel(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC , FormFactory.RELATED_GAP_COLSPEC, new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT,
				ColumnSpec.DEFAULT_GROW)}, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC }));
		CommandManager commandManager = Application.instance().getActiveWindow().getCommandManager();
		List<Object> members = new ArrayList<Object>();
		for (int i = 0; i < commandIds.length; i++) {
			members.add(commandManager.getCommand(commandIds[i]));
		}

		CellConstraints cc = new CellConstraints();

		CommandGroupFactoryBean commandGroupFactory = new CommandGroupFactoryBean("buttonBar", members.toArray());
		panel.add(new JLabel(getMessage("buttonBar.label")), cc.xy(1, 1));
		panel.add(commandGroupFactory.getCommandGroup().createButtonBar(), cc.xyw(1, 3, 3));

		JTextField toolbarTextField = new JTextField(20);
		toolbarTextField.setText("input");
		members.add(toolbarTextField);
		commandGroupFactory = new CommandGroupFactoryBean("buttonStack", members.toArray());
		panel.add(new JLabel(getMessage("buttonStack.label")), cc.xy(1, 5));
		panel.add(commandGroupFactory.getCommandGroup().createButtonStack(), cc.xy(3, 5));

		return panel;
	}

	@Override
	protected boolean onFinish() {
		return true;
	}
}