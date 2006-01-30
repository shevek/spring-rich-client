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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ComboBoxEditor;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.richclient.form.builder.FormComponentInterceptor;

/**
 * TODO: support for filters
 * 
 * @author Oliver Hutchison
 */
public class ComboBoxBinding extends CustomBinding {

    private final JComboBox comboBox;

    private final BoundComboBoxModel model = new BoundComboBoxModel();

    private final SelectableItemsChangeHandler selectableItemsChangeHandler = new SelectableItemsChangeHandler();

    private ValueModel selectableItemsHolder;

    public ComboBoxBinding(FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.comboBox = createComboBox();
    }

    public ComboBoxBinding(JComboBox comboBox, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.comboBox = comboBox;
    }

    protected JComponent doBindControl() {
        comboBox.setModel(model);
        comboBox.setSelectedItem(getValueModel().getValue());
        return comboBox;
    }

    protected Collection getSelectableItems() {
        final Object selectableItems = getSelectableItemsHolder().getValue();
        if (selectableItems instanceof Object[]) {
            return Arrays.asList((Object[])selectableItems);
        }
        else if (selectableItems instanceof Collection) {
            return (Collection)selectableItems;
        }
        else {
            throw new UnsupportedOperationException("selectableItemsHolder must contain an array or a Collection");
        }
    }

    protected void updateSelectableItems() {
        model.replaceWith(getSelectableItems());
        model.sort();
    }

    public void setSelectableItemsHolder(ValueModel selectableItemsHolder) {
        if (this.selectableItemsHolder != null) {
            this.selectableItemsHolder.removeValueChangeListener(selectableItemsChangeHandler);
        }
        this.selectableItemsHolder = selectableItemsHolder;
        selectableItemsHolder.addValueChangeListener(selectableItemsChangeHandler);
        updateSelectableItems();
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

    public void setComparator(Comparator comparator) {
        model.setComparator(comparator);
    }

    public ListCellRenderer getRenderer() {
        return comboBox.getRenderer();
    }

    public void setRenderer(ListCellRenderer renderer) {
        comboBox.setRenderer(renderer);
    }

    public void setEditor(ComboBoxEditor comboBoxEditor) {
        comboBox.setEditor(comboBoxEditor);
    }

    public ComboBoxEditor getEditor() {
        return comboBox.getEditor();
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

    private class BoundComboBoxModel extends ListListModel implements ComboBoxModel {

        public void setSelectedItem(Object selectedItem) {
            getValueModel().setValue(selectedItem);
        }

        public Object getSelectedItem() {
            return getValueModel().getValue();
        }

        protected void selectedValueChanged() {
            fireContentsChanged(-1, -1);
        }
    }

    protected void valueModelChanged(Object newValue) {
        model.selectedValueChanged();
    }

    private class SelectableItemsChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            updateSelectableItems();
        }
    }
}