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

import junit.framework.TestCase;

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.value.ValueModel;

public abstract class AbstractBindingTests extends TestCase {
    
    protected ConfigurableFormModel fm;

    protected ValueModel vm;

    public final void setUp() {
        fm = new DefaultFormModel(new TestBean());        
        String property = setUpBinding();
        vm = fm.getValueModel(property);
    }

    protected abstract String setUpBinding();

    public abstract void testComponentTracksEnabledChanges();

    public abstract void testComponentTracksReadOnlyChanges();

    public abstract void testComponentUpdatesValueModel();

    public abstract void testValueModelUpdatesComponent();
}
