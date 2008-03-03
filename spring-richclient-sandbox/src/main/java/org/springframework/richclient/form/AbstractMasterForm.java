/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.richclient.form;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import org.springframework.beans.BeanUtils;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.binding.validation.support.DefaultValidationResultsModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.DeepCopyBufferedCollectionValueModel;
import org.springframework.binding.value.support.DirtyTrackingValueModel;
import org.springframework.binding.value.support.ObservableEventList;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.table.ListSelectionListenerSupport;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * Abstract base for the Master form of a Master/Detail pair. Derived types must implement
 * two methods:
 * <dt>{@link #createDetailForm}</dt>
 * <dd>To construct the detail half of this master/detail pair</dd>
 * <dt>{@link #getSelectionModel()}</dt>
 * <dd>To return the selection model of the object rendering the list</dd>
 * <p>
 * <strong>Important note:</strong> Any subclass that implements
 * {@link AbstractForm#createControl()} <strong>MUST</strong> call {@link #configure()}
 * prior to its work in order to have the detail form properly prepared.
 *
 * @author Larry Streepy
 * @see AbstractDetailForm
 * @see #creatingNewObject()
 */
public abstract class AbstractMasterForm extends AbstractForm {

    /** Property name for indicating changes in our selected index. */
    public static final String SELECTION_INDEX_PROPERTY = "selectionIndex";

    /**
     * Property name for indicating changes in our "is creating new object" state.
     */
    public static final String IS_CREATING_PROPERTY = "isCreating";

    private DirtyTrackingDCBCVM collectionVM;
    private EventList rootEventList;
    private ObservableEventList masterEventList;
    private AbstractDetailForm detailForm;
    private Class detailType;
    private ActionCommand newFormObjectCommand;
    private ActionCommand deleteCommand;
    private CommandGroup commandGroup;
    private boolean confirmDelete = true;
    private ListSelectionHandler selectionHandler = new ListSelectionHandler();
    private PropertyChangeListener parentFormPropertyChangeHandler = new ParentFormPropertyChangeHandler();

    /**
     * Construct a new AbstractMasterForm using the given parent form model and property
     * path. The form model for this class will be constructed by getting the value model
     * of the specified property from the parent form model and constructing a
     * DeepCopyBufferedCollectionValueModel on top of it.
     *
     * @param parentFormModel Parent form model to access for this form's data
     * @param property containing this forms data (must be a collection or an array)
     * @param formId Id of this form
     * @param detailType Type of detail object managed by this master form
     */
    protected AbstractMasterForm(HierarchicalFormModel parentFormModel, String property, String formId, Class detailType) {
        super( formId );
        this.detailType = detailType;

        ValueModel propertyVM = parentFormModel.getValueModel( property );

        // Now construct the dirty tracking model
        Class collectionType = getMasterCollectionType( propertyVM );

        collectionVM = new DirtyTrackingDCBCVM( propertyVM, collectionType );
        ValidatingFormModel formModel = FormModelHelper.createChildPageFormModel( parentFormModel, formId,
                collectionVM);
        setFormModel( formModel );

        // Install a handler to detect when the parents form model changes
        propertyVM.addValueChangeListener(parentFormPropertyChangeHandler);
    }

    /**
     * Get the value model representing the collection we are managing. This value model
     * can be used to register vlue change listeners and to update the collection
     * contents.
     * <p>
     * You must use this method to get the value model since calling getValueModel on the
     * parent form model will not get you what you want.
     *
     * @return collection value model
     */
    public ValueModel getCollectionValueModel() {
        return collectionVM;
    }

    /**
     * Determine the type of the collection holding the detail items. This will be used to
     * create the value model for the collection.
     * <p>
     * <b>Note to Hibernate users:</b> You will most likely need to override this method
     * in order to force the use of a simple <code>List</code> class instead of the
     * default implementation that would return <code>PersistentList</code>. Creating a
     * new instance of this type would result in a somewhat misleading error regarding
     * lazy instantiation since the new PersistentList instance would not have been
     * properly initialized by Hibernate.
     *
     * @param collectionPropertyVM ValueModel holding the master collection
     * @return Type of collection to use
     */
    protected Class getMasterCollectionType(ValueModel collectionPropertyVM) {
        return collectionPropertyVM.getValue().getClass();
    }

    /**
     * Configure this master form's data and prepare the detail form.
     */
    protected void configure() {
        // Just configure a basic event list to handle our data
        installEventList( getRootEventList() );

        // Now we need to construct a subform and value model to handle the
        // detail elements of this master table

        Object detailObject = BeanUtils.instantiateClass(detailType);
        ValueModel valueHolder = new ValueHolder( detailObject );
        detailForm = createDetailForm( getFormModel(), valueHolder, masterEventList);

        // Start the form disabled and not validating until the form is actually in use.
        detailForm.setEnabled( false );
        detailForm.getFormModel().setValidating( false );

        // Wire up the monitor to track the selected index and edit state so we
        // can keep the delete and add button states up to date
        detailForm.getEditingIndexHolder().addValueChangeListener( new EditingIndexMonitor() );
        detailForm.addPropertyChangeListener( new EditStateMonitor() );
    }

    /**
     * Construct the detail half of this master/detail pair.
     *
     * @param parentFormModel
     * @param valueHolder BufferedValueModel holding an object of the type configured for
     *            this master form.
     * @param masterList The ObservableList of data to from the master form (this will
     *            constitute the editable object list for the detail form).
     */
    protected abstract AbstractDetailForm createDetailForm(HierarchicalFormModel parentFormModel,
            ValueModel valueHolder, ObservableList masterList);

    /**
     * Install an EventList for use as our master list data. The event list must have been
     * created on top of the event list obtained from {@link #getRootEventList()} so that
     * all changes are propery proxied onto the actual form data. The event list provided
     * will be wrapped in a {@link ObservableEventList}.
     *
     * @param eventList new EventList to install
     */
    protected void installEventList(EventList eventList) {
        masterEventList = new ObservableEventList( eventList );

        // Propogate this down to the detail form
        if( detailForm != null ) {
            detailForm.setMasterList(masterEventList);
        }
    }

    /**
     * Get the root event list for this model. This event list will be constructed from
     * the form objects value (assumed to be an EventList). Any subclasses that are
     * installing additional transformed lists should use this method to obtain the
     * original event list on top of which all the other lists are constructed.
     */
    protected EventList getRootEventList() {
        if( rootEventList == null ) {
            rootEventList = (EventList) getFormModel().getFormObjectHolder().getValue();
        }
        return rootEventList;
    }

    /**
     * Handle the root event list being changed externally. This is normally invoked
     * when the value model holding this forms source property has changed (which can
     * occur when the parent form object is changed). This method is normally invoked from
     * the parent form object listener.
     *
     * @see #parentFormPropertyChangeHandler
     */
    protected void handleExternalRootEventListChange() {
        if( masterEventList != null ) {
            // While we do this, we need to disable our normal list listener since it's
            // too late to interact with the user due to unsaved changes (the underlying
            // value model has already changed).
            uninstallSelectionHandler();

            // Clean up the detail form
            if( detailForm != null ) {
                detailForm.reset();
                detailForm.setSelectedIndex( -1 );
            }

            if( isControlCreated() ) {
                updateControlsForState(); // Ensure our controls are properly updated
            }

            installSelectionHandler(); // Reinstate the handler
        }
    }

    /**
     * Get the form data we are operating upon. The form object must be castable to List
     * for this to work.
     *
     * @return List The form object's value
     */
    public List getFormData() {
        return (List) getFormModel().getFormObjectHolder().getValue();
    }

    /**
     * Get the master EventList (which proxies the real form data).
     *
     * @return EventList
     */
    public ObservableEventList getMasterEventList() {
        return masterEventList;
    }

    /**
     * Get the selection model for the master list representation.
     *
     * @return selection model
     */
    protected abstract ListSelectionModel getSelectionModel();

    /**
     * Install our selection handler.
     */
    protected void installSelectionHandler() {
        ListSelectionModel lsm = getSelectionModel();
        if( lsm != null ) {
            lsm.addListSelectionListener( getSelectionHandler() );
        }
    }

    /**
     * Uninstall our selection handler.
     */
    protected void uninstallSelectionHandler() {
        ListSelectionModel lsm = getSelectionModel();
        if( lsm != null ) {
            lsm.removeListSelectionListener( getSelectionHandler() );
        }
    }

    /**
     * Indicates that we are creating a new detail object. Default behavior is to just
     * clear the selection on the master set.
     */
    public void creatingNewObject() {
        getSelectionModel().clearSelection();
    }

    /**
     * Get the selection handler for the master list. Default implementation.
     *
     * @return listener to handle master table selection events
     */
    protected ListSelectionListener getSelectionHandler() {
        return selectionHandler;
    }

    /**
     * Get the command group for the master table (the add and delete commands).
     *
     * @return command group
     */
    protected CommandGroup getCommandGroup() {
        if( commandGroup == null ) {
            commandGroup = CommandGroup.createCommandGroup( null, new AbstractCommand[] { getDeleteCommand(),
                    getNewFormObjectCommand() } );
        }
        return commandGroup;
    }

    /**
     * Return a standardized row of command buttons, right-justified and all of the same
     * size, with OK as the default button, and no mnemonics used, as per the Java Look
     * and Feel guidelines.
     */
    protected JComponent createButtonBar() {
        JComponent buttonBar = getCommandGroup().createButtonBar();
        GuiStandardUtils.attachDialogBorder( buttonBar );
        return buttonBar;
    }

    /**
     * Get the popup menu for the master table. This is built from the command group
     * returned from {@link #getCommandGroup()}.
     *
     * @return popup menu
     */
    protected JPopupMenu getPopupMenu() {
        return getCommandGroup().createPopupMenu();
    }

    /**
     * Get the action command to creating a new detail object. Note that we have to
     * override this method in order to call our own createNewDetailObjectCommand method
     * since the AbstractForm's implementation of createNewFormObjectCommand is private!
     */
    public ActionCommand getNewFormObjectCommand() {
        if( newFormObjectCommand == null ) {
            newFormObjectCommand = createNewFormObjectCommand();
        }
        return newFormObjectCommand;
    }

    /**
     * Create the "new detail object" command. This will encapsulate the action from our
     * detail forms {@link AbstractForm#getNewFormObjectCommand} as well as controlling
     * the state of the detail form.
     *
     * @return command
     */
    protected ActionCommand createNewFormObjectCommand() {
        String commandId = getNewFormObjectCommandId();
        if( !StringUtils.hasText( commandId ) ) {
            return null;
        }

        ActionCommand newDetailObjectCommand = new ActionCommand( commandId ) {
            protected void doExecuteCommand() {
                maybeCreateNewObject(); // Avoid losing user edits
            }
        };
        String scid = constructSecurityControllerId( commandId );
        newDetailObjectCommand.setSecurityControllerId( scid );
        return (ActionCommand) getCommandConfigurer().configure( newDetailObjectCommand );
    }

    /**
     * Construct the "new detail object" command id as
     * <code>new[detailTypeName]Command</code>
     *
     * @return constructed command id
     */
    protected String getNewFormObjectCommandId() {
        return "new" + StringUtils.capitalize( ClassUtils.getShortName( detailType + "Command" ) );
    }

    /**
     * Return the command to delete the currently selected item in the master set.
     *
     * @return command, created on demand
     */
    public ActionCommand getDeleteCommand() {
        if( deleteCommand == null ) {
            deleteCommand = createDeleteCommand();
        }
        return deleteCommand;
    }

    /**
     * Create the "delete object" command.
     *
     * @return command
     */
    protected ActionCommand createDeleteCommand() {
        String commandId = getDeleteCommandId();
        if( !StringUtils.hasText( commandId ) ) {
            return null;
        }

        final ActionCommand deleteCommand = new ActionCommand( commandId ) {
            protected void doExecuteCommand() {
                maybeDeleteSelectedItems();
            }
        };

        String scid = constructSecurityControllerId( commandId );
        deleteCommand.setSecurityControllerId( scid );
        return (ActionCommand) getCommandConfigurer().configure( deleteCommand );
    }

    /**
     * Get the message to present to the user when confirming the delete of selected
     * detail items. This default implementation just obtains the message with key
     * <code>&lt;formId&gt;.confirmDelete.message</code> or
     * <code>masterForm.confirmDelete.message</code>. Subclasses can use the selected
     * item(s) to construct a more meaningful message.
     */
    protected String getConfirmDeleteMessage() {
        return getMessage(new String[] { getId() + ".confirmDelete.message", "masterForm.confirmDelete.message" });
    }

    /**
     * Maybe delete the selected items. If we are configured to confirm the delete, then
     * do so. If the user confirms, then delete the selected items.
     */
    protected void maybeDeleteSelectedItems() {

        // If configured, have the user confirm the delete operation
        if( isConfirmDelete() ) {
            String title = getMessage( new String[] { getId() + ".confirmDelete.title", "masterForm.confirmDelete.title" } );
            String message = getConfirmDeleteMessage();
            ConfirmationDialog dlg = new ConfirmationDialog( title, message ) {

                protected void onConfirm() {
                    deleteSelectedItems();
                    getSelectionModel().clearSelection();
                }
            };
            dlg.showDialog();
        } else {
            deleteSelectedItems();
            getSelectionModel().clearSelection();
        }
    }

    /**
     * Delete the detail item at the specified index.
     */
    protected void deleteSelectedItems() {
        ListSelectionModel sm = getSelectionModel();

        if( sm.isSelectionEmpty() ) {
            return;
        }

        detailForm.reset();

        int min = sm.getMinSelectionIndex();
        int max = sm.getMaxSelectionIndex();

        // Loop backwards and delete each selected item in the interval
        for( int index = max; index >= min; index-- ) {
            if( sm.isSelectedIndex( index ) ) {
                getMasterEventList().remove( index );
            }
        }
    }

    /**
     * Construct the button to invoke the delete command.
     *
     * @return button
     */
    protected JButton createDeleteButton() {
        Assert.state( deleteCommand != null, "Delete command has not been created!" );
        return (JButton) deleteCommand.createButton();
    }

    /**
     * Construct the "delete detail object" command id as
     * <code>delete[DetailTypeName]Command</code>
     *
     * @return constructed command id
     */
    protected String getDeleteCommandId() {
        return "delete" + StringUtils.capitalize( ClassUtils.getShortName( detailType + "Command" ) );
    }

    /**
     * @return Returns the detailForm.
     */
    protected AbstractDetailForm getDetailForm() {
        return detailForm;
    }

    /**
     * @param form The detailForm to set.
     */
    protected void setDetailForm(AbstractDetailForm form) {
        detailForm = form;
    }

    /**
     * @return Returns the detailFormModel.
     */
    protected ValidatingFormModel getDetailFormModel() {
        return detailForm.getFormModel();
    }

    /**
     * @return Returns the detailType.
     */
    protected Class getDetailType() {
        return detailType;
    }

    /**
     * @param type The detailType to set.
     */
    protected void setDetailType(Class type) {
        detailType = type;
    }

    /**
     * Return confirm delete setting.
     * @return confirm delete setting.
     */
    public boolean isConfirmDelete() {
        return confirmDelete;
    }

    /**
     * Set confirm delete. If this is <code>true</code> then the master form will
     * confirm with the user prior to deleting a detail item.
     * @param confirmDelete
     */
    public void setConfirmDelete(boolean confirmDelete) {
        this.confirmDelete = confirmDelete;
    }

    /**
     * Deal with the user invoking a "new object" command. If we have unsaved changes,
     * then we need to query the user to ensure they want to really make the change.
     */
    protected void maybeCreateNewObject() {
        if( getDetailForm().isEditingNewFormObject() ) {
            return; // Already creating a new object, just bail
        }

        final ActionCommand detailNewObjectCommand = detailForm.getNewFormObjectCommand();

        if( getDetailForm().isDirty() ) {
            String title = getMessage( new String[] { getId() + ".dirtyNew.title", "masterForm.dirtyNew.title" } );
            String message = getMessage( new String[] { getId() + ".dirtyNew.message", "masterForm.dirtyNew.message" } );
            ConfirmationDialog dlg = new ConfirmationDialog( title, message ) {
                protected void onConfirm() {
                    // Tell both forms that we are creating a new object
                    detailNewObjectCommand.execute(); // Do subform action first
                    creatingNewObject();
                    detailForm.creatingNewObject();
                }
            };
            dlg.showDialog();
        } else {
            // Tell both forms that we are creating a new object
            detailNewObjectCommand.execute(); // Do subform action first
            creatingNewObject();
            detailForm.creatingNewObject();
        }
    }

    /**
     * Update our controls based on our state.
     */
    protected void updateControlsForState() {
        int state = getDetailForm().getEditState();
        boolean isCreating = state == AbstractDetailForm.STATE_CREATE;

        getDeleteCommand().setEnabled( getDetailForm().getEditingFormObjectIndex() >= 0 );
        getNewFormObjectCommand().setEnabled( !isCreating );

        // If we are in the CLEAR state, then we need to disable validations on the form
        getDetailFormModel().setValidating( state != AbstractDetailForm.STATE_CLEAR );
    }

    /**
     * When the results reporter is setup on the master form, we need to capture it and
     * forward it on to the detail form as well.
     */
    public ValidationResultsReporter newSingleLineResultsReporter(Messagable messageReceiver) {
        // create a resultsModel container which receives events from detail and master
        ValidationResultsModel validationResultsModel = new DefaultValidationResultsModel();
        validationResultsModel.add(getFormModel().getValidationResults());
        validationResultsModel.add(getDetailFormModel().getValidationResults());
        ValidationResultsReporter reporter = new SimpleValidationResultsReporter(validationResultsModel, messageReceiver);
        return reporter;
    }

    /**
     * Inner class to handle the list selection and installing the selection into the
     * detail form.
     */
    protected class ListSelectionHandler extends ListSelectionListenerSupport {
        /**
         * Called when nothing gets selected. Override this method to handle empty
         * selection
         */
        protected void onNoSelection() {
            maybeChangeSelection( -1 );
        }

        /**
         * Called when the user selects a single row. Override this method to handle
         * single selection
         *
         * @param index the selected row
         */
        protected void onSingleSelection(final int index) {
            maybeChangeSelection( index );
        }

        /**
         * Deal with a change in the selected index. If we have unsaved changes, then we
         * need to query the user to ensure they want to really make the change.
         *
         * @param newIndex The new selection index, may be -1 to clear the selection
         */
        protected void maybeChangeSelection(final int newIndex) {
            if( newIndex == getDetailForm().getSelectedIndex() ) {
                return;
            }
            if( getDetailForm().isDirty() ) {
                String title = getMessage( new String[] { getId() + ".dirtyChange.title", "masterForm.dirtyChange.title" } );
                String message = getMessage( new String[] { getId() + ".dirtyChange.message", "masterForm.dirtyChange.message" } );
                ConfirmationDialog dlg = new ConfirmationDialog( title, message ) {

                    protected void onConfirm() {
                        getDetailForm().setSelectedIndex( newIndex );
                    }

                    protected void onCancel() {
                        // Force the selction back
                        super.onCancel();
                        if( getDetailForm().isEditingNewFormObject() ) {
                            // Since they were editing a new object, we just
                            // need to clear the selection
                            getSelectionModel().clearSelection();
                        } else {
                            int index = getDetailForm().getSelectedIndex();
                            getSelectionModel().setSelectionInterval( index, index );
                        }
                    }

                };
                dlg.showDialog();
            } else {
                getDetailForm().setSelectedIndex( newIndex );
            }
        }
    }

    /**
     * Inner class to monitor the editing index on the detail form and update the state of
     * the delete command whenever it changes.
     */
    private class EditingIndexMonitor implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            updateControlsForState();
        }
    }

    /**
     * Inner class to monitor the edit state of the detail form and update the state of
     * the commands whenever it changes.
     */
    private class EditStateMonitor implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            updateControlsForState();
        }
    }

    /**
     * This class handles changes in the property that this master form is editing. This
     * can occur when the parent form's form object is changed. When that occurs, our
     * collection data will change automatically and we then need to update our event list
     * accordingly.
     */
    private class ParentFormPropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            handleExternalRootEventListChange();
        }

    }

    /**
     * Specialized DCBCVM to provide dirty tracking semantics. This will allow the form
     * model built on this value model to properly track our dirty status.
     */
    private class DirtyTrackingDCBCVM extends DeepCopyBufferedCollectionValueModel implements DirtyTrackingValueModel {

        private boolean dirty = false;
        private boolean oldDirty = false;

        /**
         * Constructs a new DirtyTrackingDCBCVM.
         *
         * @param wrappedModel the value model to wrap
         * @param wrappedType the class of the value contained by wrappedModel; this must
         *            be assignable to <code>java.util.Collection</code> or
         *            <code>Object[]</code>.
         */
        public DirtyTrackingDCBCVM(ValueModel wrappedModel, Class wrappedType) {
            super( wrappedModel, wrappedType );
            // FIXME: make DCBCVM do dirty tracking on its own
            dirty = false; // We should never start life as dirty
        }

        /**
         * Create the buffered list model. We want to use an ObservableEventList so that
         * it can be used as the root event list of the master form model.
         * @return ObservableList to use
         */
        protected ObservableList createBufferedListModel() {
            return new ObservableEventList( new BasicEventList() );
        }

        /**
         * Set the value. If this is our original value, then clear dirty.
         * @param value New value
         */
        public void setValue(Object value) {
            super.setValue( value );
            if( value == getWrappedValueModel().getValue() ) {
                // this is a revert
                dirty = false;
                valueUpdated();
            }
        }

        /**
         * Our underlying list has changed, we are now dirty.
         */
        protected void fireListModelChanged() {
            super.fireListModelChanged();
            dirty = true;
            valueUpdated();
        }

        /**
         * Return our dirty status.
         * @return dirty
         */
        public boolean isDirty() {
            return dirty;
        }

        /**
         * Clear the dirty status
         */
        public void clearDirty() {
            dirty = false;
            valueUpdated();
        }

        /**
         * Revert to original value.
         */
        public void revertToOriginal() {
            revert();
        }

        protected void valueUpdated() {
            boolean dirty = isDirty();
            if( oldDirty != dirty ) {
                oldDirty = dirty;
                firePropertyChange( DIRTY_PROPERTY, !dirty, dirty );
            }
        }
    }
}
