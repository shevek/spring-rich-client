/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.forms;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.value.IndexAdapter;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.list.ObservableList;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public abstract class AbstractForm extends AbstractControlFactory implements Form, CommitListener {
    private String formId;

    private SwingFormModel formModel;

    private NestingFormModel parentFormModel;

    private Guarded formEnabledGuarded;

    private JButton lastDefaultButton;

    private PropertyChangeListener formEnabledChangeHandler;

    private ActionCommand newFormObjectCommand;

    private ActionCommand commitCommand;

    private ActionCommand revertCommand;

    private boolean editingNewFormObject;

    private boolean clearFormOnCommit = false;

    private ObservableList editableFormObjects;

    private ValueModel editingFormObjectIndexHolder;

    private ValueChangeListener editingFormObjectSetter;

    protected AbstractForm() {
        init();
    }

    protected AbstractForm(String formId) {
        setId(formId);
        init();
    }

    protected AbstractForm(Object formObject) {
        this(SwingFormModel.createFormModel(formObject));
    }

    protected AbstractForm(FormModel pageFormModel) {
        this(pageFormModel, null);
    }

    protected AbstractForm(FormModel formModel, String formId) {
        setId(formId);
        if (formModel instanceof NestingFormModel) {
            this.parentFormModel = (NestingFormModel)formModel;
            setFormModel(SwingFormModel.createChildPageFormModel(this.parentFormModel, formId));
        }
        else if (formModel instanceof SwingFormModel) {
            setFormModel((SwingFormModel)formModel);
        }
        else {
            throw new IllegalArgumentException("Unsupported form model implementation " + formModel);
        }
        init();
    }

    protected AbstractForm(NestingFormModel parentFormModel, String formId) {
        this(SwingFormModel.createChildPageFormModel(parentFormModel, formId), formId);
    }

    protected AbstractForm(NestingFormModel parentFormModel, String formId, String childFormObjectPropertyPath) {
        setId(formId);
        this.parentFormModel = parentFormModel;
        setFormModel(SwingFormModel.createChildPageFormModel(parentFormModel, formId, childFormObjectPropertyPath));
        init();
    }

    protected AbstractForm(NestingFormModel parentFormModel, String formId, ValueModel childFormObjectHolder) {
        setId(formId);
        this.parentFormModel = parentFormModel;
        setFormModel(SwingFormModel.createChildPageFormModel(parentFormModel, formId, childFormObjectHolder));
        init();
    }

    protected void init() {
        
    }
    
    public String getId() {
        return formId;
    }

    protected void setId(String formId) {
        this.formId = formId;
    }

    public SwingFormModel getFormModel() {
        return formModel;
    }

    protected void setFormModel(SwingFormModel formModel) {
        Assert.notNull(formModel);
        if (this.formModel != null && isControlCreated()) {
            throw new UnsupportedOperationException("Cannot reset form model once form control has been created");
        }
        if (this.formModel != null) {
            this.formModel.removeCommitListener(this);
        }
        this.formModel = formModel;
        this.formModel.addCommitListener(this);
    }

    protected NestingFormModel getParent() {
        return this.parentFormModel;
    }

    protected void setEditableFormObjects(ObservableList editableFormObjects) {
        this.editableFormObjects = editableFormObjects;
    }

    protected void setEditingFormObjectIndexHolder(ValueModel valueModel) {
        this.editingFormObjectIndexHolder = valueModel;
        this.editingFormObjectSetter = new EditingFormObjectSetter();
        this.editingFormObjectIndexHolder.addValueChangeListener(editingFormObjectSetter);
    }

    public boolean isEditingNewFormObject() {
        return editingNewFormObject;
    }

    private class EditingFormObjectSetter implements ValueChangeListener {
        public void valueChanged() {
            int selectionIndex = getEditingFormObjectIndex();
            if (selectionIndex == -1) {
                reset();
                setEnabled(false);
            }
            else {
                if (selectionIndex < editableFormObjects.size()) {
                    setFormObject(getEditableFormObject(selectionIndex));
                    setEnabled(true);
                }
            }
        }
    }

    protected int getEditingFormObjectIndex() {
        return ((Integer)editingFormObjectIndexHolder.getValue()).intValue();
    }

    protected Object getEditableFormObject(int selectionIndex) {
        return editableFormObjects.get(selectionIndex);
    }

    public void setClearFormOnCommit(boolean clearFormOnCommit) {
        this.clearFormOnCommit = clearFormOnCommit;
    }

    protected void setFormEnabledGuarded(Guarded formEnabledGuarded) {
        this.formEnabledGuarded = formEnabledGuarded;
        updateFormEnabledGuarded();
    }

    private void updateFormEnabledGuarded() {
        if (formEnabledGuarded != null) {
            formEnabledGuarded.setEnabled(formModel.isEnabled());
        }
    }

    protected JButton getDefaultButton() {
        if (isControlCreated()) {
            return SwingUtilities.getRootPane(getControl()).getDefaultButton();
        }
        else {
            return null;
        }
    }

    protected void setDefaultButton(JButton button) {
        JRootPane rootPane = SwingUtilities.getRootPane(getControl());
        if (rootPane != null) {
            rootPane.setDefaultButton(button);
        }
    }

    protected final JComponent createControl() {
        Assert
                .state(getFormModel() != null,
                        "This form's FormModel cannot be null once control creation is triggered!");
        initStandardLocalFormCommands();
        JComponent formControl = createFormControl();
        this.formEnabledChangeHandler = new FormEnabledPropertyChangeHandler();
        getFormModel().addPropertyChangeListener(FormModel.ENABLED_PROPERTY, formEnabledChangeHandler);
        addFormObjectChangeListener(new FormObjectChangeHandler());
        ActionCommand commitCommand = getCommitCommand();
        if (getCommitCommand() != null) {
            attachFormErrorGuard(getCommitCommand());
            getFormModel().addCommitListener(this);
        }
        return formControl;
    }

    private void initStandardLocalFormCommands() {
        getNewFormObjectCommand();
        getCommitCommand();
        getRevertCommand();
    }

    protected abstract JComponent createFormControl();

    private class FormObjectChangeHandler implements ValueChangeListener {
        public FormObjectChangeHandler() {
            valueChanged();
        }

        public void valueChanged() {
            setFormModelDefaultEnabledState();
        }

        /**
         * Set the form's enabled state based on a default policy--specifically,
         * disable if the form object is null or the form object is guarded and
         * is marked as disabled.
         */
        protected void setFormModelDefaultEnabledState() {
            if (getFormObject() == null) {
                getFormModel().setEnabled(false);
            }
            else {
                if (getFormObject() instanceof Guarded) {
                    setEnabled(((Guarded)getFormObject()).isEnabled());
                }
            }
        }
    }

    private class FormEnabledPropertyChangeHandler implements PropertyChangeListener {
        public FormEnabledPropertyChangeHandler() {
            handleEnabledChange(getFormModel().isEnabled());
        }

        public void propertyChange(PropertyChangeEvent evt) {
            handleEnabledChange(getFormModel().isEnabled());
        }

        private void handleEnabledChange(boolean enabled) {
            updateFormEnabledGuarded();

            if (enabled) {
                if (getCommitCommand() != null) {
                    if (lastDefaultButton == null) {
                        lastDefaultButton = getDefaultButton();
                    }
                    getCommitCommand().setDefaultButton();
                }
            }
            else {
                if (getCommitCommand() != null) {
                    getCommitCommand().setEnabled(false);
                }
                // set previous default button
                if (lastDefaultButton != null) {
                    setDefaultButton(lastDefaultButton);
                }
            }
            AbstractForm.this.handleEnabledChange(enabled);
        }

    };

    protected void handleEnabledChange(boolean enabled) {

    }

    public ActionCommand getNewFormObjectCommand() {
        if (this.newFormObjectCommand == null) {
            this.newFormObjectCommand = createNewFormObjectCommand();
        }
        return newFormObjectCommand;
    }

    public ActionCommand getCommitCommand() {
        if (this.commitCommand == null) {
            this.commitCommand = createCommitCommand();
        }
        return commitCommand;
    }

    public ActionCommand getRevertCommand() {
        if (this.revertCommand == null) {
            this.revertCommand = createRevertCommand();
        }
        return revertCommand;
    }

    private ActionCommand createNewFormObjectCommand() {
        String commandId = getNewFormObjectCommandId();
        if (!StringUtils.hasText(commandId)) {
            return null;
        }
        ActionCommand newFormObjectCommand = new ActionCommand(commandId) {
            protected void doExecuteCommand() {
                getFormModel().reset();
                getFormModel().setEnabled(true);
                editingNewFormObject = true;
                if (isEditingFormObjectSelected()) {
                    setEditingFormObjectIndexSilently(-1);
                }
            }
        };
        return (ActionCommand)getCommandConfigurer().configure(newFormObjectCommand);
    }

    private boolean isEditingFormObjectSelected() {
        if (editingFormObjectIndexHolder == null) {
            return false;
        }
        else {
            int value = ((Integer)editingFormObjectIndexHolder.getValue()).intValue();
            return value != -1;
        }
    }

    private void setEditingFormObjectIndexSilently(int index) {
        editingFormObjectIndexHolder.removeValueChangeListener(editingFormObjectSetter);
        editingFormObjectIndexHolder.setValue(new Integer(index));
        editingFormObjectIndexHolder.addValueChangeListener(editingFormObjectSetter);
    }

    private final ActionCommand createCommitCommand() {
        String commandId = getCommitCommandFaceDescriptorId();
        if (!StringUtils.hasText(commandId)) {
            return null;
        }
        ActionCommand commitCommand = new ActionCommand(commandId) {
            protected void doExecuteCommand() {
                getFormModel().commit();
            }
        };
        return (ActionCommand)getCommandConfigurer().configure(commitCommand);
    }

    public boolean preEditCommitted(Object formObject) {
        return true;
    }

    public void postEditCommitted(Object formObject) {
        if (editableFormObjects != null) {
            if (editingNewFormObject) {
                editableFormObjects.add(formObject);
                setEditingFormObjectIndexSilently(editableFormObjects.size() - 1);
            }
            else {
                IndexAdapter adapter = editableFormObjects.getIndexAdapter(getEditingFormObjectIndex());
                adapter.setValue(formObject);
                adapter.fireIndexedObjectChanged();
            }
        }
        if (clearFormOnCommit) {
            setFormObject(null);
        }
        editingNewFormObject = false;
    }

    private final ActionCommand createRevertCommand() {
        String commandId = getRevertCommandFaceDescriptorId();
        if (!StringUtils.hasText(commandId)) {
            return null;
        }
        ActionCommand revertCommand = new ActionCommand(commandId) {
            protected void doExecuteCommand() {
                getFormModel().revert();
            }
        };
        return (ActionCommand)getCommandConfigurer().configure(revertCommand);
    }

    protected final JButton createNewFormObjectButton() {
        Assert.state(newFormObjectCommand != null, "New form object command has not been created!");
        return (JButton)newFormObjectCommand.createButton();
    }

    protected final JButton createCommitButton() {
        Assert.state(commitCommand != null, "Commit command has not been created!");
        return (JButton)commitCommand.createButton();
    }

    protected String getNewFormObjectCommandId() {
        return "new"
                + StringUtils
                        .capitalize(ClassUtils.getShortName(getFormModel().getFormObject().getClass() + "Command"));
    }

    protected String getCommitCommandFaceDescriptorId() {
        return null;
    }

    protected String getRevertCommandFaceDescriptorId() {
        return null;
    }

    protected void attachFormErrorGuard(Guarded guarded) {
        FormGuard guard = new FormGuard(getFormModel(), guarded);
        addValidationListener(guard);
    }

    public Object getFormObject() {
        return formModel.getFormObject();
    }

    public void setFormObject(Object formObject) {
        formModel.setFormObject(formObject);
    }

    public Object getValue(String formProperty) {
        return formModel.getValue(formProperty);
    }

    public ValueModel getValueModel(String formProperty) {
        ValueModel valueModel = formModel.getValueModel(formProperty);
        if (valueModel == null) {
            logger.warn("A value model for property '" + formProperty + "' could not be found.  Typo?");
        }
        return valueModel;
    }

    public boolean isEnabled() {
        return this.formModel.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        this.formModel.setEnabled(enabled);
    }

    public void addValidationListener(ValidationListener listener) {
        formModel.addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener) {
        formModel.removeValidationListener(listener);
    }

    public ValidationListener newSingleLineResultsReporter(Guarded guarded, Messagable messageReceiver) {
        return getFormModel().createSingleLineResultsReporter(guarded, messageReceiver);
    }

    public void addFormObjectChangeListener(ValueChangeListener listener) {
        formModel.addFormObjectChangeListener(listener);
    }

    public void removeFormObjectChangeListener(ValueChangeListener listener) {
        formModel.addFormObjectChangeListener(listener);
    }

    public void addFormValueChangeListener(String formPropertyPath, ValueChangeListener listener) {
        getFormModel().addFormValueChangeListener(formPropertyPath, listener);
    }

    public void removeFormValueChangeListener(String formPropertyPath, ValueChangeListener listener) {
        getFormModel().removeFormValueChangeListener(formPropertyPath, listener);
    }

    public boolean hasErrors() {
        return formModel.getHasErrors();
    }

    public void commit() {
        formModel.commit();
    }

    public void revert() {
        formModel.revert();
    }

    public void reset() {
        formModel.reset();
    }

}