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
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.support.AbstractBinding;
import org.springframework.richclient.list.DynamicListModel;
import org.springframework.richclient.list.ListListModel;

public class ListBinding extends AbstractBinding {

    private final JList list;

    private ListModel model;

    private ValueModel selectedItemHolder;

    private ValueModel selectableItemsHolder;

    private ListCellRenderer renderer;

    private Comparator comparator;

    public ListBinding(JList list, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.list = list;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public void setModel(ListModel model) {
        this.model = model;
    }

    public void setRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    public void setSelectableItemsHolder(ValueModel selectableItemsHolder) {
        this.selectableItemsHolder = selectableItemsHolder;
    }

    public void setSelectedItemHolder(ValueModel selectedItemHolder) {
        this.selectedItemHolder = selectedItemHolder;
    }

    protected JComponent doBindControl() {
        list.setModel(createModel());
        list.setCellRenderer(renderer);
        if (selectedItemHolder != null) {
            list.setSelectedValue(selectedItemHolder.getValue(), true);
            list.addListSelectionListener(new ListSelectedValueMediator());
        }
        return list;
    }

    private ListModel createModel() {
        if (model != null) {
            return model;
        }
        else {
            ListListModel model;
            if (selectableItemsHolder != null) {
                model = new DynamicListModel(selectableItemsHolder);
            }
            else {
                model = new ListListModel();
            }
            model.setComparator(comparator);
            return model;
        }
    }

    protected void readOnlyChanged() {
        list.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void enabledChanged() {
        list.setEnabled(isEnabled() && !isReadOnly());
    }

    private class ListSelectedValueMediator implements ListSelectionListener {

        private final PropertyChangeListener valueChangeHander;

        public ListSelectedValueMediator() {
            valueChangeHander = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (selectedItemHolder.getValue() != null) {
                        list.setSelectedValue(selectedItemHolder.getValue(), true);
                    }
                    else {
                        list.clearSelection();
                    }
                }
            };
            selectedItemHolder.addValueChangeListener(valueChangeHander);
        }

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                selectedItemHolder.setValueSilently(list.getSelectedValue(), valueChangeHander);
            }
        }
    }
}