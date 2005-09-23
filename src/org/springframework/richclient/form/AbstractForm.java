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
package org.springframework.richclient.form;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.springframework.binding.form.CommitListener;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.value.IndexAdapter;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ObservableList;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public abstract class AbstractForm extends AbstractControlFactory implements Form, CommitListener {

    private final FormObjectChangeHandler formObjectChangeHandler = new FormObjectChangeHandler();

    private String formId;

    private ValidatingFormModel formModel;

    private HierarchicalFormModel parentFormModel;

    private FormGuard formGuard;

    private JButton lastDefaultButton;

    private PropertyChangeListener formEnabledChangeHandler;

    private ActionCommand newFormObjectCommand;

    private ActionCommand commitCommand;

    private ActionCommand revertCommand;

    private boolean editingNewFormObject;

    private boolean clearFormOnCommit = false;

    private ObservableList editableFormObjects;

    private ValueModel editingFormObjectIndexHolder;

    private PropertyChangeListener editingFormObjectSetter;

    private BindingFactory bindingFactory;

    protected AbstractForm() {
        init();
    }

    protected AbstractForm(String formId) {
        setId(formId);
        init();
    }

    protected AbstractForm(Object formObject) {
        this(FormModelHelper.createFormModel(formObject));
    }

    protected AbstractForm(FormModel pageFormModel) {
        this(pageFormModel, null);
    }

    protected AbstractForm(FormModel formModel, String formId) {
        setId(formId);
        if (formModel instanceof ValidatingFormModel) {
            setFormModel((ValidatingFormModel)formModel);            
        }
        else {
            throw new IllegalArgumentException("Unsupported form model implementation " + formModel);
        }
        init();
    }

    protected AbstractForm(HierarchicalFormModel parentFormModel, String formId, String childFormObjectPropertyPath) {
        setId(formId);
        this.parentFormModel = parentFormModel;
        setFormModel(FormModelHelper.createChildPageFormModel(parentFormModel, formId, childFormObjectPropertyPath));
        init();
    }

    protected AbstractForm(HierarchicalFormModel parentFormModel, String formId, ValueModel childFormObjectHolder) {
        setId(formId);
        this.parentFormModel = parentFormModel;
        setFormModel(FormModelHelper.createChildPageFormModel(parentFormModel, formId, childFormObjectHolder));
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

    public ValidatingFormModel getFormModel() {
        return formModel;
    }

    public BindingFactory getBindingFactory() {
        if (bindingFactory == null) {
            bindingFactory = getApplicationServices().getBindingFactory(formModel);
        }
        return bindingFactory;
    }

    protected void setFormModel(ValidatingFormModel formModel) {
        Assert.notNull(formModel);
        if (this.formModel != null && isControlCreated()) {
            throw new UnsupportedOperationException("Cannot reset form model once form control has been created");
        }
        if (this.formModel != null) {
            this.formModel.removeCommitListener(this);
        }
        this.formModel = formModel;
        this.formGuard = new FormGuard(formModel);
        this.formModel.addCommitListener(this);
        setFormModelDefaultEnabledState();
    }

    protected HierarchicalFormModel getParent() {
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

    /**
     * Set the "editing new form object" state as indicated.
     * @param editingNewFormOject
     */
    protected void setEditingNewFormObject(boolean editingNewFormOject) {
        this.editingNewFormObject = editingNewFormOject;
    }

    private class EditingFormObjectSetter implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            int selectionIndex = getEditingFormObjectIndex();
            if (selectionIndex == -1) {
                // FIXME: why do we need this                   
                // getFormModel().reset();
                setEnabled(false);
            }
            else {
                if (selectionIndex < editableFormObjects.size()) {
                    // If we were editing a "new" object, we need to clear
                    // that flag since a new object has been selected
                    setEditingNewFormObject(false);
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
        Assert.state(getFormModel() != null, "This form's FormModel cannot be null once control creation is triggered!");
        initStandardLocalFormCommands();
        JComponent formControl = createFormControl();
        this.formEnabledChangeHandler = new FormEnabledPropertyChangeHandler();
        getFormModel().addPropertyChangeListener(FormModel.ENABLED_PROPERTY, formEnabledChangeHandler);
        addFormObjectChangeListener(formObjectChangeHandler);
        if (getCommitCommand() != null) {
            getFormModel().addCommitListener(this);
        }
        return formControl;
    }

    private void initStandardLocalFormCommands() {
        getNewFormObjectCommand();
        getCommitCommand();
        getRevertCommand();
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

    protected abstract JComponent createFormControl();

    private class FormObjectChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            setFormModelDefaultEnabledState();
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

    }

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
        ActionCommand newFormObjectCmd = new ActionCommand(commandId) {
            protected void doExecuteCommand() {
                getFormModel().reset();
                getFormModel().setEnabled(true);
                editingNewFormObject = true;
                if (isEditingFormObjectSelected()) {
                    setEditingFormObjectIndexSilently(-1);
                }
            }
        };
        attachFormGuard(newFormObjectCmd, FormGuard.LIKE_NEWFORMOBJCOMMAND);
        return (ActionCommand)getCommandConfigurer().configure(newFormObjectCmd);
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

    protected void setEditingFormObjectIndexSilently(int index) {
        editingFormObjectIndexHolder.removeValueChangeListener(editingFormObjectSetter);
        editingFormObjectIndexHolder.setValue(new Integer(index));
        editingFormObjectIndexHolder.addValueChangeListener(editingFormObjectSetter);
    }

    private final ActionCommand createCommitCommand() {
        String commandId = getCommitCommandFaceDescriptorId();
        if (!StringUtils.hasText(commandId)) {
            return null;
        }
        ActionCommand commitCmd = new ActionCommand(commandId) {
            protected void doExecuteCommand() {
                getFormModel().commit();
            }
        };
        attachFormGuard(commitCmd, FormGuard.LIKE_COMMITCOMMAND);
        return (ActionCommand)getCommandConfigurer().configure(commitCmd);
    }

    public void preCommit(FormModel formModel) {
    }

    public void postCommit(FormModel formModel) {
        if (editableFormObjects != null) {
            if (editingNewFormObject) {
                editableFormObjects.add(formModel.getFormObject());
                setEditingFormObjectIndexSilently(editableFormObjects.size() - 1);
            }
            else {
                int index = getEditingFormObjectIndex();
                // Avoid updating unless we have actually selected an object for edit
                if (index >= 0) {
                    IndexAdapter adapter = editableFormObjects.getIndexAdapter(index);
                    adapter.setValue(formModel.getFormObject());
                    adapter.fireIndexedObjectChanged();
                }
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
        ActionCommand revertCmd = new ActionCommand(commandId) {
            protected void doExecuteCommand() {
                getFormModel().revert();
            }
        };
        attachFormGuard(revertCmd, FormGuard.LIKE_REVERTCOMMAND);
        return (ActionCommand)getCommandConfigurer().configure(revertCmd);
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
                + StringUtils.capitalize(ClassUtils.getShortName(getFormModel().getFormObject().getClass() + "Command"));
    }

    protected String getCommitCommandFaceDescriptorId() {
        return null;
    }

    protected String getRevertCommandFaceDescriptorId() {
        return null;
    }

    protected void attachFormErrorGuard(Guarded guarded) {
        attachFormGuard(guarded, FormGuard.FORMERROR_GUARDED);
    }

    protected void attachFormGuard(Guarded guarded, int mask) {
        this.formGuard.addGuarded(guarded, mask);
    }

    public Object getFormObject() {
        return formModel.getFormObject();
    }

    public void setFormObject(Object formObject) {
        formModel.setFormObject(formObject);
    }

    public Object getValue(String formProperty) {
        return formModel.getValueModel(formProperty).getValue();
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
        formModel.getValidationResults().addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener) {
        formModel.getValidationResults().removeValidationListener(listener);
    }

    public ValidationListener newSingleLineResultsReporter(Guarded guarded, Messagable messageReceiver) {
        return new SimpleValidationResultsReporter(formModel.getValidationResults(), guarded, messageReceiver);
    }

    public void addFormObjectChangeListener(PropertyChangeListener listener) {
        formModel.getFormObjectHolder().addValueChangeListener(listener);
    }

    public void removeFormObjectChangeListener(PropertyChangeListener listener) {
        formModel.getFormObjectHolder().removeValueChangeListener(listener);
    }

    public void addFormValueChangeListener(String formPropertyPath, PropertyChangeListener listener) {
        getFormModel().getValueModel(formPropertyPath).addValueChangeListener(listener);
    }

    public void removeFormValueChangeListener(String formPropertyPath, PropertyChangeListener listener) {
        getFormModel().getValueModel(formPropertyPath).removeValueChangeListener(listener);
    }

    public boolean isDirty() {
        return formModel.isDirty();
    }

    public boolean hasErrors() {
        return formModel.getValidationResults().getHasErrors();
    }

    public void commit() {
        formModel.commit();
    }

    public void revert() {
        formModel.revert();
    }

    public void reset() {
        getFormModel().reset();
    }
}