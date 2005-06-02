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
 * TODO: support for filters
 * 
 * @author Oliver Hutchison
 */
public class ComboBoxBinding extends AbstractBinding implements Binding {

    private final JComboBox comboBox;

    private ValueModel selectableItemsHolder;

    private ListCellRenderer renderer;

    private Comparator comparator;

    public ComboBoxBinding(FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath);
        this.comboBox = createComboBox();
    }

    public ComboBoxBinding(JComboBox comboBox, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath);
        this.comboBox = comboBox;
    }

    protected JComponent doBindControl() {
        final ValueModel valueModel = getDisplayValueModel();
        DynamicComboBoxListModel model = new DynamicComboBoxListModel(valueModel, getSelectableItemsHolder());
        model.setComparator(getComparator());
        model.sort();
        comboBox.setModel(model);
        return comboBox;
    }

    public void setSelectableItemsHolder(ValueModel selectableItemsHolder) {
        this.selectableItemsHolder = selectableItemsHolder;
    }

    public ValueModel getSelectableItemsHolder() {
        if (selectableItemsHolder != null) {
            return selectableItemsHolder;
        }
        else {
            // copy the existing Model
            Object[] items = new Object[comboBox.getModel().getSize()];
            for (int i = 0; i < comboBox.getModel().getSize(); i++) {
                items[i] = comboBox.getModel().getElementAt(i);
            }
            return new ValueHolder(items);
        }
    }

    private Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public ListCellRenderer getRenderer() {
        return comboBox.getRenderer();
    }

    public void setRenderer(ListCellRenderer renderer) {
        comboBox.setRenderer(renderer);
    }

    protected JComboBox createComboBox() {
        return getComponentFactory().createComboBox();
    }

    protected void readOnlyChanged() {
        comboBox.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void enabledChanged() {
        comboBox.setEnabled(isEnabled() && !isReadOnly());
    }

}