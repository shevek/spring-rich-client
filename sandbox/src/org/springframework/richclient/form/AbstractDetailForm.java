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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * This is an abstract base implementation of the detail side of a Master/Detail form
 * pair. Derived types need only implement {@link AbstractForm#createFormControl()}.
 * <p>
 * The various form commands can be secured by specifying security controller id's for
 * the command.  Use the {@link AbstractForm#getCommitSecurityControllerId()} and
 * {@link AbstractForm#getNewFormObjectCommand()}.
 * 
 * @author Larry Streepy
 * 
 */
public abstract class AbstractDetailForm extends AbstractForm implements PropertyChangePublisher {

    /** State indicating that we are editing no object. */
    public static final int STATE_CLEAR = 0;

    /** State indicating that we are editing an existing object. */
    public static final int STATE_EDIT = 1;

    /** State indicating that we are creating a new object. */
    public static final int STATE_CREATE = 2;

    /** Edit state property name for change notifications. */
    public static final String EDIT_STATE_PROPERTY = "edit_state";

    /**
     * @param pageFormModel
     */
    protected AbstractDetailForm(FormModel formModel, String formId, ObservableList editableItemList) {
        super( formModel, formId );

        // Install the detail data as our editable object list
        _editableItemList = editableItemList;
        setEditableFormObjects( editableItemList );
        setEditingFormObjectIndexHolder( _indexHolder );
    }

    /**
     * Construct a detail form using the provided parent form model (we will construct our
     * own form model as a child of the parent model). The provided masterList will be
     * installed as the set of editable form objects.
     * 
     * @param parentFormModel
     * @param formId
     * @param childFormObjectHolder
     * @param masterList ObservableList holding the editable items
     */
    public AbstractDetailForm(HierarchicalFormModel parentFormModel, String formId, ValueModel childFormObjectHolder,
            ObservableList masterList) {
        super( parentFormModel, formId, childFormObjectHolder );
        setMasterList( masterList );
        setEditingFormObjectIndexHolder( _indexHolder );
    }

    /**
     * Set the master list model.
     * 
     * @param masterList list to use as our master data
     */
    protected void setMasterList(ObservableList masterList) {
        _editableItemList = masterList;
        setEditableFormObjects( _editableItemList );
    }

    /**
     * Set the selected object index.
     * 
     * @param index of selected item
     */
    public void setSelectedIndex(int index) {
        _indexHolder.setValue( new Integer( index ) );
        setEditState( index < 0 ? STATE_CLEAR : STATE_EDIT );
        updateControlsForState();
    }

    /**
     * @return index of item being edited
     */
    public int getSelectedIndex() {
        return getEditingFormObjectIndex();
    }

    /**
     * Get the value holder containing the editing index. This allows triggers to monitor
     * for changes in the index of the object we are editing.
     * 
     * @return
     */
    public ValueHolder getEditingIndexHolder() {
        return _indexHolder;
    }

    /**
     * Set the form for "create new object" mode. This will set controls as needed for
     * this edit mode.
     */
    public void creatingNewObject() {
        setEditState( STATE_CREATE );
        updateControlsForState();
    }

    /**
     * Update our controls based on our state.
     */
    protected void updateControlsForState() {
        boolean showCancel = false;
        boolean showRevert = false;

        switch( getEditState() ) {
        case STATE_CREATE:
            showCancel = true;
            showRevert = false;
            break;
        case STATE_CLEAR:
            showCancel = false;
            showRevert = false;
            break;

        case STATE_EDIT:
            showCancel = false;
            showRevert = true;
            break;
        default:
            Assert.isTrue( false, "Invalid edit state: " + getEditState() );
        }

        getCancelCommand().setVisible( showCancel );
        getRevertCommand().setVisible( showRevert );
    }

    /**
     * Set the current edit state.
     * 
     * @param new edit state
     */
    protected void setEditState(int editState) {
        int oldEditState = _editState;
        _editState = editState;
        updateControlsForState();
        firePropertyChange( EDIT_STATE_PROPERTY, oldEditState, _editState );
    }

    /**
     * Get the current edit state: one of {@link #STATE_CLEAR}, {@link #STATE_CREATE},
     * or {@link #STATE_EDIT}.
     * 
     * @return current state
     */
    public int getEditState() {
        return _editState;
    }

    /**
     * Commit this forms data back to the master table. Let our super class do all the
     * work and then just inform our master table that the value has changed.
     */
    public void postCommit(FormModel formModel) {
        super.postCommit( formModel );

        // Now set the selected index back to -1 so that the forms properly reset
        setSelectedIndex( -1 );
    }

    protected String getRevertCommandFaceDescriptorId() {
        return "revert";
    }

    protected String getCommitCommandFaceDescriptorId() {
        return "save";
    }

    protected String getCancelCommandFaceDescriptorId() {
        return "cancelNew";
    }

    /**
     * Override to return null for the new object security controller id.  We do
     * this because this command is not used directly, so it shouldn't be controlled.
     * The {@link AbstractMasterForm} is responsible for the real (invocable)
     * instance of this command.
     * 
     * @return null
     */
    protected String getNewFormObjectSecurityControllerId() {
        return null;
    }

    /**
     * Return the configured cancel command, creating it if necessary.
     * 
     * @return cancel command
     */
    public ActionCommand getCancelCommand() {
        if( _cancelCommand == null ) {
            _cancelCommand = createCancelCommand();
        }
        return _cancelCommand;
    }

    /**
     * Create the cancel command. This will cancel the "create new" operation and reset
     * the form.
     * 
     * @return cancel command action
     */
    protected ActionCommand createCancelCommand() {
        String commandId = getCancelCommandFaceDescriptorId();
        if( !StringUtils.hasText( commandId ) ) {
            return null;
        }
        ActionCommand command = new ActionCommand( commandId ) {
            protected void doExecuteCommand() {
                AbstractDetailForm.this.reset();
                AbstractDetailForm.this.setEnabled( false );
                setEditingNewFormObject( false );
                setEditingFormObjectIndexSilently( -1 );
                setEditState( STATE_CLEAR );
                setFormObject( null );
            }
        };
        return (ActionCommand) getCommandConfigurer().configure( command );
    }

    /**
     * Return a standardized row of command buttons, right-justified and all of the same
     * size, with OK as the default button, and no mnemonics used, as per the Java Look
     * and Feel guidelines.
     */
    protected JComponent createButtonBar() {
        _commitCommand = getCommitCommand();
        _revertCommand = getRevertCommand();
        _cancelCommand = getCancelCommand();

        _formCommandGroup = CommandGroup.createCommandGroup( null, new AbstractCommand[] { _cancelCommand,
                _revertCommand, _commitCommand } );
        JComponent buttonBar = _formCommandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder( buttonBar );
        return buttonBar;
    }

    // =======================================
    // PropertyChangePublisher implementation
    // =======================================

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        if( listener == null ) {
            return;
        }
        if( _changeSupport == null ) {
            _changeSupport = new PropertyChangeSupport( this );
        }
        _changeSupport.addPropertyChangeListener( listener );
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        if( listener == null || _changeSupport == null ) {
            return;
        }
        _changeSupport.removePropertyChangeListener( listener );
    }

    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if( listener == null ) {
            return;
        }
        if( _changeSupport == null ) {
            _changeSupport = new PropertyChangeSupport( this );
        }
        _changeSupport.addPropertyChangeListener( propertyName, listener );
    }

    public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if( listener == null || _changeSupport == null ) {
            return;
        }
        _changeSupport.removePropertyChangeListener( propertyName, listener );
    }

    protected final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if( _changeSupport == null ) {
            return;
        }
        _changeSupport.firePropertyChange( propertyName, oldValue, newValue );
    }

    private ValueHolder _indexHolder = new ValueHolder( new Integer( -1 ) );
    private CommandGroup _formCommandGroup;
    private ActionCommand _commitCommand;
    private ActionCommand _revertCommand;
    private ActionCommand _cancelCommand;
    private ObservableList _editableItemList;
    private int _editState = STATE_CLEAR;
    private transient PropertyChangeSupport _changeSupport;

}
