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

import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageAreaPane;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.rules.values.FormModel;
import org.springframework.rules.values.NestingFormModel;
import org.springframework.rules.values.ValidationListener;
import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public abstract class AbstractFormPage extends AbstractControlFactory {

    private SwingFormModel pageFormModel;

    public AbstractFormPage(FormModel formModel, String pageName) {
        if (formModel instanceof NestingFormModel) {
            this.pageFormModel = SwingFormModel.createChildPageFormModel(
                    (NestingFormModel)formModel, pageName);
        }
        else if (formModel instanceof SwingFormModel) {
            this.pageFormModel = (SwingFormModel)formModel;
        }
        else {
            throw new IllegalArgumentException(
                    "Unsupported form model implementation " + formModel);
        }
    }

    public AbstractFormPage(NestingFormModel parent, String pageName) {
        this(SwingFormModel.createChildPageFormModel(parent, pageName));
    }

    public AbstractFormPage(SwingFormModel pageFormModel) {
        Assert.notNull(pageFormModel);
        this.pageFormModel = pageFormModel;
    }

    public SwingFormModel getFormModel() {
        return pageFormModel;
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
            MessageAreaPane messageAreaPane) {
        return getFormModel().createSingleLineResultsReporter(guarded,
                messageAreaPane);
    }

    protected void addFormObjectListener(ValueListener listener) {
        getFormObjectHolder().addValueListener(listener);
    }

    protected void removeFormObjectListener(ValueListener listener) {
        getFormObjectHolder().removeValueListener(listener);
    }

    protected void addFormValueListener(String formProperty,
            ValueListener listener) {
        getFormModel().addValueListener(formProperty, listener);
    }

    protected void removeFormValueListener(String formProperty,
            ValueListener listener) {
        getFormModel().removeValueListener(formProperty, listener);
    }

    protected void registerGuard(Guarded guarded) {
        FormGuard c = new FormGuard(getFormModel(), guarded);
        addValidationListener(c);
    }

    public boolean hasErrors() {
        return pageFormModel.hasErrors();
    }

    public void commit() {
        pageFormModel.commit();
    }

    public void revert() {
        pageFormModel.revert();
    }
}