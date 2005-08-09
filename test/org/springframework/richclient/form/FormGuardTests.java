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

import junit.framework.TestCase;

import org.springframework.binding.form.support.ValidatingFormModel;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.core.Guarded;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

/**
 * @author Peter De Bruycker
 */
public class FormGuardTests extends TestCase {

    private ValidatingFormModel formModel;

    private TestGuarded guarded;

    private static class TestGuarded implements Guarded {

        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private static class TestBean {
        private String field;
        private String field2;

        public String getField() {
            return field;
        }

        public String getField2() {
            return field2;
        }

        public void setField(String string) {
            field = string;
        }

        public void setField2(String string) {
            field2 = string;
        }
    }

    protected void setUp() throws Exception {
        DefaultRulesSource rulesSource = new DefaultRulesSource();
        Rules rules = new Rules(TestBean.class);
        rules.add("field", rules.required());
        rulesSource.addRules(rules);

        // load application
        Application.load(null);
        new Application(new DefaultApplicationLifecycleAdvisor());
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        Application.services().setApplicationContext(applicationContext);
        Application.services().setRulesSource(rulesSource);
        applicationContext.refresh();

        guarded = new TestGuarded();
        formModel = FormModelHelper.createFormModel(new TestBean());
    }

    public void testEnabledState() {
        guarded.setEnabled(true);
        formModel.setEnabled(true);

        new FormGuard(formModel, guarded);
        assertTrue("guarded should still be enabled", guarded.isEnabled());

        formModel.setEnabled(false);
        assertFalse("guarded should be disabled", guarded.isEnabled());

        formModel.setEnabled(true);
        assertTrue("guarded should be enabled", guarded.isEnabled());
    }

    public void testErrorState() {        
        guarded.setEnabled(true);
        formModel.setEnabled(true);

        new FormGuard(formModel, guarded);
        assertTrue("guarded should still be enabled", guarded.isEnabled());

        formModel.getValueModel("field").setValue(null);
        assertTrue(formModel.getHasErrors());
        assertFalse("guarded should be disabled", guarded.isEnabled());

        formModel.getValueModel("field").setValue("test");
        assertFalse(formModel.getHasErrors());
        assertTrue("guarded should be enabled", guarded.isEnabled());
    }
}
