/*
 * Copyright 2002-2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.dialog;

import java.awt.Image;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.form.Form;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A TitledApplicationDialog that delegates to a single DialogPage for its
 * title, content and messages.
 * 
 * @author oliverh
 */
public abstract class TitledPageApplicationDialog extends TitledApplicationDialog {

	private DialogPage dialogPage;

	private PropertyChangeListener dialogPagePropertyChangeHandler = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (Messagable.MESSAGE_PROPERTY.equals(evt.getPropertyName())) {
				update();
			}
			else if (DialogPage.PAGE_COMPLETE_PROPERTY.equals(evt.getPropertyName())) {
				setEnabled(dialogPage.isPageComplete());
			}
			else {
				update();
			}
		}
	};

	private Image titlePaneImage;

	private String titlePaneTitle;

	/**
	 * Default constructor. Make sure to call {@link #setDialogPage(DialogPage)}
	 * prior to using this dialog.
	 */
	public TitledPageApplicationDialog() {
		super();
	}

	public TitledPageApplicationDialog(DialogPage dialogPage) {
		super();
		setDialogPage(dialogPage);
	}

	public TitledPageApplicationDialog(Form form, Window parent) {
		this(new FormBackedDialogPage(form), parent);
	}

	public TitledPageApplicationDialog(DialogPage dialogPage, Window parent) {
		super(dialogPage.getTitle(), parent);
		setDialogPage(dialogPage);
	}

	public TitledPageApplicationDialog(DialogPage dialogPage, Window parent, CloseAction closeAction) {
		super(dialogPage.getTitle(), parent, closeAction);
		setDialogPage(dialogPage);
	}

	protected void setDialogPage(DialogPage dialogPage) {
		Assert.notNull(dialogPage, "The single dialog page to display is required");
		this.dialogPage = dialogPage;
	}

	protected DialogPage getDialogPage() {
		return dialogPage;
	}

	protected JComponent createTitledDialogContentPane() {
		dialogPage.addPropertyChangeListener(dialogPagePropertyChangeHandler);
		update();
		return dialogPage.getControl();
	}

	protected Message getDescription() {
		return new DefaultMessage(dialogPage.getDescription());
	}

	protected void update() {
		if (!StringUtils.hasText(getTitle())) {
			setTitle(dialogPage.getTitle());
		}
		updateTitlePane();
		updateMessagePane();
	}

	protected void updateTitlePane() {
		super.setTitlePaneTitle(titlePaneTitle != null ? titlePaneTitle : dialogPage.getTitle());
		super.setTitlePaneImage(titlePaneImage != null ? titlePaneImage : dialogPage.getImage());
		setDescription(dialogPage.getDescription());
	}
	
	protected void updateMessagePane() {
		setMessage(dialogPage.getMessage());
	}

	/**
	 * Sets the image to use in the title pane. Normally the image is provided
	 * by the current dialog page, but this method allows for overriding this.
	 * <p>
	 * If the image passed is null, the image of the dialog page will be used.
	 * @param image the image
	 * @see TitledApplicationDialog#setTitlePaneImage(Image)
	 */
	public void setTitlePaneImage(Image image) {
		titlePaneImage = image;
		super.setTitlePaneImage(image);
	}

	/**
	 * Sets the title to use in the title pane. Normally the title is provided
	 * by the current dialog page, but this method allows for overriding this.
	 * <p>
	 * If the title passed is null, the title of the dialog page will be used.
	 * @param title the title
	 * @see TitledApplicationDialog#setTitlePaneTitle(String)
	 */
	public void setTitlePaneTitle(String title) {
		titlePaneTitle = title;
		super.setTitlePaneTitle(title);
	}
}