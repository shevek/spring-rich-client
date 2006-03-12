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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class ListBinder extends AbstractBinder {
    public static final String SELECTABLE_ITEMS_HOLDER_KEY = "selectableItemsHolder";

    public static final String SELECTED_ITEM_HOLDER_KEY = "selectedItemHolder";

    public static final String SELECTED_ITEM_TYPE_KEY = "selectedItemType";

    public static final String MODEL_KEY = "model";

    public static final String COMPARATOR_KEY = "comparator";

    public static final String RENDERER_KEY = "renderer";

    public static final String FILTER_KEY = "filter";

    public static final String SELECTION_MODE_KEY = "selectionMode";

    public ListBinder() {
        super(null, new String[] {SELECTABLE_ITEMS_HOLDER_KEY, SELECTED_ITEM_HOLDER_KEY, SELECTED_ITEM_TYPE_KEY, MODEL_KEY, COMPARATOR_KEY,
                RENDERER_KEY, FILTER_KEY, SELECTION_MODE_KEY});
    }

    public ListBinder(String[] supportedContextKeys) {
        super(null, supportedContextKeys);
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JList, formPropertyPath);
        ListBinding binding = new ListBinding((JList)control, formModel, formPropertyPath);
        applyContext(binding, context);
        return binding;
    }

    protected void applyContext(ListBinding binding, Map context) {
        // Validate that we have enough context to function
        boolean haveModel = context.containsKey(MODEL_KEY);
        boolean haveSelectableItems = context.containsKey(SELECTABLE_ITEMS_HOLDER_KEY);
        boolean haveSelectedItem = context.containsKey(SELECTED_ITEM_HOLDER_KEY);

        if( !(haveModel || haveSelectableItems) ) {
            throw new IllegalArgumentException("Context must contain value for one of: "
                + MODEL_KEY + " or " + SELECTABLE_ITEMS_HOLDER_KEY );
        }

        if( !haveSelectedItem ) {
            throw new IllegalArgumentException("Context must contain a value for: " + SELECTED_ITEM_HOLDER_KEY);
        }

        if (haveModel) {
            binding.setModel((ListModel)context.get(MODEL_KEY));
        }
        if (haveSelectableItems) {
            binding.setSelectableItemsHolder((ValueModel)context.get(SELECTABLE_ITEMS_HOLDER_KEY));
        }
        if (haveSelectedItem) {
            binding.setSelectedItemHolder((ValueModel)context.get(SELECTED_ITEM_HOLDER_KEY));
        }
        if (context.containsKey(RENDERER_KEY)) {
            binding.setRenderer((ListCellRenderer)context.get(RENDERER_KEY));
        }
        if (context.containsKey(COMPARATOR_KEY)) {
            binding.setComparator((Comparator)context.get(COMPARATOR_KEY));
        }
        if (context.containsKey(SELECTED_ITEM_TYPE_KEY)) {
            binding.setSelectedItemType((Class)context.get(SELECTED_ITEM_TYPE_KEY));
        }
        if (context.containsKey(SELECTION_MODE_KEY)) {
            if (context.get(SELECTION_MODE_KEY) instanceof Integer) {
                binding.setSelectionMode((Integer)context.get(SELECTION_MODE_KEY));
            }
            else {
                try {
                    binding.setSelectionMode((Integer)ListSelectionModel.class.getField(
                            (String)context.get(SELECTION_MODE_KEY)).get(null));
                }
                catch (IllegalAccessException e) {
                    final IllegalArgumentException iae = new IllegalArgumentException(
                            "Unable to access selection mode field in ListSelectionModel");
                    iae.initCause(e);
                    throw iae;
                }
                catch (NoSuchFieldException e) {
                    final IllegalArgumentException iae = new IllegalArgumentException("Unknown selection mode '"
                            + context.get(SELECTION_MODE_KEY) + "'");
                    iae.initCause(e);
                    throw iae;
                }
            }
        }
    }

    protected JComponent createControl(Map context) {
        JList list = getComponentFactory().createList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }
}