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
package org.springframework.richclient.form.binding.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class BasicBindingFactory implements BindingFactory {

    private BinderSelectionStrategy binderSelectionStrategy;
    
    private FormModel formModel;
    
    public BasicBindingFactory(BinderSelectionStrategy binderSelectionStrategy, FormModel formModel) {
        this.binderSelectionStrategy = binderSelectionStrategy;
        this.formModel = formModel;
    }
    
    public Binding createBinding(String formPropertyPath) {
        return createBinding(formPropertyPath, Collections.EMPTY_MAP);
    }

    public Binding createBinding(Class controlType, String formPropertyPath) {
        return createBinding(controlType, formPropertyPath, Collections.EMPTY_MAP);
    }

    public Binding bindControl(JComponent control, String formPropertyPath) {
        return bindControl(control, formPropertyPath, Collections.EMPTY_MAP);
    }
    
    public Binding createBinding(String formPropertyPath, Map context) {
        Assert.notNull(context, "Context must not be null");
        Binder binder = binderSelectionStrategy.selectBinder(formModel, formPropertyPath);
        return binder.bind(formModel, formPropertyPath, context);
    }

    public Binding createBinding(Class controlType, String formPropertyPath, Map context) {
        Assert.notNull(context, "Context must not be null");
        Binder binder = binderSelectionStrategy.selectBinder(controlType, formModel, formPropertyPath);
        return binder.bind(formModel, formPropertyPath, context);
    }

    public Binding bindControl(JComponent control, String formPropertyPath, Map context) {
        Assert.notNull(context, "Context must not be null");
        Binder binder = binderSelectionStrategy.selectBinder(control.getClass(), formModel, formPropertyPath);
        return binder.bind(control, formModel, formPropertyPath, context);
    }
    
    public FormModel getFormModel() {
        return formModel;
    }
    
    protected BinderSelectionStrategy getBinderSelectionStrategy() {
        return binderSelectionStrategy;
    }
    
    protected Map createContext(String key, Object value) {
        Map context = new HashMap(1);
        context.put(key, value);
        return context;
    }
}