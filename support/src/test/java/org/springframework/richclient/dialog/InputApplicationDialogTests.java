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

import javax.swing.JTextField;

import org.springframework.richclient.application.support.StaticApplicationServices;
import org.springframework.richclient.test.SpringRichTestCase;
import org.springframework.rules.Rules;
import org.springframework.rules.RulesSource;
import org.springframework.rules.support.DefaultRulesSource;

/**
 * @author Peter De Bruycker
 */
public class InputApplicationDialogTests extends SpringRichTestCase {
    private static final String BUSINESS_FIELD = "name";

    private BusinessObject businessObject;

    public void testInitialEnabledState() {
        assertEnabledStateReflectsFormModelState(createDialog(businessObject, BUSINESS_FIELD));

        businessObject.setName("test");
        assertEnabledStateReflectsFormModelState(createDialog(businessObject, BUSINESS_FIELD));
    }

    private void assertEnabledStateReflectsFormModelState(InputApplicationDialog dialog) {
        assertEquals(dialog.getFormModel().getValidationResults().getHasErrors(), !dialog.isEnabled());
    }

    private InputApplicationDialog createDialog(BusinessObject businessObject, String field) {
        InputApplicationDialog dialog = new InputApplicationDialog(businessObject, BUSINESS_FIELD);
        dialog.createDialog();

        return dialog;
    }

    public void testConstructor() {
        InputApplicationDialog dialog = new InputApplicationDialog(businessObject, BUSINESS_FIELD);

        assertNotNull("No FormModel created", dialog.getFormModel());
        assertEquals("BusinessObject not set on FormModel", businessObject, dialog.getFormModel().getFormObject());

        assertNotNull("No inputField created", dialog.getInputField());
        assertTrue("Default inputField not a JTextField", dialog.getInputField() instanceof JTextField);
    }

    public void testEnabledByUserAction() {
        InputApplicationDialog dialog = createDialog(businessObject, BUSINESS_FIELD);

        assertFalse(dialog.isEnabled());

        dialog.getFormModel().getValueModel(BUSINESS_FIELD).setValue("test");
        assertEnabledStateReflectsFormModelState(dialog);

        dialog.getFormModel().getValueModel(BUSINESS_FIELD).setValue("");
        assertEnabledStateReflectsFormModelState(dialog);
    }

    protected void doSetUp() {
        // create business object
        businessObject = new BusinessObject();
    }

    /**
     * May be implemented in subclasses that need to register services with the global
     * application services instance.
     */
    protected void registerAdditionalServices( StaticApplicationServices applicationServices ) {
        applicationServices.registerService(new BusinessRulesSource(), RulesSource.class);
    }

    private class BusinessRulesSource extends DefaultRulesSource {
        public BusinessRulesSource() {
            Rules rules = new Rules(BusinessObject.class);
            rules.add(BUSINESS_FIELD, rules.required());
            addRules(rules);
        }
    }

    public class BusinessObject {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}