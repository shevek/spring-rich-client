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

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public abstract class AbstractFormPage extends AbstractControlFactory implements
        FormPage {
    private NestingFormModel parent;

    private SwingFormModel pageFormModel;

    private String formPageId;

    protected AbstractFormPage() {

    }

    protected AbstractFormPage(String formPageId) {
        this.formPageId = formPageId;
    }

    protected AbstractFormPage(SwingFormModel pageFormModel) {
        setFormModel(pageFormModel);
    }

    protected AbstractFormPage(NestingFormModel parent, String formPageId) {
        this(SwingFormModel.createChildPageFormModel(parent, formPageId));
        this.formPageId = formPageId;
    }

    protected AbstractFormPage(NestingFormModel parent, String formPageId,
            String parentFormObjectPropertyPath) {
        this.parent = parent;
        setFormModel(SwingFormModel.createChildPageFormModel(parent,
                formPageId, parentFormObjectPropertyPath));
        this.formPageId = formPageId;
    }

    protected AbstractFormPage(NestingFormModel parent, String formPageId,
            ValueModel childFormObjectHolder) {
        this.parent = parent;
        setFormModel(SwingFormModel.createChildPageFormModel(parent,
                formPageId, childFormObjectHolder));
        this.formPageId = formPageId;
    }

    protected AbstractFormPage(FormModel formModel, String formPageId) {
        this.formPageId = formPageId;
        if (formModel instanceof NestingFormModel) {
            this.parent = (NestingFormModel)formModel;
            setFormModel(SwingFormModel.createChildPageFormModel(this.parent,
                    formPageId));
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
        return formPageId;
    }

    public SwingFormModel getFormModel() {
        return pageFormModel;
    }

    protected void setFormModel(SwingFormModel formModel) {
        Assert.notNull(formModel);
        this.pageFormModel = formModel;
    }

    protected NestingFormModel getParent() {
        return this.parent;
    }

    public Object getFormObject() {
        return pageFormModel.getFormObject();
    }

    public ValueModel getFormObjectHolder() {
        return pageFormModel.getFormObjectHolder();
    }

    public Object getValue(String formProperty) {
        return pageFormModel.getValue(formProperty);
    }

    public ValueModel getValueModel(String formProperty) {
        ValueModel valueModel = pageFormModel.getValueModel(formProperty);
        if (valueModel == null) {
            logger.warn("A value model for property '" + formProperty
                    + "' could not be found.  Typo?");
        }
        return valueModel;
    }

    public void addValidationListener(ValidationListener listener) {
        pageFormModel.addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener) {
        pageFormModel.removeValidationListener(listener);
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

    protected void addFormPropertyListener(String formProperty,
            ValueChangeListener listener) {
        getFormModel().addValueChangeListener(formProperty, listener);
    }

    protected void removeFormPropertyListener(String formProperty,
            ValueChangeListener listener) {
        getFormModel().removeValueChangeListener(formProperty, listener);
    }

    protected void registerGuard(Guarded guarded) {
        FormGuard c = new FormGuard(getFormModel(), guarded);
        addValidationListener(c);
    }

    public boolean hasErrors() {
        return pageFormModel.getHasErrors();
    }

    public void commit() {
        pageFormModel.commit();
    }

    public void revert() {
        pageFormModel.revert();
    }
}