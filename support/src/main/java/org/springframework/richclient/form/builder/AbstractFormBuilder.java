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
package org.springframework.richclient.form.builder;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import org.springframework.binding.form.FormModel;
import org.springframework.core.closure.Constraint;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.swing.ComboBoxBinder;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;

/**
 * @author oliverh
 */
public abstract class AbstractFormBuilder {

    private final BindingFactory bindingFactory;

    private ComponentFactory componentFactory;

    protected AbstractFormBuilder(BindingFactory bindingFactory) {
        Assert.notNull(bindingFactory);
        this.bindingFactory = bindingFactory;

    }

    protected ComponentFactory getComponentFactory() {
        if (componentFactory == null) {
            componentFactory = (ComponentFactory)ApplicationServicesLocator.services().getService(ComponentFactory.class);
        }
        return componentFactory;
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    protected BindingFactory getBindingFactory() {
        return bindingFactory;
    }

    protected FormModel getFormModel() {
        return bindingFactory.getFormModel();
    }

    protected Binding getDefaultBinding(String propertyName) {
        return getBindingFactory().createBinding(propertyName);
    }
    
    protected Binding getBinding(String propertyName, JComponent component) {
        return getBindingFactory().bindControl(component, propertyName);
    }

    protected JComponent getSelector(String propertyName, Constraint filter) {
        Map context = new HashMap();
        context.put(ComboBoxBinder.FILTER_KEY, filter);
        return getBindingFactory().createBinding(JComboBox.class, propertyName).getControl();
    }

    protected JPasswordField getPasswordField(String propertyName) {
        return new JPasswordField(8);
    }

    protected JComponent getTextArea(String propertyName) {
        JTextArea textArea = GuiStandardUtils.createStandardTextArea(5, 40);
        return textArea;
    }

    protected JLabel getLabelFor(String propertyName, JComponent component) {
        JLabel label = getComponentFactory().createLabel("");
        getFormModel().getFieldFace(propertyName).configure(label);
        label.setLabelFor(component);
        return label;
    }
}