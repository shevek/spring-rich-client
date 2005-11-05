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
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.DeepCopyBufferedCollectionValueModel;
import org.springframework.binding.value.support.ObservableEventList;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.table.ListSelectionListenerSupport;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * Abstract base for the Master form of a Master/Detail pair. Derived types must implement
 * two methods:
 * <dt>{@link #createDetailForm}</dt>
 * <dd>To construct the detail half of this master/detail pair</dd>
 * <dt>{@link #getSelectionModel()}</dt>
 * <dd>To return the selection model of the object rendering the list</dd>
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
        _detailType = detailType;

        ValueModel propertyVM = parentFormModel.getValueModel( property );
        ValueModel detailVM = new DeepCopyBufferedCollectionValueModel( propertyVM, propertyVM.getValue().getClass() );
        ValidatingFormModel formModel = FormModelHelper.createChildPageFormModel( parentFormModel, formId, detailVM );

        setFormModel( formModel );

        // Install a handler to detect when the parents form model changes
        propertyVM.addValueChangeListener( _parentFormPropertyChangeHandler );

        configure();
    }

    /**
     * Construct a new AbstractMasterForm using the given parent model, form Id, and
     * detail object type. This method will attempt to pull the master list data from the
     * provided form model. If it finds a usable model (ObservableList), then it will
     * install this as the master data.
     * <p>
     * <em>Warning:</em> This constructor makes the assumption that changes in the
     * underlying form data can be detected by watching the wrapped value model of the
     * provided form model's form object holder. If the form object holder is a buffered
     * value model, then the wrapped value model will be obtained and a listener will be
     * added to detect changes on that wrapped model. Specifically, the ValueModel to
     * watch is obtained like this:
     * 
     * <pre>
     * ValueModel wrappedVM = ((BufferedValueModel) formModel.getFormObjectHolder()).getWrappedValueModel();
     * </pre>
     * 
     * If this is not the case, then this form may not function properly when the parent
     * form model is changed.
     * <p>
     * Use of this constructor (although not deprecated yet) is discouraged due to this
     * requirement. You should use the following constructor:
     * {@link #AbstractMasterForm(HierarchicalFormModel, String, String, Class)}
     * <p>
     * @param formModel Parent form model
     * @param formId Id of this form
     * @param detailType Type of detail object managed by this master form
     */
    protected AbstractMasterForm(HierarchicalFormModel formModel, String formId, Class detailType) {
        super( formModel, formId );
        _detailType = detailType;

        // Install a handler to detect when the parents form model changes.
        // Note that this makes a BIG assumption that our form model's wrapped value model
        // is the ValueModel we need to watch.
        ValueModel formObjectVM = formModel.getFormObjectHolder();
        if( formObjectVM instanceof BufferedValueModel ) {
            ValueModel wrappedVM = ((BufferedValueModel) formObjectVM).getWrappedValueModel();
            wrappedVM.addValueChangeListener( _parentFormPropertyChangeHandler );
        }

        configure();
    }

    /**
     * Configure this master form's data and prepare the detail form.
     */
    protected void configure() {
        // Just configure a basic event list to handle our data
        installEventList( getRootEventList() );

        // Now we need to construct a subform and value model to handle the
        // detail elements of this master table

        Object detailObject = BeanUtils.instantiateClass( _detailType );
        ValueModel valueHolder = new ValueHolder( detailObject );
        _detailForm = createDetailForm( getFormModel(), valueHolder, _masterEventList );

        // Start the form disabled and not validating until the form is actually in use.
        _detailForm.setEnabled( false );
        _detailForm.getFormModel().setValidating( false );

        // Wire up the monitor to track the selected index and edit state so we
        // can keep the delete and add button states up to date
        _detailForm.getEditingIndexHolder().addValueChangeListener( new EditingIndexMonitor() );
        _detailForm.addPropertyChangeListener( new EditStateMonitor() );
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
        _masterEventList = new ObservableEventList( eventList );

        // Propogate this down to the detail form
        if( _detailForm != null ) {
            _detailForm.setMasterList( _masterEventList );
        }
    }

    /**
     * Get the root event list for this model. This event list will be constructed from
     * the form objects value (assumed to be a collection). Any subclasses that are
     * installing additional transformed lists should use this method to obtain the
     * original event list on top of which all the other lists are constructed.
     */
    protected EventList getRootEventList() {
        if( _rootEventList == null ) {
            _rootEventList = new BasicEventList();
            _rootEventList.addAll( getFormData() );

            // Install a listener so we can forward changes to the underlying form data
            _rootEventList.addListEventListener( _proxyingListEventHandler );
        }
        return _rootEventList;
    }

    /**
     * Rebuild the event list with data from the form object. This is normally invoked
     * when the value model holding this forms source property has changed (which can
     * occur when the parent form object is changed). This method is normally invoked from
     * the parent form object listener.
     * 
     * @see #_parentFormPropertyChangeHandler
     */
    protected void rebuildRootEventList() {
        if( _masterEventList != null ) {
            // While we do this, we need to disable our normal list listener since it's
            // too late to interact with the user due to unsaved changes (the underlying
            // value model has already changed).
            uninstallSelectionHandler();

            // Also remove the proxying event handler so the refresh will not result in
            // actual changes to the underlying form data (which has already been
            // rebuilt).
            _rootEventList.removeListEventListener( _proxyingListEventHandler );

            // Simply clear the current (old) list data and replace it with the new data
            _masterEventList.clear();
            _masterEventList.addAll( getFormData() );

            // Clean up the detail form
            if( _detailForm != null ) {
                _detailForm.reset();
                _detailForm.setSelectedIndex( -1 );
            }

            if( isControlCreated() ) {
                updateControlsForState(); // Ensure our controls are properly updated
            }

            // Reinstate the handlers
            _rootEventList.addListEventListener( _proxyingListEventHandler );
            installSelectionHandler();
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
        return _masterEventList;
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
        return _selectionHandler;
    }

    /**
     * Get the command group for the master table (the add and delete commands).
     * 
     * @return command group
     */
    protected CommandGroup getCommandGroup() {
        if( _commandGroup == null ) {
            _commandGroup = CommandGroup.createCommandGroup( null, new AbstractCommand[] { getDeleteCommand(),
                    getNewFormObjectCommand() } );
        }
        return _commandGroup;
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
        if( _newFormObjectCommand == null ) {
            _newFormObjectCommand = createNewFormObjectCommand();
        }
        return _newFormObjectCommand;
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
        return (ActionCommand) getCommandConfigurer().configure( newDetailObjectCommand );
    }

    /**
     * Construct the "new detail object" command id as
     * <code>new[detailTypeName]Command</code>
     * 
     * @return constructed command id
     */
    protected String getNewFormObjectCommandId() {
        return "new" + StringUtils.capitalize( ClassUtils.getShortName( _detailType + "Command" ) );
    }

    /**
     * Return the command to delete the currently selected item in the master set.
     * 
     * @return command, created on demand
     */
    public ActionCommand getDeleteCommand() {
        if( _deleteCommand == null ) {
            _deleteCommand = createDeleteCommand();
        }
        return _deleteCommand;
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
        return (ActionCommand) getCommandConfigurer().configure( deleteCommand );
    }

    /**
     * Get the message to present to the user when confirming the delete of selected
     * detail items. This default implementation just obtains the message with key
     * <code>masterForm.confirmDelete.message</code>. Subclasses can use the selected
     * item(s) to construct a more meaningful message.
     */
    protected String getConfirmDeleteMessage() {
        return getMessage( "masterForm.confirmDelete.message" );
    }

    /**
     * Maybe delete the selected items. If we are configured to confirm the delete, then
     * do so. If the user confirms, then delete the selected items.
     */
    protected void maybeDeleteSelectedItems() {

        // If configured, have the user confirm the delete operation
        if( isConfirmDelete() ) {
            String title = getMessage( "masterForm.confirmDelete.title" );
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
     * @param index Index of item to delete
     */
    protected void deleteSelectedItems() {
        ListSelectionModel sm = getSelectionModel();

        if( sm.isSelectionEmpty() ) {
            return;
        }

        _detailForm.reset();

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
        Assert.state( _deleteCommand != null, "Delete command has not been created!" );
        return (JButton) _deleteCommand.createButton();
    }

    /**
     * Construct the "delete detail object" command id as
     * <code>delete[DetailTypeName]Command</code>
     * 
     * @return constructed command id
     */
    protected String getDeleteCommandId() {
        return "delete" + StringUtils.capitalize( ClassUtils.getShortName( _detailType + "Command" ) );
    }

    /**
     * @return Returns the detailForm.
     */
    protected AbstractDetailForm getDetailForm() {
        return _detailForm;
    }

    /**
     * @param form The detailForm to set.
     */
    protected void setDetailForm(AbstractDetailForm form) {
        _detailForm = form;
    }

    /**
     * @return Returns the detailFormModel.
     */
    protected ValidatingFormModel getDetailFormModel() {
        return _detailForm.getFormModel();
    }

    /**
     * @return Returns the detailType.
     */
    protected Class getDetailType() {
        return _detailType;
    }

    /**
     * @param type The detailType to set.
     */
    protected void setDetailType(Class type) {
        _detailType = type;
    }

    /**
     * Return confirm delete setting.
     * @return confirm delete setting.
     */
    public boolean isConfirmDelete() {
        return _confirmDelete;
    }

    /**
     * Set confirm delete. If this is <code>true</code> then the master form will
     * confirm with the user prior to deleting a detail item.
     * @param confirmDelete
     */
    public void setConfirmDelete(boolean confirmDelete) {
        _confirmDelete = confirmDelete;
    }

    /**
     * Deal with the user invoking a "new object" command. If we have unsaved changes,
     * then we need to query the user to ensure they want to really make the change.
     * 
     * @param newIndex The new selection index, may be -1 to clear the selection
     */
    protected void maybeCreateNewObject() {
        if( getDetailForm().isEditingNewFormObject() ) {
            return; // Already creating a new object, just bail
        }

        final ActionCommand detailNewObjectCommand = _detailForm.getNewFormObjectCommand();

        if( getDetailForm().isDirty() ) {
            String title = getMessage( "masterForm.dirtyNew.title" );
            String message = getMessage( "masterForm.dirtyNew.message" );
            ConfirmationDialog dlg = new ConfirmationDialog( title, message ) {
                protected void onConfirm() {
                    // Tell both forms that we are creating a new object
                    detailNewObjectCommand.execute(); // Do subform action first
                    creatingNewObject();
                    _detailForm.creatingNewObject();
                }
            };
            dlg.showDialog();
        } else {
            // Tell both forms that we are creating a new object
            detailNewObjectCommand.execute(); // Do subform action first
            creatingNewObject();
            _detailForm.creatingNewObject();
        }
    }

    /**
     * Update our controls based on our state.
     */
    protected void updateControlsForState() {
        int state = getDetailForm().getEditState();
        boolean isCreating = state == AbstractDetailForm.STATE_CREATE;

        _deleteCommand.setEnabled( getDetailForm().getEditingFormObjectIndex() >= 0 );
        _newFormObjectCommand.setEnabled( !isCreating );

        // If we are in the CLEAR state, then we need to disable validations on the form
        getDetailFormModel().setValidating( state != AbstractDetailForm.STATE_CLEAR );
    }

    /**
     * When the results reporter is setup on the master form, we need to capture it and
     * forward it on to the detail form as well.
     */
    public ValidationListener newSingleLineResultsReporter(Guarded guarded, Messagable messageReceiver) {
        ValidationListener l = super.newSingleLineResultsReporter( guarded, messageReceiver );
        getDetailForm().newSingleLineResultsReporter( guarded, messageReceiver );
        getDetailFormModel().validate();
        return l;
    }

    private EventList _rootEventList;
    private ObservableEventList _masterEventList;
    private AbstractDetailForm _detailForm;
    private Class _detailType;
    private ActionCommand _newFormObjectCommand;
    private ActionCommand _deleteCommand;
    private CommandGroup _commandGroup;
    private boolean _confirmDelete = true;
    private ListSelectionHandler _selectionHandler = new ListSelectionHandler();
    private ListEventListener _proxyingListEventHandler = new ProxyingListEventHandler();
    private PropertyChangeListener _parentFormPropertyChangeHandler = new ParentFormPropertyChangeHandler();

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
                String title = getMessage( "masterForm.dirtyChange.title" );
                String message = getMessage( "masterForm.dirtyChange.message" );
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
     * Inner class to monitor changes on the root event list and proxy them on to the
     * underlying form object (collection).
     */
    private class ProxyingListEventHandler implements ListEventListener {

        /**
         * The list has changed, forward the changes on to the underlying form data
         */
        public void listChanged(ListEvent listChanges) {
            while( listChanges.next() ) {
                int changeIndex = listChanges.getIndex();
                switch( listChanges.getType() ) {
                case ListEvent.INSERT:
                    getFormData().add( changeIndex, _rootEventList.get( changeIndex ) );
                    break;
                case ListEvent.UPDATE:
                    getFormData().set( changeIndex, _rootEventList.get( changeIndex ) );
                    break;
                case ListEvent.DELETE:
                    getFormData().remove( changeIndex );
                    break;
                }
            }
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
            rebuildRootEventList();
        }

    }
}
