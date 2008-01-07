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

import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.selection.binding.support.LabelProvider;
import org.springframework.richclient.selection.binding.support.SelectField;
import org.springframework.richclient.selection.binding.support.SimpleSelectField;
import org.springframework.util.Assert;

/**
 * Binder for <code>SelectField</code>.
 * <p>
 * The context can contain the following items:
 * <ul>
 * <li><code>SELECTABLE_ITEMS_KEY</code>: the collection of selectable items</li>
 * <li><code>LABEL_PROVIDER_KEY</code>: the label provider to provide the text of a given item</li>
 * <li><code>FILTERED_KEY</code>: boolean value indicating whether the selection dialog will contain a filter field.
 * If this value is <code>true</code> and the filter properties are not given, the string representation of the item
 * will be used as filter value.</li>
 * <li><code>FILTER_PROPERTIES_KEY</code>: array of properties that will be used for filtering</li>
 * <li><code>RENDERER_KEY</code>: custom ListCellRenderer that will be used in the selection dialog</li>
 * </ul>
 * 
 * @author Peter De Bruycker
 */
public class ListSelectionDialogBinder extends AbstractBinder {

    public static final String SELECTABLE_ITEMS_KEY = "selectableItems";

    public static final String LABEL_PROVIDER_KEY = "labelProvider";

    public static final String FILTER_PROPERTIES_KEY = "filterProperties";

    public static final String FILTERED_KEY = "filtered";

    public static final String RENDERER_KEY = "renderer";

    private LabelProvider labelProvider;
    private List selectableItems;

    private String[] filterProperties;
    private boolean filtered;
    
    private ListCellRenderer renderer;

    protected ListSelectionDialogBinder() {
        super(null, new String[] { SELECTABLE_ITEMS_KEY, LABEL_PROVIDER_KEY, FILTER_PROPERTIES_KEY, FILTERED_KEY, RENDERER_KEY });
    }

    protected JComponent createControl(Map context) {
        return new SimpleSelectField();
    }

    protected void applyContext(ListSelectionDialogBinding binding, Map context) {
        if (context.containsKey(LABEL_PROVIDER_KEY)) {
            binding.setLabelProvider((LabelProvider) context.get(LABEL_PROVIDER_KEY));
        }
        else if (labelProvider != null) {
            binding.setLabelProvider(labelProvider);
        }

        if (context.containsKey(SELECTABLE_ITEMS_KEY)) {
            binding.setSelectableItems((List) context.get(SELECTABLE_ITEMS_KEY));
        }
        else if (selectableItems != null) {
            binding.setSelectableItems(selectableItems);
        }

        if (context.containsKey(FILTER_PROPERTIES_KEY)) {
            binding.setFilterProperties((String[]) context.get(FILTER_PROPERTIES_KEY));
        }
        else if (filterProperties != null) {
            binding.setFilterProperties(filterProperties);
        }

        if (context.containsKey(FILTERED_KEY)) {
            binding.setFiltered(((Boolean) context.get(FILTERED_KEY)).booleanValue());
        }
        else if (filterProperties != null) {
            binding.setFiltered(filtered);
        }

        if (context.containsKey(RENDERER_KEY)) {
            binding.setRenderer((ListCellRenderer) context.get(RENDERER_KEY));
        }
        else if (renderer != null) {
            binding.setRenderer(renderer);
        }
}

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isInstanceOf(SelectField.class, control);

        ListSelectionDialogBinding binding = new ListSelectionDialogBinding((SelectField) control, formModel, formPropertyPath);
        applyContext(binding, context);

        return binding;
    }

    public void setLabelProvider(LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
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
