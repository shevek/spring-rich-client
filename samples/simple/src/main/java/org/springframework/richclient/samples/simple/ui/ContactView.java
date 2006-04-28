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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.binding.value.ValueModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.list.ListSingleSelectionGuard;
import org.springframework.richclient.samples.simple.domain.Contact;
import org.springframework.richclient.samples.simple.domain.ContactDataStore;
import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

/**
 * This class provides the main view of the contacts. It provides a table showing the
 * contact objects and a quick filter field to narrow down the list of visible contacts.
 * Several commands are tied to the selection of the contacts table
 * <p>
 * By implementing special tag interfaces, this component will be automatically wired in
 * to certain events of interest.
 * <ul>
 * <li><b>InitializingBean</b> - when this bean has been constructed and all the
 * properties have been set, the bean factory will call the {@link #afterPropertiesSet()}
 * method. We use that call to verify that all the required configuration has been done.</li>
 * <li><b>ApplicationListener</b> - This component will be automatically registered as a
 * listener for application events.</li>
 * </ul>
 * 
 * @author Larry Streepy
 * 
 */
public class ContactView extends AbstractView implements InitializingBean, ApplicationListener {

    private final Log _logger = LogFactory.getLog(getClass());

    /**
     * The ObjectFactory for creating instances of our contact table. Initialized in the
     * context.
     */
    private ObjectFactory contactTableFactory;

    /** The object table holding our contacts. */
    private ContactTable contactTable;

    /** The data store holding all our contacts. */
    private ContactDataStore contactDataStore;

    /** This is the entry field for the name/address filter. */
    private JTextField txtFilter = new JTextField();

    /** The executor to handle the "properties" command. */
    private PropertiesExecutor propertiesExecutor = new PropertiesExecutor();

    /** The executor to handle the "delete" command. */
    private DeleteExecutor deleteExecutor = new DeleteExecutor();

    /** The command Id of the Delete command. */
    private static final String DELETE_COMMAND_ID = "deleteCommand";

    /** The group Id for the popup menu. */
    private static final String POPUP_COMMAND_ID = "contactViewPopupMenu";

    /**
     * Default constructor.
     */
    public ContactView() {
    }

    /**
     * This method is called automatically after this bean has had all its properties set
     * by the bean factory. This happens because this class implements the
     * {@link InitializingBean} interface.
     * 
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        // Verify that we have been properly configured
        Assert.state(getContactTableFactory() != null, "contactTableFactory must be set");
    }

    /**
     * Create the control for this view. This method is called by the platform in order to
     * obtain the control to add to the surrounding window and page.
     * 
     * @return component holding this view
     */
    protected JComponent createControl() {

        prepareTable(); // Start by preparing the main table

        JPanel view = new JPanel(new BorderLayout());
        JScrollPane sp = getComponentFactory().createScrollPane(contactTable.getTable());

        // Now the filter controls
        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel lblFilter = getComponentFactory().createLabel("nameAddressFilter.label");
        filterPanel.add(lblFilter, BorderLayout.WEST);
        
        String tip = getMessage("nameAddressFilter.caption");
        txtFilter.setToolTipText(tip);
        filterPanel.add(txtFilter, BorderLayout.CENTER);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        view.add(sp, BorderLayout.CENTER);
        view.add(filterPanel, BorderLayout.NORTH);

        return view;
    }

    /**
     * Register the local command executors to be associated with named commands. This is
     * called by the platform prior to making the view visible.
     */
    protected void registerLocalCommandExecutors( PageComponentContext context ) {
        context.register(GlobalCommandIds.PROPERTIES, propertiesExecutor);
        context.register(DELETE_COMMAND_ID, deleteExecutor);

    }

    /**
     * @return the contactTableFactory
     */
    public ObjectFactory getContactTableFactory() {
        return contactTableFactory;
    }

