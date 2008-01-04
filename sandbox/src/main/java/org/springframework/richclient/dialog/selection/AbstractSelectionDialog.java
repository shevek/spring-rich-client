package org.springframework.richclient.dialog.selection;

import java.awt.Window;

import javax.swing.JComponent;

import org.springframework.core.closure.Closure;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractSelectionDialog extends ApplicationDialog {
	private String description;

	private Closure onSelectAction;

	public AbstractSelectionDialog(String title, Window parent) {
		super(title, parent);
	}

	protected JComponent createDialogContentPane() {
		TableLayoutBuilder builder = new TableLayoutBuilder();

		JComponent selectionComponent = createSelectionComponent();
		Assert.state(selectionComponent != null, "createSelectionComponent cannot return null");

		if (StringUtils.hasText(description)) {
			builder.cell(getComponentFactory().createLabelFor(description, selectionComponent));
			builder.row();
		}

		builder.cell(selectionComponent);

		return builder.getPanel();
	}

	protected abstract JComponent createSelectionComponent();

	protected boolean onFinish() {
		onSelect(getSelectedObject());
		return true;
	}

	public void setDescription(String desc) {
		Assert.isTrue(!isControlCreated(), "Set the description before the control is created.");

		description = desc;
	}

	protected abstract Object getSelectedObject();

	protected void onSelect(Object selection) {
		if (onSelectAction != null) {
			onSelectAction.call(selection);
		}
		else {
			throw new UnsupportedOperationException("Either provide an onSelectAction or override the onSelect method");
		}
	}

	public void setOnSelectAction(Closure onSelectAction) {
		this.onSelectAction = onSelectAction;
	}
}
