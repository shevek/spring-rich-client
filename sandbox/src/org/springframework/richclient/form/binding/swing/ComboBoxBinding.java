/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.form.binding.swing;

import java.util.Comparator;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinding;
import org.springframework.richclient.list.DynamicComboBoxListModel;

/**
 * @author Oliver Hutchison
 */
public class ComboBoxBinding extends AbstractBinding implements Binding {
    public static final String SELECTABLE_ITEMS_HOLDER_KEY = "selectableItemsHolder";
    
    public static final String SELECTABLE_ITEMS_KEY = "selectableItems";
    
    public static final String COMPARATOR_KEY = "comparator";
    
    public static final String RENDERER_KEY = "renderer";
    
    private final Map context;
    
    private JComboBox comboBox;    
    
    public ComboBoxBinding(FormModel formModel, String formPropertyPath, Map context) {
        this(null, formModel, formPropertyPath, context);
    }
    
    public ComboBoxBinding(JComboBox comboBox, FormModel formModel, String formPropertyPath, Map context) {
        super(formModel, formPropertyPath);
        this.comboBox = comboBox;
        this.context = context;
    }
   
    protected JComponent doCreateAndBindControl() {
        if (comboBox == null) {
            comboBox = createComboBox();
        }
        final ValueModel valueModel = getDisplayValueModel();
        DynamicComboBoxListModel model = new DynamicComboBoxListModel(valueModel, getSelectableItemsHolder()); 
        model.setComparator(getComparator());
        model.sort();
        comboBox.setModel(model);
        comboBox.setRenderer(getRenderer());
        return comboBox;
    }

    protected ValueModel getSelectableItemsHolder() {
        if (context.containsKey(SELECTABLE_ITEMS_HOLDER_KEY)) {
            return (ValueModel) context.get(SELECTABLE_ITEMS_HOLDER_KEY);
        } else if (context.containsKey(SELECTABLE_ITEMS_KEY)) {
            return new ValueHolder(context.get(SELECTABLE_ITEMS_KEY));
        } else { 
            // copy the existing Model
            Object[] items = new Object[comboBox.getModel().getSize()];
            for (int i = 0; i < comboBox.getModel().getSize(); i++) {
                items[i] = comboBox.getModel().getElementAt(i);
            }
            return new ValueHolder(items);
        }
    }
    
    private Comparator getComparator() {
        if (context.containsKey(COMPARATOR_KEY)) {
            return (Comparator) context.get(COMPARATOR_KEY);
        } else { 
            return null;
        }
    }
    
    protected ListCellRenderer getRenderer() {
        if (context.containsKey(RENDERER_KEY)) {
            return (ListCellRenderer) context.get(RENDERER_KEY);
        } else { 
            return comboBox.getRenderer();
        }
    }

    protected JComboBox createComboBox() {        
        return getComponentFactory().createComboBox();
    }
    
    protected void readOnlyChanged() {
        comboBox.setEnabled(isEnabled() && ! isReadOnly());    
    }

    protected void enabledChanged() {
        comboBox.setEnabled(isEnabled() && ! isReadOnly());        
    }
}