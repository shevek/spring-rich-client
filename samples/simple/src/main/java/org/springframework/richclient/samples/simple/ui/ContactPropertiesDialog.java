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

import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.samples.simple.domain.Contact;
import org.springframework.richclient.samples.simple.domain.ContactDataStore;

/**
 * This is a dialog for editing the properties of a Contact object. It is a simple "form
 * backed" dialog, meaning that the body of the dialog is provided from a "form backed"
 * dialog page. The Ok (finish) button will be wired into the "page complete" state of the
 * dialog page, which in turn gets its state from the automatic validation of the
 * properties on the form.
 * 
 * @author Larry Streepy
 * @see FormBackedDialogPage
 * @see ContactForm
 * 
 */
public class ContactPropertiesDialog extends TitledPageApplicationDialog implements ActionCommandExecutor {

    /** Our page Id, used for configuring messages. */
    private static final String PROPERTIES_PAGE_ID = "contactProperties";

    /** The form model used to manage instances of the Contact object. */
    private ValidatingFormModel formModel;

    /** The form that displays the form model. */
    private Form form;

    /** Are we creating a new Contact or editing an existing one? */
    private boolean creatingNew = false;

    /** The contact object we are processing. */
    private Contact contact;

    /** The data store holding all our contacts. */
    private ContactDataStore contactDataStore;

    /**
     * Constructor.
     */
    public ContactPropertiesDialog() {
        setCloseAction(CloseAction.DISPOSE);

        setContact(null);
        formModel = FormModelHelper.createFormModel(contact);
        form = new ContactForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);
        setDialogPage(page);
    }

    /**
     * Initialize using the configured contact.
     */
    private void initFormObject() {
        form.setFormObject(contact);

        if( creatingNew ) {
            getMessage("contactProperties.new.title");
            setTitle(getMessage("contactProperties.new.title"));
        } else {
            String title = getMessage("contactProperties.edit.title", new Object[] { contact.getFirstName(),
                    contact.getLastName() });
            setTitle(title);
        }
    }

    /**
     * Set the Contact object on which to operate. If the provided objec is null, then
     * configure to handle the creation of a new object.
     * 
     * @param contact to operate upon, may be null
     */
    public void setContact( Contact contact ) {
        if( contact == null ) {
            creatingNew = true;
            this.contact = new Contact();
        } else {
            creatingNew = false;
            this.contact = contact;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.dialog.ApplicationDialog#onFinish()
     */
    protected boolean onFinish() {
        formModel.commit(); // Commit all changes to the model object

        Contact contact = (Contact) formModel.getFormObject();

        // Update the persistent store with the new/modified object.
        String eventType;
        if( creatingNew ) {
            eventType = LifecycleApplicationEvent.CREATED;
            getContactDataStore().add(contact);
        } else {
            eventType = LifecycleApplicationEvent.MODIFIED;
            getContactDataStore().update(contact);
        }

        // And notify the rest of the application of the change
        getApplicationContext().publishEvent(new LifecycleApplicationEvent(eventType, contact));

        return true;
    }

    /**
     * Handle a dialog cancellation request. Get use confirmation before discarding
     * unsaved changes.
     */
    protected void onCancel() {
        // Warn the user if they are about to discard their changes
        if( formModel.isDirty() ) {
            String msg = getMessage(PROPERTIES_PAGE_ID + ".dirtyCancelMessage");
            String title = getMessage(PROPERTIES_PAGE_ID + ".dirtyCancelTitle");
            ConfirmationDialog dlg = new ConfirmationDialog(title, msg) {

                protected void onConfirm() {
                    ContactPropertiesDialog.super.onCancel();
                }
            };
            dlg.showDialog();
        } else {
            super.onCancel();
        }
    }

    /**
     * Execute this dialog. Simply show the dialog using the configured object. Note that
     * when execute gets called from the newContactCommand, no call to
     * {@link #setContact(Contact)} will have been made. So, this method resets the edit
     * state upon exit.
     */
    public void execute() {
        setParent(getActiveWindow().getControl());
        initFormObject();
        showDialog();

        // At this point, the user is done interacting with the dialog. So, we can
        // reset our object.
        setContact(null);
    }

    /**
     * @return the contactDataStore
     */
    public ContactDataStore getContactDataStore() {
        return contactDataStore;
    }

    /**
     * @param contactDataStore the contactDataStore to set
     */
    public void setContactDataStore( ContactDataStore contactDataStore ) {
        this.contactDataStore = contactDataStore;
    }
}
