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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.core.closure.Constraint;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.support.ScrollPaneDecoratedBinding;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.util.Assert;

/**
 * @author oliverh
 */
public class TableFormBuilder extends AbstractFormBuilder {

    private TableLayoutBuilder builder;

    private String labelAttributes = TableLayoutBuilder.DEFAULT_LABEL_ATTRIBUTES;

    public TableFormBuilder(BindingFactory bindingFactory) {
        super(bindingFactory);
        this.builder = new TableLayoutBuilder(new JPanel());
    }

    public void row() {
        builder.relatedGapRow();
    }

    public JComponent[] add(String propertyName) {
        return add(propertyName, "");
    }

    public JComponent[] add(Binding binding) {
        return add(binding, "");
    }

    public JComponent[] add(String propertyName, String attributes) {
        return addBinding(getDefaultBinding(propertyName), attributes, getLabelAttributes());
    }

    public JComponent[] add(Binding binding, String attributes) {
        Assert.isTrue(getFormModel() == binding.getFormModel(),
                "Binding's form model must match FormBuilder's form model");
        return addBinding(binding, attributes, getLabelAttributes());
    }

    public JComponent[] add(String propertyName, JComponent component) {
        return add(propertyName, component, "");
    }

    public JComponent[] add(String propertyName, JComponent component, String attributes) {
        return addBinding(getBinding(propertyName, component), attributes, getLabelAttributes());
    }

    public JComponent[] addSelector(String propertyName, Constraint filter) {
        return addSelector(propertyName, filter, "");
    }

    public JComponent[] addSelector(String propertyName, Constraint filter, String attributes) {
        return addBinding(getBinding(propertyName, getSelector(propertyName, filter)), attributes, getLabelAttributes());
    }

    public JComponent[] addPasswordField(String propertyName) {
        return addPasswordField(propertyName, "");
    }

    public JComponent[] addPasswordField(String propertyName, String attributes) {
        return addBinding(getBinding(propertyName, getPasswordField(propertyName)), attributes, getLabelAttributes());
    }

    public JComponent[] addTextArea(String propertyName) {
        return addTextArea(propertyName, "");
    }

    public JComponent[] addTextArea(String propertyName, String attributes) {
        JComponent textArea = getTextArea(propertyName);
        return addBinding(getBinding(propertyName, textArea), new JScrollPane(textArea), attributes, getLabelAttributes()
                + " valign=top");
    }
  
    public JComponent[] addInScrollPane(String propertyName) {
        return addInScrollPane(propertyName, "");
    }
  
    public JComponent[] addInScrollPane(String propertyName, String attributes) {
        return addInScrollPane(getDefaultBinding(propertyName), attributes);
    }
  
    public JComponent[] addInScrollPane(Binding binding) {
        return addInScrollPane(binding, "");
    }
  
    public JComponent[] addInScrollPane(Binding binding, String attributes) {
        return add(new ScrollPaneDecoratedBinding(binding), attributes);
    }

    public void addSeparator(String text) {
        builder.separator(text);
    }

    public void addSeparator(String text, String attributes) {
        builder.separator(text, attributes);
    }

    public TableLayoutBuilder getLayoutBuilder() {
        return builder;
    }

    public JComponent getForm() {
        getBindingFactory().getFormModel().revert();
        return builder.getPanel();
    }

    protected String getLabelAttributes() {
        return labelAttributes;
    }

    public void setLabelAttributes(String labelAttributes) {
        this.labelAttributes = labelAttributes;
    }

    private JComponent[] addBinding(Binding binding, String attributes, String labelAttributes) {
        return addBinding(binding, binding.getControl(), attributes, labelAttributes);
    }

    private JComponent[] addBinding(Binding binding, JComponent wrappedControl, String attributes, String labelAttributes) {
        final JLabel label = getLabelFor(binding.getProperty(), binding.getControl());
        if (!builder.hasGapToLeft()) {
            builder.gapCol();
        }
        builder.cell(label, labelAttributes);
        builder.labelGapCol();
        builder.cell(wrappedControl, attributes);
        return new JComponent[] {label, wrappedControl};
    }
}