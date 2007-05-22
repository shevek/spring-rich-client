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

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.validation.support.RulesValidator;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.rules.RulesSource;

/**
 * This provides a collection of useful functions for working with {@link FormModel}s.
 *
 * @author Keith Donald
 * @author Jim Moore
 */
public class FormModelHelper {

    public static ValidatingFormModel createFormModel(Object formObject, String formId) {
        return createFormModel(formObject, true, formId);
    }

    public static ValidatingFormModel createUnbufferedFormModel(Object formObject, String formId) {
        return createFormModel(formObject, false, formId);
    }

    public static ValidatingFormModel createFormModel(Object formObject, boolean bufferChanges, String formId) {
        DefaultFormModel formModel = new DefaultFormModel(formObject, bufferChanges);
        formModel.setId(formId);
        return formModel;
    }

    public static ValidatingFormModel createFormModel(Object formObject, boolean bufferChanges, RulesSource rulesSource,
                                                      String formId) {
        DefaultFormModel formModel = new DefaultFormModel(formObject, bufferChanges);
        formModel.setId(formId);
        formModel.setValidator(new RulesValidator(formModel, rulesSource));
        return formModel;
    }

    public static ValidatingFormModel createCompoundFormModel(Object formObject, String formId) {
        DefaultFormModel model = new DefaultFormModel(formObject);
        model.setId(formId);
        return model;
    }

    public static ValidatingFormModel createFormModel(ValueModel formObjectHolder) {
        return createFormModel(formObjectHolder, true, null);
    }

    public static ValidatingFormModel createFormModel(ValueModel formObjectHolder, String formId) {
        return createFormModel(formObjectHolder, true, formId);
    }

    public static ValidatingFormModel createUnbufferedFormModel(ValueModel formObjectHolder, String formId) {
        return createFormModel(formObjectHolder, false, formId);
    }

    public static ValidatingFormModel createFormModel(ValueModel formObjectHolder, boolean bufferChanges,
                                                      String formId) {
        DefaultFormModel formModel = new DefaultFormModel(formObjectHolder, bufferChanges);
        formModel.setId(formId);
        return formModel;
    }

    public static ValidatingFormModel createCompoundFormModel(ValueModel formObjectHolder, String formId) {
        DefaultFormModel model = new DefaultFormModel(formObjectHolder);
        model.setId(formId);
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

    public static HierarchicalFormModel createCompoundFormModel(Object formObject) {
        return createCompoundFormModel(formObject, null);
    }

    public static FormModel createChildPageFormModel(HierarchicalFormModel parentModel) {
        return createChildPageFormModel(parentModel, null);
    }

    public static ValidatingFormModel createChildPageFormModel(HierarchicalFormModel parentModel,
                                                               String childPageName) {
        ValidatingFormModel child = createFormModel(parentModel.getFormObjectHolder());
        child.setId(childPageName);
        parentModel.addChild(child);
        return child;
    }

    /**
     * Create a child form model nested by this form model identified by the
     * provided name. The form object associated with the created child model is
     * the value model at the specified parent property path.
     *
     * @param parentModel                 the model to create the FormModelHelper in
     * @param childPageName               the name to associate the created FormModelHelper
     *                                    with in the groupingModel
     * @param childFormObjectPropertyPath the path into the groupingModel that
     *                                    the FormModelHelper is for
     *
     * @return The child form model
     */
    public static ValidatingFormModel createChildPageFormModel(HierarchicalFormModel parentModel, String childPageName,
                                                               String childFormObjectPropertyPath) {
        final ValueModel childValueModel = parentModel.getValueModel(childFormObjectPropertyPath);
        return createChildPageFormModel(parentModel, childPageName, childValueModel);
    }

    public static ValidatingFormModel createChildPageFormModel(HierarchicalFormModel parentModel, String childPageName,
                                                               ValueModel childFormObjectHolder) {
        ValidatingFormModel child = createFormModel(childFormObjectHolder);
        child.setId(childPageName);
        parentModel.addChild(child);
        return child;
    }

    public static ValidationResultsReporter createSingleLineResultsReporter(ValidatingFormModel formModel,
                                                                     Messagable messageReceiver) {
        return new SimpleValidationResultsReporter(formModel.getValidationResults(), messageReceiver);
    }


    /**
     * Returns the child of the formModel with the given page name.
     *
     * @param formModel     the parent model to get the child from
     * @param childPageName the name of the child to retrieve
     *
     * @return null the child can not be found
     *
     * @throws IllegalArgumentException if childPageName or formModel are null
     */
    public static FormModel getChild(HierarchicalFormModel formModel, String childPageName) {
        if (childPageName == null) throw new IllegalArgumentException("childPageName == null");
        if (formModel == null) throw new IllegalArgumentException("formModel == null");

        final FormModel[] children = formModel.getChildren();

        if (children == null) return null;

        for (int i = 0; i < children.length; i++) {
            final FormModel child = children[i];
            if (childPageName.equals(child.getId())) return child;
        }

        return null;
    }

}
