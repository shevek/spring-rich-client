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
import javax.swing.SwingUtilities;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public abstract class AbstractForm extends AbstractControlFactory implements
        Form {
    private String formId;

    private SwingFormModel formModel;

    private NestingFormModel parentFormModel;

    private Guarded formEnabledGuarded;

    private JButton lastDefaultButton;

    private PropertyChangeListener formEnabledChangeHandler;

    private ActionCommand commitCommand;

    protected AbstractForm() {

    }

    protected AbstractForm(String formId) {
        this.formId = formId;
    }

    protected AbstractForm(SwingFormModel pageFormModel) {
        this(pageFormModel, null);
    }

    protected AbstractForm(NestingFormModel parentFormModel, String formId) {
        this(SwingFormModel.createChildPageFormModel(parentFormModel,
                formId));
        this.formId = formId;
    }

    protected AbstractForm(NestingFormModel parentFormModel, String formId,
            String childFormObjectPropertyPath) {
        this.parentFormModel = parentFormModel;
        setFormModel(SwingFormModel.createChildPageFormModel(parentFormModel,
                formId, childFormObjectPropertyPath));
        this.formId = formId;
    }

    protected AbstractForm(NestingFormModel parentFormModel, String formId,
            ValueModel childFormObjectHolder) {
        this.parentFormModel = parentFormModel;
        setFormModel(SwingFormModel.createChildPageFormModel(parentFormModel,
                formId, childFormObjectHolder));
        this.formId = formId;
    }

    protected AbstractForm(FormModel formModel, String formId) {
        this.formId = formId;
        if (formModel instanceof NestingFormModel) {
            this.parentFormModel = (NestingFormModel)formModel;
            setFormModel(SwingFormModel.createChildPageFormModel(
                    this.parentFormModel, formId));
        }
        else if (formModel instanceof SwingFormModel) {
            setFormModel((SwingFormModel)formModel);
        }
        else {
            throw new IllegalArgumentException(
                    "Unsupported form model implementation " + formModel);
        }
    }

    public String getId() {
        return formId;
    }

    public SwingFormModel getFormModel() {
        return formModel;
    }

    protected void setFormModel(SwingFormModel formModel) {
        Assert.notNull(formModel);
        this.formModel = formModel;
    }

    protected NestingFormModel getParent() {
        return this.parentFormModel;
    }

    public Object getFormObject() {
        return formModel.getFormObject();
    }

    public void setFormObject(Object formObject) {
        getFormObjectHolder().setValue(formObject);
    }

    public ValueModel getFormObjectHolder() {
        return formModel.getFormObjectHolder();
    }

    public Object getValue(String formProperty) {
        return formModel.getValue(formProperty);
    }

    public ValueModel getValueModel(String formProperty) {
        ValueModel valueModel = formModel.getValueModel(formProperty);
        if (valueModel == null) {
            logger.warn("A value model for property '" + formProperty
                    + "' could not be found.  Typo?");
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

    public ValidationListener newSingleLineResultsReporter(Guarded guarded,
            MessageReceiver messageAreaPane) {
        return getFormModel().createSingleLineResultsReporter(guarded,
                messageAreaPane);
    }

    protected void addFormObjectListener(ValueChangeListener listener) {
        getFormObjectHolder().addValueChangeListener(listener);
    }

    protected void removeFormObjectListener(ValueChangeListener listener) {
        getFormObjectHolder().removeValueChangeListener(listener);
    }

    protected void addFormValueChangeListener(String formPropertyPath,
            ValueChangeListener listener) {
        getFormModel().addFormValueChangeListener(formPropertyPath, listener);
    }

    protected void removeFormValueChangeListener(String formPropertyPath,
            ValueChangeListener listener) {
        getFormModel()
                .removeFormValueChangeListener(formPropertyPath, listener);
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

    protected void setFormEnabledGuarded(Guarded formEnabledGuarded) {
        this.formEnabledGuarded = formEnabledGuarded;
    }

    protected void attachGuard(Guarded guarded) {
        FormGuard guard = new FormGuard(getFormModel(), guarded);
        addValidationListener(guard);
    }

    protected JButton getDefaultButton() {
        return SwingUtilities.getRootPane(getControl()).getDefaultButton();
    }

    protected void setDefaultButton(JButton button) {
        SwingUtilities.getRootPane(getControl()).setDefaultButton(button);
    }

    protected final JComponent createControl() {
        JComponent formControl = createFormControl();
        this.formEnabledChangeHandler = new FormEnabledPropertyChangeHandler();
        getFormModel().addPropertyChangeListener(FormModel.ENABLED_PROPERTY,
                formEnabledChangeHandler);
        this.addFormObjectListener(new FormEnabledStateController());
        ActionCommand commitCommand = getCommitCommand();
        if (getCommitCommand() != null) {
            attachGuard(getCommitCommand());
        }
        return formControl;
    }

    protected abstract JComponent createFormControl();

    private class FormEnabledPropertyChangeHandler implements
            PropertyChangeListener {
        public FormEnabledPropertyChangeHandler() {
            handleEnabledChange(getFormModel().isEnabled());
        }

        public void propertyChange(PropertyChangeEvent evt) {
            handleEnabledChange(getFormModel().isEnabled());
        }

        private void handleEnabledChange(boolean enabled) {
            if (formEnabledGuarded != null) {
                formEnabledGuarded.setEnabled(enabled);
            }
            if (enabled) {
                if (getCommitCommand() != null) {
                    if (lastDefaultButton == null) {
                        lastDefaultButton = getDefaultButton();
                    }
                    getCommitCommand().setDefaultButton();
                }
            }
            else {
                // set previous default button
                if (lastDefaultButton != null) {
                    setDefaultButton(lastDefaultButton);
                }
            }
        }
    };

    private ActionCommand getCommitCommand() {
        return commitCommand;
    }

    protected void setCommitCommand(ActionCommand commitCommand) {
        this.commitCommand = commitCommand;
    }

    private class FormEnabledStateController implements ValueChangeListener {
        public FormEnabledStateController() {
            valueChanged();
        }

        public void valueChanged() {
            setFormModelDefaultEnabledState();
        }
    }

    /**
     * Set the form's enabled state based on a default policy--specifically,
     * disable if the form object is null or the form object is guarded and is
     * marked as disabled.
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