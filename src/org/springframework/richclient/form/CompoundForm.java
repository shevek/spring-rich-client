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

import org.springframework.beans.BeanUtils;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.HierarchicalFormModel;

/**
 * @author Keith Donald
 */
public class CompoundForm {
    private HierarchicalFormModel compoundFormModel;

    public CompoundForm() {

    }

    public CompoundForm(Class clazz) {
        this(BeanUtils.instantiateClass(clazz));
    }

    public CompoundForm(Object formObject) {
        setFormObject(formObject);
    }

    public ConfigurableFormModel newPageFormModel(String formName) {
        return FormModelHelper.createChildPageFormModel(getFormModel(), formName);
    }

    public HierarchicalFormModel getFormModel() {
        return compoundFormModel;
    }

    public Object getFormObject() {
        return compoundFormModel.getFormObject();
    }

    public void setFormObject(Object formObject) {
        if (compoundFormModel == null) {
            this.compoundFormModel = createCompoundFormModel(formObject);
        }
        this.compoundFormModel.setFormObject(formObject);
    }

    protected HierarchicalFormModel createCompoundFormModel(Object formObject) {
        return FormModelHelper.createCompoundFormModel(formObject);
    }

    public void commit() {
        compoundFormModel.commit();
    }

    public void revert() {
        compoundFormModel.revert();
    }

}