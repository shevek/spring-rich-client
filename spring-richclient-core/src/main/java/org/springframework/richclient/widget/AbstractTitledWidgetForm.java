package org.springframework.richclient.widget;

import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.binding.form.FormModel;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.awt.*;

public abstract class AbstractTitledWidgetForm extends AbstractWidgetForm implements
		TitledWidget {
	private Message description = new DefaultMessage(RcpSupport.getMessage(
			"titledWidget", "defaultMessage", RcpSupport.TEXT), Severity.INFO);

	private TitlePane titlePane = new TitlePane(2);

	public AbstractTitledWidgetForm(FormModel model) {
		super(model);
	}

	public AbstractTitledWidgetForm(FormModel model, String formId) {
		super(model, formId);
	}

	public void setTitle(String title) {
		this.titlePane.setTitle(title);
	}

	public void setImage(Image image) {
		this.titlePane.setImage(image);
	}

	public void setMessage(Message message) {
		if (message != null)
			titlePane.setMessage(message);
		else
			titlePane.setMessage(getDescription());
	}

	protected Message getDescription() {
		return description;
	}

	public void setDescription(String longDescription) {
		this.description = new DefaultMessage(longDescription);
		setMessage(this.description);
	}

	public JComponent getComponent() {
		JPanel titlePaneContainer = new JPanel(new BorderLayout());
		titlePaneContainer.add(titlePane.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);

		JPanel pageControl = new JPanel(new BorderLayout());
		pageControl.add(titlePaneContainer, BorderLayout.NORTH);
		JComponent content = createFormControl();
		GuiStandardUtils.attachDialogBorder(content);
		pageControl.add(content);

		setMessage(getDescription());

		return pageControl;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.titlePane.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String txt,
			PropertyChangeListener listener) {
		this.titlePane.addPropertyChangeListener(txt, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.titlePane.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String txt,
			PropertyChangeListener listener) {
		this.titlePane.removePropertyChangeListener(txt, listener);
	}

	public boolean canClose() {
		return true;
	}

	public java.util.List<? extends AbstractCommand> getCommands() {
		return Collections.emptyList();
	}

	public void onAboutToHide() {
	}

	public void onAboutToShow() {
	}

	public void setCaption(String shortDescription) {
		setTitle(shortDescription);
	}

	public void setBeanName(String name) {
		setId(name);
	}
}

