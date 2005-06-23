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

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.form.support.CompoundFormModel;
import org.springframework.binding.form.support.ValidatingFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.rules.RulesSource;

/**
 * @author Keith Donald
 */
public class FormModelHelper {

    public static ValidatingFormModel createFormModel(Object formObject, String formId) {
        return createFormModel(formObject, true, formId);
    }

    public static ValidatingFormModel createUnbufferedFormModel(Object formObject, String formId) {
        return createFormModel(formObject, false, formId);
    }

    public static ValidatingFormModel createFormModel(Object formObject, boolean bufferChanges, String formId) {
        return createFormModel(formObject, Application.services().getRulesSource(), bufferChanges, formId);
    }

    public static ValidatingFormModel createFormModel(Object formObject, RulesSource rulesSource,
            boolean bufferChanges, String formId) {
        ValidatingFormModel formModel = new ValidatingFormModel(formObject);
        formModel.setId(formId);
        formModel.setRulesSource(rulesSource);
        formModel.setBufferChangesDefault(bufferChanges);
        return formModel;
    }

    public static NestingFormModel createCompoundFormModel(Object formObject, String formId) {
        CompoundFormModel model = new CompoundFormModel(formObject);
        model.setId(formId);
        model.setRulesSource(Application.services().getRulesSource());
        return model;
    }

    public static ValidatingFormModel createFormModel(Object formObject) {
        return createFormModel(formObject, true);
    }

    public static ValidatingFormModel createUnbufferedFormModel(Object formObject) {
        return createFormModel(formObject, false);
    }

    public static ValidatingFormModel createFormModel(Object formObject, boolean bufferChanges) {
        return createFormModel(formObject, bufferChanges, null);
    }

    public static NestingFormModel createCompoundFormModel(Object formObject) {
        return createCompoundFormModel(formObject, null);
    }

    public static ConfigurableFormModel createChildPageFormModel(NestingFormModel groupingModel, String pageName) {
        return groupingModel.createChild(pageName);
    }

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * the value model at the specified parent property path.
     * 
     * @param groupingModel the model to create the FormModelHelper in
     * @param childPageName the name to associate the created FormModelHelper
     *        with in the groupingModel
     * @param childFormObjectPropertyPath the path into the groupingModel that
     *        the FormModelHelper is for
     * @return The child form model
     */
    public static ConfigurableFormModel createChildPageFormModel(NestingFormModel groupingModel, String childPageName,
            String childFormObjectPropertyPath) {
        return groupingModel.createChild(childPageName, childFormObjectPropertyPath);
    }

    public static ConfigurableFormModel createChildPageFormModel(NestingFormModel groupingModel, String childPageName,
            ValueModel childFormObjectHolder) {
        return groupingModel.createChild(childPageName, childFormObjectHolder);
    }

    public static NestingFormModel createChildCompoundFormModel(NestingFormModel groupingModel, String childPageName,
            String parentPropertyFormObjectPath) {
        return groupingModel.createCompoundChild(childPageName, parentPropertyFormObjectPath);
    }

    public static ValidationListener createSingleLineResultsReporter(FormModel formModel, Guarded guardedComponent,
            Messagable messageReceiver) {
        return new SimpleValidationResultsReporter(formModel, guardedComponent, messageReceiver);
    }

}