    /**
     * Set the contact table factory to use for our view. Since the table contains state,
     * we need a new instance for every instance of this view. However, the view instances
     * are not created directly in the application context, so they can not be directly
     * configured with injected objects. Each instance is configured by the platform from
     * the "viewProperties" value. Since that list is simply retained by the view
     * descriptor and then used to configure each new view instance, the application
     * context is not involved, so non-singleton bean references wouldn't generate a new
     * instance. So, using this factory mechanism allows us to generate new table
     * instances on the fly without having to directly access the context and call
     * getBean, which would break the Inversion of Control model.
     * 
     * @param contactTableFactory the contactTableFactory to set
     */
    public void setContactTableFactory( ObjectFactory contactTableFactory ) {
        this.contactTableFactory = contactTableFactory;
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

    /**
     * Prepare the table holding all the Contact objects. This table provides pretty much
     * all the functional operations within this view. Prior to calling this method the
     * {@link #setContactTable(ContactTable)} will have already been called as part of the
     * context bean creation.
     */
    private void prepareTable() {

        // Get the table instance from our factory
        try {
            contactTable = (ContactTable) getContactTableFactory().getObject();
        } catch( Exception e ) {
            _logger.error("Failed to generate new contactTable", e);
            throw new RuntimeException("Failed to generate new contactTable", e);
        }

        // Make a double click invoke the properties dialog and plugin the
        // context menu
        contactTable.setDoubleClickHandler(propertiesExecutor);

        // Get the popup menu definition from the command manager (as defined in
        // the commands-context.xml file).
        CommandGroup popup = getWindowCommandManager().getCommandGroup(POPUP_COMMAND_ID);
        contactTable.setPopupCommandGroup(popup);

        // Construct and install our filtering list. This filter will allow the user
        // to simply type data into the txtFilter (JTextField). With the configuration
        // setup below, the text entered by the user will be matched against the values
        // in the lastName and address.address1 properties of the contacts in the table.
        // The GlazedLists filtered lists is used to accomplish this.

        EventList baseList = contactTable.getBaseEventList();
        TextFilterator filterator = GlazedLists.textFilterator(new String[] { "lastName", "address.address1" });
        FilterList filterList = new FilterList(baseList, new TextComponentMatcherEditor(txtFilter, filterator));

        // Install the fully constructed (layered) list into the table
        contactTable.setFinalEventList(filterList);

        // Register to get notified when the filtered list changes
        contactTable.reportToStatusBar(getStatusBar(), "Contact", "Contacts");

        // Ensure our commands are only active when something is selected.
        // These guard objects operate by inspecting a list selection model
        // (held within a ValueModel) and then either enabling or disabling the
        // guarded object (our executors) based on the configured criteria.
        // This configuration greatly simplifies the interaction between commands
        // that require a selection on which to operate.

        ValueModel selectionHolder = contactTable.getTableSelectionHolder();
        new ListSingleSelectionGuard(selectionHolder, deleteExecutor);
        new ListSingleSelectionGuard(selectionHolder, propertiesExecutor);
    }

    /**
     * Handle an application event. This will notify us of object adds, deletes, and
     * modifications. Our object table takes care of updating itself, so we don't have
     * anything to do.
     * 
     * @param e event to process
     */
    public void onApplicationEvent( ApplicationEvent e ) {
        if( _logger.isInfoEnabled() ) {
            _logger.info("Got event: " + e);
        }
    }

    /**
     * Private inner class to handle the properties form display.
     */
    private class PropertiesExecutor extends AbstractActionCommandExecutor {

        /**
         * Execute this command.
         */
        public void execute() {
            Contact contact = contactTable.getSelectedContacts()[0];

            // Get the dialog from the application context since it is a managed bean
            ContactPropertiesDialog dlg = (ContactPropertiesDialog) getApplicationContext().getBean(
                    "contactPropertiesDialog");

            // Tell it what object to edit and execute the command
            dlg.setContact(contact);
            dlg.execute();
        }
    }

    /**
     * Private class to handle the delete command. Note that due to the configuration
     * above, this executor is only enabled when exactly one contact is selected in the
     * table. Thus, we don't have to protect against being executed with an incorrect
     * state.
     */
    private class DeleteExecutor extends AbstractActionCommandExecutor {
        public DeleteExecutor() {
            getApplicationServices().configure(this, "delete");
        }

        /**
         * Execute this command.
         */
        public void execute() {
            // We know exactly one contact will be selected at this time because
            // of the guards put in place in prepareTable.
            final Contact contact = contactTable.getSelectedContacts()[0];

            // Query the user to be sure they want to do this
            String title = getMessage("contact.confirmDelete.title");
            String message = getMessage("contact.confirmDelete.message");
            ConfirmationDialog dlg = new ConfirmationDialog(title, message) {
                protected void onConfirm() {
                    // Delete the object from the persistent store.
                    getContactDataStore().delete(contact);

                    // And notify the rest of the application of the change
                    getApplicationContext().publishEvent(
                            new LifecycleApplicationEvent(LifecycleApplicationEvent.DELETED, contact));
                }
            };

            dlg.showDialog();
        }
    }
}
