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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.springframework.richclient.form.binding.BindingFactoryProvider;
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

    private Map childForms = new HashMap();
    
    private List validationResultsReporters = new ArrayList();

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
        this(pageFormModel, pageFormModel.getId());
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
            bindingFactory = ((BindingFactoryProvider)getService(BindingFactoryProvider.class)).getBindingFactory(formModel);
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

    /**
     * Add a child (or sub) form to this form. Child forms will be tied in to the same
     * validation results reporter as this form and they will be configured to control the
     * same guarded object as this form.
     * <p>
     * Validation listeners are unique to a form, so calling
     * {@link #addValidationListener(ValidationListener)} will only add a listener to this
     * form. If you want to listen to the child forms, you will need to add a validation
     * listener on each child form of interest.
     * <p>
     * <em>Note:</em> It is very important that the child form provided be created using
     * a form model that is a child model of this form's form model. If this is not done,
     * then commit and revert operations will not be properly delegated to the child form
     * models.
     * 
     * @param childForm to add
     */
    public void addChildForm(Form childForm) {
        childForms.put( childForm.getId(), childForm );
        getFormModel().addChild(childForm.getFormModel());
        Iterator it = validationResultsReporters.iterator();
        while (it.hasNext())
        {
            ValidationResultsReporter reporter = (ValidationResultsReporter)it.next();
            childForm.addValidationResultsReporter(reporter.createChild(childForm.getFormModel().getValidationResults()));
        }
    }
    
    /**
     * @inheritDoc
     */
    public List getValidationResultsReporters()
    {
        return validationResultsReporters;
    }

    /**
     * @inheritDoc
     */
    public void addValidationResultsReporter(ValidationResultsReporter reporter)
    {
        this.validationResultsReporters.add(reporter);
    }
    
    /**
     * @inheritDoc
     */
    public void removeValidationResultsReporter(ValidationResultsReporter reporter)
    {
        this.validationResultsReporters.remove(reporter);
    }
    
    /**
     * @inheritDoc
     */
    public void removeChildForm(Form childForm) {
        getFormModel().removeChild(childForm.getFormModel());
        childForms.remove(childForm.getId());
        Iterator it = childForm.getValidationResultsReporters().iterator();
        while (it.hasNext())
        {
            ValidationResultsReporter reporter = (ValidationResultsReporter)it.next();
            reporter.removeParent();
        }
    }
    /**
     * Return a child form of this form with the given form id.
     * @param id of child form
     * @return child form, null if no child form with the given id has been registered
     */
    protected Form getChildForm(String id) {
        return (Form)childForms.get(id);
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
            JRootPane rootPane = SwingUtilities.getRootPane(getControl());
            return rootPane == null ? null : rootPane.getDefaultButton();
        }
        return null;
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
                getFormModel().setFormObject(createNewObject());
                getFormModel().setEnabled(true);
                editingNewFormObject = true;
                if (isEditingFormObjectSelected()) {
                    setEditingFormObjectIndexSilently(-1);
                }
            }
        };
        newFormObjectCmd.setSecurityControllerId(getNewFormObjectSecurityControllerId());
        attachFormGuard(newFormObjectCmd, FormGuard.LIKE_NEWFORMOBJCOMMAND);
        return (ActionCommand)getCommandConfigurer().configure(newFormObjectCmd);
    }

    /**
     * Create a new object to install into the form.  By default, this simply returns
     * null.  This will cause the form model to instantiate a new copy of the model object
     * class.  Subclasses should override this method if they need more control over how
     * new objects are constructed.
     * 
     * @return new object for editing
     */
    protected Object createNewObject() {
        return null;
    }

    private boolean isEditingFormObjectSelected() {
        if (editingFormObjectIndexHolder == null) {
            return false;
        }
        int value = ((Integer)editingFormObjectIndexHolder.getValue()).intValue();
        return value != -1;
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
                commit();
            }
        };
        commitCmd.setSecurityControllerId(getCommitSecurityControllerId());
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
                revert();
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

    /**
     * Subclasses may override to return a security controller id to be attached to the
     * newFormObject command. The default is
     * <code>[formModel.id] + "." + [getNewFormObjectCommandId()]</code>.
     * <p>
     * This id can be mapped to a specific security controller using the
     * SecurityControllerManager service.
     * 
     * @return security controller id, may be null if the face id is null
     * @see org.springframework.richclient.security.SecurityControllerManager
     */
    protected String getNewFormObjectSecurityControllerId() {
        return constructSecurityControllerId(getNewFormObjectCommandId());
    }

    /**
     * Subclasses may override to return a security controller id to be attached to the
     * commit command. The default is The default is
     * <code>[formModel.id] + "." + [getCommitCommandFaceDescriptorId()]</code>.
     * <p>
     * This id can be mapped to a specific security controller using the
     * SecurityControllerManager service.
     * 
     * @return security controller id, may be null if the face id is null
     * @see org.springframework.richclient.security.SecurityControllerManager
     */
    protected String getCommitSecurityControllerId() {
        return constructSecurityControllerId(getCommitCommandFaceDescriptorId());
    }

    /**
     * Construct a default security controller Id for a given command face id.
     * The id will be a combination of the form model id, if any, and the face id.
     * <p>
     * <code>[formModel.id] + "." + [commandFaceId]</code> if the form model id is not
     * null.
     * <p>
     * <code>[commandFaceId]</code> if the form model is null.
     * <p>
     * <code>null</code> if the commandFaceId is null.
     * @param commandFaceId
     * @return default security controller id
     */
    protected String constructSecurityControllerId( String commandFaceId ) {
        String id = null;
        String formModelId = getFormModel().getId();
        
        if( commandFaceId != null ) {
            id = (formModelId != null) ? formModelId + "." + commandFaceId : commandFaceId;
        }
        return id;
    }

    protected void attachFormErrorGuard(Guarded guarded) {
        attachFormGuard(guarded, FormGuard.FORMERROR_GUARDED);
    }

    protected void attachFormGuard(Guarded guarded, int mask) {
        this.formGuard.addGuarded(guarded, mask);
    }

    protected void detachFormGuard(Guarded guarded) {
        this.formGuard.removeGuarded(guarded);
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

    /**
     * Construct the validation results reporter for this form and attach it to the
     * provided Guarded object. An instance of {@link SimpleValidationResultsReporter}
     * will be constructed and returned.  All registered child forms will be attached
     * to the same <code>guarded</code> and <code>messageReceiver</code> as this form.
     */
    public ValidationResultsReporter newSingleLineResultsReporter(Guarded guarded, Messagable messageReceiver) {

        SimpleValidationResultsReporter reporter =
            new SimpleValidationResultsReporter( formModel.getValidationResults(), guarded, messageReceiver );

        // Configure all our child forms with this same data
        for( Iterator iter = childForms.values().iterator(); iter.hasNext(); ) {
            Form childForm = (Form) iter.next();
            ValidationResultsReporter child = childForm.newSingleLineResultsReporter( guarded, messageReceiver );
            reporter.addChild( child );
            childForm.getFormModel().validate();    // Force an initial validation
        }
        validationResultsReporters.add(reporter);
        
        return reporter;
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