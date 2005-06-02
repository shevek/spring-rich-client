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

import java.util.Collection;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.core.enums.support.StaticLabeledEnumResolver;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class EnumComboBoxBinder extends ComboBoxBinder {

    private final LabeledEnumResolver enumResolver = StaticLabeledEnumResolver.instance();

    public EnumComboBoxBinder() {
        super(new String[] {COMPARATOR_KEY, RENDERER_KEY, FILTER_KEY});
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        ComboBoxBinding binding = (ComboBoxBinding)super.doBind(control, formModel, formPropertyPath, context);
        binding.setSelectableItemsHolder(createEnumSelectableItemsHolder(formModel, formPropertyPath));
        return binding;
    }

    private ValueModel createEnumSelectableItemsHolder(FormModel formModel, String formPropertyPath) {
        Collection enumCollection = enumResolver.getLabeledEnumCollection(getPropertyType(formModel, formPropertyPath));
        Assert.notNull(enumCollection, "Unable to resolve enums for class '"
                + getPropertyType(formModel, formPropertyPath).getName() + "'.");
        return new ValueHolder(enumCollection);
    }
}