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
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableEventList;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
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
     * Construct a new AbstractMasterForm using the given parent model, form Id, and
     * detail object type. This method will attempt to pull the master list data from the
     * provided form model. If it finds a usable model (ObservableList), then it will
     * install this as the master data.
     * 
     * @param formModel Parent form model
     * @param formId Id of this form
     * @param detailType Type of detail object managed by this master form
     */
    protected AbstractMasterForm(HierarchicalFormModel formModel, String formId, Class detailType) {
        super( formModel, formId );
        _formModel = formModel;
        _detailType = detailType;

        // Just configure a basic event list to handle our data
        installEventList( getRootEventList() );

        // Now we need to construct a subform and value model to handle the
        // detail elements of this master table

        Object detailObject = BeanUtils.instantiateClass( detailType );
        ValueModel valueHolder = new ValueHolder( detailObject );
        _detailForm = createDetailForm( _formModel, valueHolder, _masterEventList );

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
            _rootEventList.addListEventListener( new ListEventListener() {

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

            } );
        }
        return _rootEventList;
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
        getSelectionModel().addListSelectionListener( getSelectionHandler() );
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
        return new ListSelectionHandler();
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

        ActionCommand deleteCommand = new ActionCommand( commandId ) {
            protected void doExecuteCommand() {
                // Reset the detail form and remove the item from the list
                _detailForm.reset();
                int index = getSelectionModel().getMinSelectionIndex();
                if( index >= 0 ) {
                    getMasterEventList().remove( index );
                }
            }
        };
        deleteCommand.setEnabled( false ); // Until it is specifically enabled
        // by a selection
        return (ActionCommand) getCommandConfigurer().configure( deleteCommand );
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
    protected ConfigurableFormModel getDetailFormModel() {
        return _detailFormModel;
    }

    /**
     * @param formModel The detailFormModel to set.
     */
    protected void setDetailFormModel(ConfigurableFormModel formModel) {
        _detailFormModel = formModel;
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
            System.out.println( "Form is dirty" );
            ConfirmationDialog dlg = new ConfirmationDialog( "Unsaved Changes",
                "Creating a new item will cause you to lose your unsaved changes.\nAre you sure you want to do this?" ) {

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
        boolean isCreating = getDetailForm().getEditState() == AbstractDetailForm.STATE_CREATE;

        _deleteCommand.setEnabled( getDetailForm().getEditingFormObjectIndex() >= 0 );
        _newFormObjectCommand.setEnabled( !isCreating );
    }

    private EventList _rootEventList;
    private ObservableEventList _masterEventList;
    private HierarchicalFormModel _formModel;
    private ConfigurableFormModel _detailFormModel;
    private AbstractDetailForm _detailForm;
    private Class _detailType;
    private ActionCommand _newFormObjectCommand;
    private ActionCommand _deleteCommand;
    private CommandGroup _commandGroup;

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
                System.out.println( "Form is dirty" );
                ConfirmationDialog dlg = new ConfirmationDialog(
                    "Unsaved Changes",
                    "Selecting a different item will cause you to lose your unsaved changes.\nAre you sure you want to select a different item?" ) {

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
}
