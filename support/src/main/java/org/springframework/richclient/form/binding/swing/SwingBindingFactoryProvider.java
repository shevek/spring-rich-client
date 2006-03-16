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
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.BindingFactoryProvider;

/**
 * This provider constructs instances of {@link SwingBindingFactory} on demand.
 * @author Larry Streepy
 * @see org.springframework.richclient.application.ApplicationServices#getBindingFactory(FormModel)
 * @see org.springframework.richclient.application.ApplicationServices#getBindingFactoryProvider()
 */
public class SwingBindingFactoryProvider implements BindingFactoryProvider {

    /**
     * Produce a BindingFactory using the provided form model.
     * @param formModel Form model on which to construct the BindingFactory
     * @return BindingFactory
     */
    public BindingFactory getBindingFactory(FormModel formModel) {
        return new SwingBindingFactory((ConfigurableFormModel)formModel);
    }

}