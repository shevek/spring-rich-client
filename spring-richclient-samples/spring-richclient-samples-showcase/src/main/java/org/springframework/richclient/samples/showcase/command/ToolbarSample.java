package org.springframework.richclient.samples.showcase.command;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
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

public class ToolbarSample extends ApplicationDialog {

	private String[] commandIds = new String[] { "basicApplicationDialogCommand", "basicConfirmationDialogCommand",
			"basicInputApplicationDialogCommand" };

	@Override
	protected JComponent createDialogContentPane() {
		JPanel panel = new JPanel(new FormLayout(new ColumnSpec[] { new ColumnSpec(ColumnSpec.LEFT, Sizes.DEFAULT,
				ColumnSpec.DEFAULT_GROW) }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC }));
		CommandManager commandManager = Application.instance().getActiveWindow().getCommandManager();
		List<Object> members = new ArrayList<Object>();
		for (int i = 0; i < commandIds.length; i++) {
			members.add(commandManager.getCommand(commandIds[i]));
		}

		CellConstraints cc = new CellConstraints();

		CommandGroupFactoryBean commandGroupFactory = new CommandGroupFactoryBean("toolbar", members.toArray());
		panel.add(commandGroupFactory.getCommandGroup().createToolBar(), cc.xy(1, 1));

		JTextField toolbarTextField = new JTextField(20);
		toolbarTextField.setText("input");
		members.add(toolbarTextField);
		commandGroupFactory = new CommandGroupFactoryBean("toolbar2", members.toArray());
		panel.add(commandGroupFactory.getCommandGroup().createToolBar(), cc.xy(1, 3));

		return panel;
	}

	@Override
	protected boolean onFinish() {
		return true;
	}
}