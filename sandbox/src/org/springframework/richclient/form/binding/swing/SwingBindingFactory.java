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
import javax.swing.JTextField;

import org.springframework.beans.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.BasicBindingFactory;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;

/**
 * @author Oliver Hutchison
 */
public class SwingBindingFactory extends BasicBindingFactory {

    public SwingBindingFactory(BinderSelectionStrategy binderSelectionStrategy, FormModel formModel) {
        super(binderSelectionStrategy, formModel);
    }

    public Binding createBoundTextField(String formProperty) {
        return createBinding(JTextField.class, formProperty);
    }

    public Binding createBoundCheckBox(String formProperty) {
        return createBinding(JCheckBox.class, formProperty);
    }

    public Binding createBoundComboBox(String formProperty) {
        return createBinding(JComboBox.class, formProperty);
    }

    public Binding createBoundComboBox(String formProperty, Object[] selectableItems) {
        Map context = createContext(ComboBoxBinding.SELECTABLE_ITEMS_KEY, selectableItems);
        return createBinding(JComboBox.class, formProperty, context);
    }

    public Binding createBoundComboBox(String formProperty, String selectableItemsProperty,
            String renderedItemProperty) {
        Map context = createContext(ComboBoxBinding.SELECTABLE_ITEMS_KEY, getFormModel().getDisplayValueModel(
                selectableItemsProperty));
        context.put(ComboBoxBinding.RENDERER_KEY, new BeanPropertyValueListRenderer(renderedItemProperty));
        context.put(ComboBoxBinding.COMPARATOR_KEY, new PropertyComparator(renderedItemProperty));
        return createBinding(JComboBox.class, formProperty, context);
    }
}
