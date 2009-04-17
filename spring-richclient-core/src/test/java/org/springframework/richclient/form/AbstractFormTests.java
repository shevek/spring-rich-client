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
 */
package org.springframework.richclient.form;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.test.SpringRichTestCase;

import javax.swing.*;

/**
 * @author Mathias Broekelmann
 * 
 */
public abstract class AbstractFormTests extends SpringRichTestCase {

    /**
     * Test method for
     * {@link org.springframework.richclient.form.AbstractForm#addChildForm(org.springframework.richclient.form.Form)}.
     */
    public void testAddChildForm() {
        TestBean testBean = new TestBean();
        testBean.setNestedProperty(new TestBean());
        HierarchicalFormModel model = new DefaultFormModel(testBean);
        ValidatingFormModel childModel = FormModelHelper.createChildPageFormModel(model, "test", "nestedProperty");
        AbstractForm form = new TestAbstractForm(model);
        form.addChildForm(new TestAbstractForm(childModel));
    }

    private static class TestAbstractForm extends AbstractForm {

        public TestAbstractForm() {
            super();
        }

        public TestAbstractForm(FormModel formModel, String formId) {
            super(formModel, formId);
        }

        public TestAbstractForm(FormModel pageFormModel) {
            super(pageFormModel);
        }

        public TestAbstractForm(HierarchicalFormModel parentFormModel, String formId, ValueModel childFormObjectHolder) {
            super(parentFormModel, formId, childFormObjectHolder);
        }

        public TestAbstractForm(String formId) {
            super(formId);
        }

        public TestAbstractForm(Object formObject) {
            super(formObject);
        }

        public TestAbstractForm(HierarchicalFormModel parentFormModel, String formId, String childFormObjectPropertyPath) {
            super(parentFormModel, formId, childFormObjectPropertyPath);
        }

        protected JComponent createFormControl() {
            return null;
        }

    }
}
