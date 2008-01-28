/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.samples.simple.ui;

import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.samples.simple.domain.Contact;
import org.springframework.richclient.samples.simple.domain.ContactDataStore;
import org.springframework.util.Assert;

/**
 * This is a dialog for editing the properties of a Contact object. It is a simple "form backed" dialog, meaning that
 * the body of the dialog is provided from a "form backed" dialog page. The Ok (finish) button will be wired into the
 * "page complete" state of the dialog page, which in turn gets its state from the automatic validation of the
 * properties on the form.
 * @author Larry Streepy
 * @see FormBackedDialogPage
 * @see ContactForm
 */
public class ContactPropertiesDialog extends TitledPageApplicationDialog {

	/** The form that allows for editing the contact. */
	private Form form;

	/** Are we creating a new Contact or editing an existing one? */
	private boolean creatingNew = false;

	/** The data store holding all our contacts, used to add a new contact. */
	private ContactDataStore dataStore;

	public ContactPropertiesDialog(ContactDataStore dataStore) {
		this(null, dataStore);
	}

	public ContactPropertiesDialog(Contact contact, ContactDataStore dataStore) {
		Assert.notNull(dataStore, "The data store is required to edit a contact");
		if (contact == null) {
			creatingNew = true;
			contact = new Contact();
		}
		setCloseAction(CloseAction.DISPOSE);
		form = new ContactForm(contact);
		setDialogPage(new FormBackedDialogPage(form));
		this.dataStore = dataStore;
	}

	private Contact getEditingContact() {
		return (Contact) form.getFormModel().getFormObject();
	}

	protected void onAboutToShow() {
		if (creatingNew) {
			getMessage("contactProperties.new.title");
			setTitle(getMessage("contactProperties.new.title"));
		}
		else {
			Contact contact = getEditingContact();
			String title = getMessage("contactProperties.edit.title", new Object[] { contact.getFirstName(),
					contact.getLastName() });
			setTitle(title);
		}
	}

	protected boolean onFinish() {
		// commit any buffered edits to the model
		form.getFormModel().commit();
		// Update the persistent store with the new/modified object.
		String eventType;
		if (creatingNew) {
			eventType = LifecycleApplicationEvent.CREATED;
			dataStore.add(getEditingContact());
		}
		else {
			eventType = LifecycleApplicationEvent.MODIFIED;
		}
		// And notify the rest of the application of the change
		getApplicationContext().publishEvent(new LifecycleApplicationEvent(eventType, getEditingContact()));
		return true;
	}

	protected void onCancel() {
		// Warn the user if they are about to discard their changes
		if (form.getFormModel().isDirty()) {
			String msg = getMessage("contactProperties.dirtyCancelMessage");
			String title = getMessage("contactProperties.dirtyCancelTitle");
			ConfirmationDialog dlg = new ConfirmationDialog(title, msg) {
				protected void onConfirm() {
					ContactPropertiesDialog.super.onCancel();
				}
			};
			dlg.showDialog();
		}
		else {
			super.onCancel();
		}
	}
}
