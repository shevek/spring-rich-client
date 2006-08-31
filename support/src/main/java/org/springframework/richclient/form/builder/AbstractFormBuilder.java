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

import org.springframework.binding.form.FormModel;
import org.springframework.core.closure.Constraint;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.swing.ComboBoxBinder;
import org.springframework.util.Assert;

/**
 * @author oliverh
 * @author Mathias Broekelmann
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

    /**
     * @deprecated Use {@link #createDefaultBinding(String)} instead
     */
    protected Binding getDefaultBinding(String fieldName) {
        return createDefaultBinding(fieldName);
    }

    protected Binding createDefaultBinding(String fieldName) {
        return getBindingFactory().createBinding(fieldName);
    }
    
    /**
     * @deprecated Use {@link #createBinding(String,JComponent)} instead
     */
    protected Binding getBinding(String fieldName, JComponent component) {
        return createBinding(fieldName, component);
    }

    protected Binding createBinding(String fieldName, JComponent component) {
        return getBindingFactory().bindControl(component, fieldName);
    }

    protected Binding createBinding(String fieldName, JComponent component, Map context) {
        return getBindingFactory().bindControl(component, fieldName, context);
    }

    /**
     * @deprecated Use {@link #createSelector(String,Constraint)} instead
     */
    protected JComponent getSelector(String fieldName, Constraint filter) {
        return createSelector(fieldName, filter);
    }

    /**
     * Creates a component which is used as a selector in the form. This implementation creates a {@link JComboBox}
     * 
     * @param fieldName
     *            the name of the field for the selector
     * @param filter
     *            an optional filter constraint
     * @return the component to use for a selector, not null
     */
    protected JComponent createSelector(String fieldName, Constraint filter) {
        Map context = new HashMap();
        context.put(ComboBoxBinder.FILTER_KEY, filter);
        return getBindingFactory().createBinding(JComboBox.class, fieldName).getControl();
    }
    
    /**
     * Creates a component which is used as a scrollpane for a component 
     * 
     * @param fieldName the fieldname for the scrollpane
     * @param component the component to place into the scrollpane
     * @return the scrollpane component
     */
    protected JComponent createScrollPane(String fieldName, JComponent component) {
        return getComponentFactory().createScrollPane(component);
    }

    /**
     * @deprecated Use {@link #createPasswordField(String)} instead
     */
    protected JPasswordField getPasswordField(String fieldName) {
        return createPasswordField(fieldName);
    }

    protected JPasswordField createPasswordField(String fieldName) {
        return getComponentFactory().createPasswordField();
    }

    /**
     * @deprecated Use {@link #createTextArea(String)} instead
     */
    protected JComponent getTextArea(String fieldName) {
        return createTextArea(fieldName);
    }

    protected JComponent createTextArea(String fieldName) {
        return getComponentFactory().createTextArea(5, 40);
    }

    /**
     * @deprecated Use {@link #createLabelFor(String,JComponent)} instead
     */
    protected JLabel getLabelFor(String fieldName, JComponent component) {
        return createLabelFor(fieldName, component);
    }

    protected JLabel createLabelFor(String fieldName, JComponent component) {
        JLabel label = getComponentFactory().createLabel("");
        getFormModel().getFieldFace(fieldName).configure(label);
        label.setLabelFor(component);
        return label;
    }
}