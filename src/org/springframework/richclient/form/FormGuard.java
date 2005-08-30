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

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.richclient.core.Guarded;

/**
 * 
 * @author Keith Donald
 */
public class FormGuard implements PropertyChangeListener {

    private final ValidatingFormModel formModel;

    private final Guarded guarded;

    public FormGuard(ValidatingFormModel formModel, Guarded guarded) {
        this.formModel = formModel;
        this.formModel.addPropertyChangeListener(FormModel.ENABLED_PROPERTY, this);
        this.formModel.getValidationResults().addPropertyChangeListener(ValidationResultsModel.HAS_ERRORS_PROPERTY,
                this);
        this.guarded = guarded;
        update(formModel);
    }

    protected void update(ValidatingFormModel formModel) {
        guarded.setEnabled((!formModel.getValidationResults().getHasErrors()) && formModel.isEnabled());
    }

    public void propertyChange(PropertyChangeEvent e) {
        update(formModel);
    }
}