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

import javax.swing.JComponent;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.builder.AbstractFormBuilder;
import org.springframework.util.closure.Constraint;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Keith Donald
 */
public class JGoodiesBeanFormBuilder extends AbstractFormBuilder implements
        BeanFormBuilder {

    private JGoodiesFormBuilder formBuilder;

    public JGoodiesBeanFormBuilder(SwingFormModel formModel,
            FormLayout formLayout) {
        this(formModel, formLayout, Application.services()
                .getComponentFactory());
    }

    public JGoodiesBeanFormBuilder(SwingFormModel formModel,
            FormLayout formLayout, ComponentFactory componentFactory) {
        super(formModel);
        this.formBuilder = new JGoodiesFormBuilder(formLayout, componentFactory);
        setComponentFactory(componentFactory);
    }

    public JGoodiesFormBuilder getWrappedFormBuilder() {
        return formBuilder;
    }

    public DefaultFormBuilder getDefaultFormBuilder() {
        return formBuilder.getDefaultFormBuilder();
    }

    public JComponent getForm() {
        return formBuilder.getForm();
    }

    public JComponent[] add(String formPropertyPath) {
        JComponent component = getDefaultComponent(formPropertyPath);
        return formBuilder.add(getLabelFor(formPropertyPath, component),
                component);
    }

    public JComponent[] addSelector(String formPropertyPath, Constraint filter) {
        JComponent component = getSelector(formPropertyPath, filter);
        return formBuilder.add(getLabelFor(formPropertyPath, component),
                component);
    }

    public JComponent[] addPasswordField(String formPropertyPath) {
        JComponent component = getPasswordField(formPropertyPath);
        return formBuilder.add(getLabelFor(formPropertyPath, component),
                component);
    }

    public JComponent[] addTextArea(String formPropertyPath) {
        JComponent component = getTextArea(formPropertyPath);
        return formBuilder.add(getLabelFor(formPropertyPath, component),
                "left,top", component);
    }
}