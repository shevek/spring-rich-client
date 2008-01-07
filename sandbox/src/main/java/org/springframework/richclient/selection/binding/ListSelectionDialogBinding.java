/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.selection.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.springframework.binding.form.FormModel;
import org.springframework.core.closure.Closure;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.richclient.selection.binding.support.LabelProvider;
import org.springframework.richclient.selection.binding.support.LabelProviderListCellRenderer;
import org.springframework.richclient.selection.binding.support.SelectField;
import org.springframework.richclient.selection.dialog.FilterListSelectionDialog;
import org.springframework.richclient.selection.dialog.ListSelectionDialog;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.impl.beans.BeanTextFilterator;
import ca.odell.glazedlists.impl.filter.StringTextFilterator;

/**
 * Binding for selection objects in a form.
 * 
 * @author Peter De Bruycker
 */
public class ListSelectionDialogBinding extends CustomBinding {

    private LabelProvider labelProvider;

    private SelectField selectField;

    private List selectableItems;

    private boolean filtered;

    private String[] filterProperties;

    private ListCellRenderer renderer;

    protected ListSelectionDialogBinding(SelectField selectField, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.selectField = selectField;
    }

    protected JComponent doBindControl() {
        selectField.setLabelProvider(labelProvider);
        selectField.setSelectionDialog(createSelectionDialog());
        selectField.setValue(getValue());

        selectField.addPropertyChangeListener("value", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                controlValueChanged(selectField.getValue());
            }
        });

        return selectField;
    }

    protected ListSelectionDialog createSelectionDialog() {
        Closure onSelectAction = new Closure() {
            public Object call(Object argument) {
                controlValueChanged(argument);
                selectField.setValue(argument);

                return argument;
            }
        };

        ListSelectionDialog selectionDialog = null;
        if (filtered) {
            FilterListSelectionDialog filterDialog = new FilterListSelectionDialog("", null, new FilterList(GlazedLists
                    .eventList(selectableItems)));
            if (filterProperties == null) {
                filterDialog.setFilterator(new StringTextFilterator());
            }
            else {
                filterDialog.setFilterator(new BeanTextFilterator(filterProperties));
            }

            selectionDialog = filterDialog;
        }
        else {
            selectionDialog = new ListSelectionDialog("", null, GlazedLists.eventList(selectableItems));
        }

        selectionDialog.setOnSelectAction(onSelectAction);

        selectionDialog.setRenderer(getRendererForSelectionDialog());

        return selectionDialog;
    }
    
    protected ListCellRenderer getRendererForSelectionDialog() {
        if(renderer != null) {
            return renderer;
        }
        
        if(labelProvider != null) {
            return new LabelProviderListCellRenderer(labelProvider);
        }
        
        return null;
    }

    protected void enabledChanged() {
        selectField.setEnabled(isEnabled());
    }

    protected void readOnlyChanged() {
        selectField.setEditable(!isReadOnly());
    }

    public void setLabelProvider(LabelProvider provider) {
        this.labelProvider = provider;
    }

    protected void valueModelChanged(Object newValue) {
        selectField.setValue(newValue);
    }

    public void setSelectableItems(List selectableItems) {
        this.selectableItems = selectableItems;
    }

    public void setFilterProperties(String[] filterProperties) {
        this.filterProperties = filterProperties;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public void setRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }
}
