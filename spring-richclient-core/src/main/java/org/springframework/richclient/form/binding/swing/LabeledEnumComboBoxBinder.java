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

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class LabeledEnumComboBoxBinder extends ComboBoxBinder {

    private LabeledEnumResolver enumResolver;

    public LabeledEnumComboBoxBinder() {
        super(new String[] { COMPARATOR_KEY, RENDERER_KEY, EDITOR_KEY, FILTER_KEY });
    }

    protected AbstractListBinding createListBinding(JComponent control, FormModel formModel, String formPropertyPath) {
        Assert.isInstanceOf(JComboBox.class, control, formPropertyPath);
        return new LabeledEnumComboBoxBinding((JComboBox) control, formModel, formPropertyPath);
    }

    protected void applyContext(AbstractListBinding binding, Map context) {
        super.applyContext(binding, context);
        binding.setSelectableItems(createEnumSelectableItemsHolder(binding.getFormModel(), binding.getProperty()));
    }

    protected Collection createEnumSelectableItemsHolder(FormModel formModel, String formPropertyPath) {
        Collection enumCollection = getLabeledEnumResolver().getLabeledEnumSet(
                getPropertyType(formModel, formPropertyPath));
        Assert.notNull(enumCollection, "Unable to resolve enums for class '"
                + getPropertyType(formModel, formPropertyPath).getName() + "'.");
        return enumCollection;
    }

    public LabeledEnumResolver getLabeledEnumResolver() {
        if (enumResolver == null) {
            enumResolver = (LabeledEnumResolver) ApplicationServicesLocator.services().getService(
                    LabeledEnumResolver.class);
        }
        return enumResolver;
    }

    public void setEnumResolver(LabeledEnumResolver enumResolver) {
        this.enumResolver = enumResolver;
    }
}