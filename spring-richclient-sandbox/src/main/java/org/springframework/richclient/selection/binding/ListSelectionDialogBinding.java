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
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.richclient.selection.binding.support.LabelProvider;
import org.springframework.richclient.selection.binding.support.LabelProviderListCellRenderer;
import org.springframework.richclient.selection.binding.support.SelectField;
import org.springframework.richclient.selection.binding.support.ValueModel2EventListBridge;
import org.springframework.richclient.selection.dialog.FilterListSelectionDialog;
import org.springframework.richclient.selection.dialog.ListSelectionDialog;
import org.springframework.rules.closure.Closure;
import org.springframework.rules.closure.support.Block;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.impl.beans.BeanTextFilterator;
import ca.odell.glazedlists.impl.filter.StringTextFilterator;

/**
 * Binding for selection objects in a form.
 * 
 * @author Peter De Bruycker
 */
public class ListSelectionDialogBinding extends CustomBinding {

    protected SelectField selectField;
    private boolean filtered;
    private String[] filterProperties;
    private ListCellRenderer renderer;
    private ValueModel selectableItemsHolder;
    private LabelProvider labelProvider;
    private Comparator comparator;
    private String descriptionKey;
    private String titleKey;
    private boolean nullable = true;

    protected ListSelectionDialogBinding(SelectField selectField, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, null);
        this.selectField = selectField;
    }

    protected JComponent doBindControl() {
        selectField.setLabelProvider(labelProvider);
        selectField.setSelectionDialog(createSelectionDialog());

        selectField.setNullable(nullable);
        
        // trigger control creation so we can set the value
        selectField.getControl();

        selectField.setValue(getValue());

        selectField.addPropertyChangeListener("value", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                controlValueChanged(selectField.getValue());
            }
        });

        return selectField.getControl();
    }

    protected ApplicationDialog createSelectionDialog() {
        EventList eventList = createEventList(selectableItemsHolder);
        final ValueModel2EventListBridge itemRefresher = new ValueModel2EventListBridge(selectableItemsHolder,
                eventList, true);

        ListSelectionDialog selectionDialog = null;
        if (filtered) {
            FilterListSelectionDialog filterDialog = new FilterListSelectionDialog("", null, new FilterList(eventList));
            if (filterProperties == null) {
                filterDialog.setFilterator(new StringTextFilterator());
            } else {
                filterDialog.setFilterator(new BeanTextFilterator(filterProperties));
            }

            selectionDialog = filterDialog;
        } else {
            selectionDialog = new ListSelectionDialog("", null, eventList);
        }

        selectionDialog.setOnAboutToShow(new Block() {
            protected void handle(Object ignore) {
                itemRefresher.synchronize();
            }
        });

        selectionDialog.setOnSelectAction(new Closure() {
            public Object call(Object argument) {
                controlValueChanged(argument);
                selectField.setValue(argument);

                return argument;
            }
        });
        selectionDialog.setRenderer(getRendererForSelectionDialog());

        if (StringUtils.hasText(descriptionKey)) {
            String description = getMessage(descriptionKey);
            selectionDialog.setDescription(description);
        }
        if (StringUtils.hasText(titleKey)) {
            String title = getMessage(titleKey);
            selectionDialog.setTitle(title);
        }

        return selectionDialog;
    }

    private EventList createEventList(ValueModel selectableItemsHolder) {
        EventList eventList = GlazedLists.eventList(Collections.emptyList());

        if (comparator != null) {
            eventList = new SortedList(eventList, comparator);
        }

        return eventList;
    }

    protected ListCellRenderer getRendererForSelectionDialog() {
        if (renderer != null) {
            return renderer;
        }

        if (labelProvider != null) {
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

    public void setFilterProperties(String[] filterProperties) {
        this.filterProperties = filterProperties;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public void setRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    public void setSelectableItemsHolder(ValueModel selectableItemsHolder) {
        this.selectableItemsHolder = selectableItemsHolder;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
