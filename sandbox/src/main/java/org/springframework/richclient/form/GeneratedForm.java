/*
 * Copyright 2002-2006 the original author or authors.
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
 *
 */

package org.springframework.richclient.form;

import javax.swing.JComponent;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.value.ValueModel;

/**
 * Convenience class for producing a Spring Rich {@link Form} based on a
 * pre-generated form UI (typically, a form that has been created in a form
 * designer such as Matisse or JFormDesigner).  This implementation handles
 * the most common case where a developer designs a form and wants to display
 * it without any custom functionality beyond what Spring Rich already
 * provides.
 * <p/>
 * In order to better facilitate the common usage scenarios and to avoid
 * developers having to subclass <code>GeneratedForm</code>, many convenience
 * constructors are provided, along with making the
 * {@link #setFormModel(ValidatingFormModel)} and {@link #setId(String)}
 * methods public. 
 *
 * @author Andy DePue
 * @author Peter De Bruycker
 */
public class GeneratedForm extends AbstractForm implements InitializingBean {
    private FormUIProvider formUIProvider;

    public GeneratedForm() {
        super();
    }

    public GeneratedForm(String formId) {
        super(formId);
    }

    public GeneratedForm(Object formObject) {
        super(formObject);
    }

    public GeneratedForm(FormModel pageFormModel) {
        super(pageFormModel);
    }

    public GeneratedForm(FormModel formModel, String formId) {
        super(formModel, formId);
    }

    public GeneratedForm(HierarchicalFormModel parentFormModel, String formId, String childFormObjectPropertyPath) {
        super(parentFormModel, formId, childFormObjectPropertyPath);
    }

    public GeneratedForm(HierarchicalFormModel parentFormModel, String formId, ValueModel childFormObjectHolder) {
        super(parentFormModel, formId, childFormObjectHolder);
    }

    public GeneratedForm(final FormUIProvider formUIProvider) {
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(String formId, final FormUIProvider formUIProvider) {
        super(formId);
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(Object formObject, final FormUIProvider formUIProvider) {
        super(formObject);
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(Object formObject, String formId, final FormUIProvider formUIProvider) {
        super(formObject);
        setId(formId);
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(FormModel pageFormModel, final FormUIProvider formUIProvider) {
        super(pageFormModel);
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(FormModel formModel, String formId, final FormUIProvider formUIProvider) {
        super(formModel, formId);
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(HierarchicalFormModel parentFormModel, String formId, String childFormObjectPropertyPath, final FormUIProvider formUIProvider) {
        super(parentFormModel, formId, childFormObjectPropertyPath);
        this.formUIProvider = formUIProvider;
    }

    public GeneratedForm(HierarchicalFormModel parentFormModel, String formId, ValueModel childFormObjectHolder, final FormUIProvider formUIProvider) {
        super(parentFormModel, formId, childFormObjectHolder);
        this.formUIProvider = formUIProvider;
    }
    

    //
    // METHODS FROM CLASS AbstractForm
    //

    protected JComponent createFormControl() {
        this.formUIProvider.bind(getBindingFactory(), this);
        return this.formUIProvider.getControl();
    }

    /**
     * Provides public access to this method as a convenience.
     *
     * @param formModel
     */
    public void setFormModel(ValidatingFormModel formModel) {
        super.setFormModel(formModel);
    }


    /**
     * Provides public access to this method as a convenience.
     *
     * @param formId
     */
    public void setId(String formId) {
        super.setId(formId);
    }
    
    
    
    
    

    //
    // METHODS FROM INTERFACE InitializingBean
    //

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.formUIProvider, "formUIProvider must be set");
    }




    //
    // SIMPLE PROPERTY ACCESSORS
    //

    public FormUIProvider getFormUIProvider() {
        return formUIProvider;
    }

    public void setFormUIProvider(final FormUIProvider formUIProvider) {
        this.formUIProvider = formUIProvider;
    }
}
