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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.rules.Constraint;
import org.springframework.util.Assert;

/**
 * @author oliverh
 */
public abstract class AbstractFormBuilder {

    private final SwingFormModel formModel;
    
    private ComponentFactory componentFactory;

    protected AbstractFormBuilder(SwingFormModel formModel) {
        Assert.notNull(formModel);
        this.formModel = formModel;
        
    }

    protected ComponentFactory getComponentFactory() {
        if (componentFactory == null) {
            componentFactory = Application.services().getComponentFactory();
        }
        return componentFactory;
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    protected SwingFormModel getFormModel() {
        return formModel;
    }

    protected JComponent getDefaultComponent(String propertyName) {
        return getFormModel().createBoundControl(propertyName);
    }

    protected JComponent getSelector(String propertyName, Constraint filter) {
        JComponent propertyEditor = null;
        if (getFormModel().getMetadataAccessStrategy().isEnumeration(
                propertyName)) {
            propertyEditor = getFormModel().createBoundEnumComboBox(
                    propertyName, filter);
        }
        Assert.notNull(propertyEditor, "Unsupported filterable property "
                + propertyName);
        return propertyEditor;
    }

    protected JPasswordField getPasswordField(String propertyName) {
        JPasswordField field = (JPasswordField)getFormModel().bind(
                new JPasswordField(8), propertyName);
        return field;
    }

    protected JComponent getTextArea(String propertyName) {
        JTextArea textArea = GuiStandardUtils.createStandardTextArea(5, 40);
        JComponent component = new JScrollPane(getFormModel().bind(textArea,
                propertyName));
        return component;
    }

    protected JLabel getLabelFor(String propertyName, JComponent component) {
        JLabel label = formModel.createLabel(propertyName);
        label.setLabelFor(component);
        return label;
    }
}