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
import javax.swing.JLabel;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.factory.ControlFactory;
import org.springframework.util.Assert;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * @author Keith Donald
 */
public class JGoodiesFormBuilder implements FormBuilder {

    private DefaultFormBuilder formBuilder;

    private ComponentFactory componentFactory;

    public JGoodiesFormBuilder(FormLayout layout) {
        this(layout, Application.services().getComponentFactory());
    }

    public JGoodiesFormBuilder(FormLayout layout,
            ComponentFactory componentFactory) {
        Assert.notNull(componentFactory);
        this.formBuilder = new DefaultFormBuilder(layout);
        this.componentFactory = componentFactory;
    }

    /**
     * @see org.springframework.richclient.forms.FormBuilder#getForm()
     */
    public JComponent getForm() {
        return formBuilder.getPanel();
    }

    public DefaultFormBuilder getDefaultFormBuilder() {
        return formBuilder;
    }
    
    /**
     * @see org.springframework.richclient.forms.FormBuilder#add(java.lang.String,
     *      javax.swing.JComponent)
     */
    public void add(String labelKey, JComponent labeledComponent) {
        JLabel label = componentFactory.createLabelFor(labelKey,
                labeledComponent);
        Assert.notNull(labeledComponent);
        formBuilder.append(label, labeledComponent);
    }

    public void add(String labelKey, String labelConstraints,
            JComponent labeledComponent) {
        JLabel label = componentFactory.createLabelFor(labelKey,
                labeledComponent);
        Assert.notNull(labeledComponent);
        formBuilder.nextLine();
        formBuilder.appendRelatedComponentsGapRow();
        formBuilder.nextRow();
        labelConstraints = formBuilder.getColumn() + "," + formBuilder.getRow()
                + "," + labelConstraints;
        formBuilder.appendRow("pref");
        formBuilder.add(label, labelConstraints);
        formBuilder.nextColumn(2);
        formBuilder.add(labeledComponent);
        formBuilder.nextLine();
    }

    public void addSeparator() {
        formBuilder.appendSeparator();
        formBuilder.nextLine();
    }

    public void addGapRow() {
        formBuilder.appendRelatedComponentsGapRow();
        formBuilder.nextLine();
    }

    public void addRow(ControlFactory controlFactory) {
        if (formBuilder.getRow() <= formBuilder.getRowCount()) {
            formBuilder.nextLine();
        }
        formBuilder.append(controlFactory.getControl(), formBuilder
                .getColumnCount());
    }

}