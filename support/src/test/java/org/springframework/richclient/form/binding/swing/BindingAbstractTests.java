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
package org.springframework.richclient.form.binding.swing;

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.test.SpringRichTestCase;

public abstract class BindingAbstractTests extends SpringRichTestCase {
    
    protected ConfigurableFormModel fm;

    protected ValueModel vm;

    protected String property;

    public void doSetUp() {
        fm = new DefaultFormModel(createTestBean());        
        property = setUpBinding();
        vm = fm.getValueModel(property);
    }

    protected TestBean createTestBean() {
        return new TestBean();
    }

    protected abstract String setUpBinding();

    public abstract void testComponentTracksEnabledChanges();

    public abstract void testComponentTracksReadOnlyChanges();

    public abstract void testComponentUpdatesValueModel();

    public abstract void testValueModelUpdatesComponent();
}
