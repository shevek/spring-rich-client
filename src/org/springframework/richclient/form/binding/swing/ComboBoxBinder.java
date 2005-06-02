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
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class ComboBoxBinder extends AbstractBinder  {
    public static final String SELECTABLE_ITEMS_HOLDER_KEY = "selectableItemsHolder";

    public static final String SELECTABLE_ITEMS_KEY = "selectableItems";

    public static final String COMPARATOR_KEY = "comparator";

    public static final String RENDERER_KEY = "renderer";

    public static final String FILTER_KEY = "filter";
    
    public ComboBoxBinder() {
        super(new String[] {SELECTABLE_ITEMS_HOLDER_KEY,
            SELECTABLE_ITEMS_KEY, COMPARATOR_KEY, RENDERER_KEY, FILTER_KEY});
    }
    
    public ComboBoxBinder(String[] supportedContextKeys) {
        super(supportedContextKeys);        
    }
    
    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JComboBox, formPropertyPath);
        ComboBoxBinding binding = new ComboBoxBinding((JComboBox)control, formModel, formPropertyPath);
        applyContext(binding, context);
        return binding;
    }

    protected void applyContext(ComboBoxBinding binding, Map context) {
        if (context.containsKey(SELECTABLE_ITEMS_HOLDER_KEY)) {
            binding.setSelectableItemsHolder((ValueModel)context.get(SELECTABLE_ITEMS_HOLDER_KEY));
        }
        if (context.containsKey(SELECTABLE_ITEMS_KEY)) {
            binding.setSelectableItemsHolder(new ValueHolder(context.get(SELECTABLE_ITEMS_KEY)));
        }
        if (context.containsKey(RENDERER_KEY)) {
            binding.setRenderer((ListCellRenderer)context.get(RENDERER_KEY));
        }
        if (context.containsKey(COMPARATOR_KEY)) {
            binding.setComparator((Comparator)context.get(COMPARATOR_KEY));
        }
    }

    protected JComponent createControl(Map context) {
        return getComponentFactory().createComboBox();
    }
}
