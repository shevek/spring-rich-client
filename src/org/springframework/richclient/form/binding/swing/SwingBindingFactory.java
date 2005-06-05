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

import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import org.springframework.beans.PropertyComparator;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBindingFactory;
import org.springframework.richclient.forms.BufferedCollectionValueModel;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.springframework.richclient.list.ObservableList;

/**
 * A convenient implementation of <code>BindingFactory</code>. Provides a set
 * of methods that address the typical binding requirements of Swing based 
 * forms.
 * 
 * @author Oliver Hutchison
 */
public class SwingBindingFactory extends AbstractBindingFactory {

    public SwingBindingFactory(ConfigurableFormModel formModel) {
        super(formModel);
    }

    public Binding createBoundTextField(String formProperty) {
        return createBinding(JTextField.class, formProperty);
    }

    public Binding createBoundTextArea(String formProperty) {
        return createBinding(JTextArea.class, formProperty);
    }

    public Binding createBoundTextArea(String formProperty, int rows, int columns) {
        Map context = createContext(TextAreaBinder.ROWS_KEY, new Integer(rows));
        context.put(TextAreaBinder.COLUMNS_KEY, new Integer(columns));
        return createBinding(JTextArea.class, formProperty, context);
    }

    public Binding createBoundFormattedTextField(String formProperty) {
        return createBinding(JFormattedTextField.class, formProperty);
    }

    public Binding createBoundFormattedTextField(String formProperty, AbstractFormatterFactory formatterFactory) {
        Map context = createContext(FormattedTextFieldBinder.FORMATTER_FACTORY_KEY, formatterFactory);
        return createBinding(JFormattedTextField.class, formProperty, context);
    }

    public Binding createBoundSpinner(String formProperty) {
        return createBinding(JSpinner.class, formProperty);
    }

    public Binding createBoundCheckBox(String formProperty) {
        return createBinding(JCheckBox.class, formProperty);
    }

    public Binding createBoundComboBox(String formProperty) {
        return createBinding(JComboBox.class, formProperty);
    }

    public Binding createBoundComboBox(String formProperty, Object[] selectableItems) {
        Map context = createContext(ComboBoxBinder.SELECTABLE_ITEMS_KEY, selectableItems);
        return createBinding(JComboBox.class, formProperty, context);
    }

    public Binding createBoundComboBox(String formProperty, ValueModel selectableItemsHolder) {
        Map context = createContext(ComboBoxBinder.SELECTABLE_ITEMS_HOLDER_KEY, selectableItemsHolder);
        return createBinding(JComboBox.class, formProperty, context);
    }

    public Binding createBoundComboBox(String formProperty, String selectableItemsProperty, String renderedItemProperty) {
        return createBoundComboBox(formProperty, getFormModel().getDisplayValueModel(selectableItemsProperty),
                renderedItemProperty);
    }

    public Binding createBoundComboBox(String formProperty, ValueModel selectableItemsHolder,
            String renderedItemProperty) {
        Map context = createContext(ComboBoxBinder.SELECTABLE_ITEMS_HOLDER_KEY, selectableItemsHolder);
        context.put(ComboBoxBinder.RENDERER_KEY, new BeanPropertyValueListRenderer(renderedItemProperty));
        context.put(ComboBoxBinder.COMPARATOR_KEY, new PropertyComparator(renderedItemProperty));
        return createBinding(JComboBox.class, formProperty, context);
    }

    /**
     * This method will most likely move over to FormModel
     * 
     * @deprecated
     */
    public ObservableList createBoundListModel(String formProperty) {
        final ConfigurableFormModel formModel = ((ConfigurableFormModel)getFormModel());
        ValueModel valueModel = formModel.getValueModel(formProperty );
        if (! (valueModel instanceof BufferedCollectionValueModel)) {            
            valueModel = new BufferedCollectionValueModel(
                    formModel.getPropertyAccessStrategy().getPropertyValueModel(
                            formProperty), formModel.getPropertyAccessStrategy()
                            .getMetadataAccessStrategy()
                            .getPropertyType(formProperty));
            formModel.add(formProperty, valueModel);
        }
        return (ObservableList)valueModel.getValue();
    }

}