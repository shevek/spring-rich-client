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
package org.springframework.richclient.dialog;

import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.forms.SwingFormModel;

/**
 * @author Keith Donald
 */
public class CompoundForm {
    private NestingFormModel formModel;

    public ValueModel formObjectHolder;

    public CompoundForm() {
        this(null);
    }

    public CompoundForm(Object formObject) {
        setFormObject(formObject);
        afterPropertiesSet();
    }

    public void setFormObject(Object formObject) {
        if (formObjectHolder == null) {
            this.formObjectHolder = new ValueHolder(formObject);
        }
        else {
            this.formObjectHolder.setValue(formObject);
        }
    }

    public void afterPropertiesSet() {
        if (this.formModel == null) {
            this.formModel = SwingFormModel
                    .createCompoundFormModel(formObjectHolder);
        }
    }

    public SwingFormModel newPageFormModel(String formName) {
        return SwingFormModel
                .createChildPageFormModel(getFormModel(), formName);
    }

    public NestingFormModel getFormModel() {
        return formModel;
    }

    public Object getFormObject() {
        return formObjectHolder.getValue();
    }

    public ValueModel getFormObjectHolder() {
        return formObjectHolder;
    }

    public void commit() {
        formModel.commit();
    }

    public void revert() {
        formModel.revert();
    }

}