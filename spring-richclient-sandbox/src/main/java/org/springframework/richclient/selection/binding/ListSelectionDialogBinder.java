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

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.selection.binding.support.LabelProvider;
import org.springframework.richclient.selection.binding.support.SimpleSelectField;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.util.Comparator;
import java.util.Map;

/**
 * Binder for <code>SelectField</code>.
 * <p>
 * The context can contain the following items:
 * <ul>
 * <li><code>SELECTABLE_ITEMS_HOLDER_KEY</code>: The ValueModel holding the collection of selectable items</li>
 * <li><code>LABEL_PROVIDER_KEY</code>: the label provider to provide the text of a given item</li>
 * <li><code>FILTERED_KEY</code>: boolean value indicating whether the selection dialog will contain a filter field. If
 * this value is <code>true</code> and the filter properties are not given, the string representation of the item will
 * be used as filter value.</li>
 * <li><code>FILTER_PROPERTIES_KEY</code>: array of properties that will be used for filtering</li>
 * <li><code>RENDERER_KEY</code>: custom ListCellRenderer that will be used in the selection dialog</li>
 * </ul>
 * 
 * @author Peter De Bruycker
 */
public class ListSelectionDialogBinder extends AbstractBinder {

    public static final String SELECTABLE_ITEMS_HOLDER_KEY = "selectableItemsHolder";

    public static final String FILTER_PROPERTIES_KEY = "filterProperties";

    public static final String FILTERED_KEY = "filtered";

    public static final String RENDERER_KEY = "renderer";

    public static final String COMPARATOR_KEY = "comparator";

    public static final String LABEL_PROVIDER_KEY = "lavelProvider";

    public static final String DESCRIPTION_KEY_KEY = "descriptionKey";

    public static final String TITLE_KEY_KEY = "titleKey";

    public static final String NULLABLE_KEY = "nullable";

    private ValueModel selectableItemsHolder;

    private String[] filterProperties;
    private boolean filtered;
    private LabelProvider labelProvider;
    private ListCellRenderer renderer;
    private Comparator comparator;
    private String descriptionKey;
    private String titleKey;
    private boolean nullable = true;

    public ListSelectionDialogBinder() {
        super(null, new String[] { SELECTABLE_ITEMS_HOLDER_KEY, FILTER_PROPERTIES_KEY, FILTERED_KEY, RENDERER_KEY,
                LABEL_PROVIDER_KEY, COMPARATOR_KEY, DESCRIPTION_KEY_KEY, TITLE_KEY_KEY, NULLABLE_KEY });
    }

    protected JComponent createControl(Map context) {
        // not used
        return new JLabel("dummy");
    }

    protected void applyContext(ListSelectionDialogBinding binding, Map context) {
        if (context.containsKey(SELECTABLE_ITEMS_HOLDER_KEY)) {
            binding.setSelectableItemsHolder((ValueModel) context.get(SELECTABLE_ITEMS_HOLDER_KEY));
        } else if (selectableItemsHolder != null) {
            binding.setSelectableItemsHolder(selectableItemsHolder);
        }

        if (context.containsKey(FILTER_PROPERTIES_KEY)) {
            binding.setFilterProperties((String[]) context.get(FILTER_PROPERTIES_KEY));
        } else if (filterProperties != null) {
            binding.setFilterProperties(filterProperties);
        }

        if (context.containsKey(FILTERED_KEY)) {
            binding.setFiltered(((Boolean) context.get(FILTERED_KEY)).booleanValue());
        } else if (filterProperties != null) {
            binding.setFiltered(filtered);
        }

        if (context.containsKey(RENDERER_KEY)) {
            binding.setRenderer((ListCellRenderer) context.get(RENDERER_KEY));
        } else if (renderer != null) {
            binding.setRenderer(renderer);
        }

        if (context.containsKey(COMPARATOR_KEY)) {
            binding.setComparator((Comparator) context.get(COMPARATOR_KEY));
        } else if (comparator != null) {
            binding.setComparator(comparator);
        }

        if (context.containsKey(LABEL_PROVIDER_KEY)) {
            binding.setLabelProvider((LabelProvider) context.get(LABEL_PROVIDER_KEY));
        } else if (labelProvider != null) {
            binding.setLabelProvider(labelProvider);
        }

        if (context.containsKey(DESCRIPTION_KEY_KEY)) {
            binding.setDescriptionKey((String) context.get(DESCRIPTION_KEY_KEY));
        } else if (descriptionKey != null) {
            binding.setDescriptionKey(descriptionKey);
        }

        if (context.containsKey(TITLE_KEY_KEY)) {
            binding.setTitleKey((String) context.get(TITLE_KEY_KEY));
        } else if (titleKey != null) {
            binding.setTitleKey(titleKey);
        }

        if (context.containsKey(NULLABLE_KEY)) {
            binding.setNullable(((Boolean) context.get(NULLABLE_KEY)).booleanValue());
        } else {
            binding.setNullable(nullable);
        }
    }

    protected Binding doBind(JComponent notUsed, FormModel formModel, String formPropertyPath, Map context) {
        ListSelectionDialogBinding binding = new ListSelectionDialogBinding(new SimpleSelectField(), formModel,
                formPropertyPath);
        applyContext(binding, context);

        return binding;
    }

    public void setSelectableItemsHolder(ValueModel selectableItemsHolder) {
        this.selectableItemsHolder = selectableItemsHolder;
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

    public void setLabelProvider(LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
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

    /**
     * May return null if no value is set!
     * 
     * @return whether the field shall be nullable
     */
    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
}
