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

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.MessageSource;
import org.springframework.core.enums.AbstractLabeledEnum;
import org.springframework.core.enums.LabeledEnumResolver;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.list.LabeledEnumListRenderer;
import org.springframework.util.comparator.ComparableComparator;
import org.springframework.util.comparator.CompoundComparator;

/**
 * @author Oliver Hutchison
 */
public class EnumComboBoxBinding extends ComboBoxBinding {

    private LabeledEnumResolver enumResolver;

    private MessageSource messageSource;

    public EnumComboBoxBinding(FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath);
    }

    public EnumComboBoxBinding(JComboBox comboBox, FormModel formModel, String formPropertyPath) {
        super(comboBox, formModel, formPropertyPath);
    }

    protected JComponent doBindControl() {
        setSelectableItemsHolder(new ValueHolder(getEnumResolver().getLabeledEnumSet(getPropertyType())));
        setRenderer(new LabeledEnumListRenderer(getMessageSource()));
        CompoundComparator comparator = new CompoundComparator();
        comparator.addComparator(AbstractLabeledEnum.LABEL_ORDER);
        comparator.addComparator(new ComparableComparator());
        setComparator(comparator);
        return super.doBindControl();
    }

    public void setEnumResolver(LabeledEnumResolver enumResolver) {
        this.enumResolver = enumResolver;
    }

    protected LabeledEnumResolver getEnumResolver() {
        if (enumResolver == null) {
            enumResolver = Application.services().getLabeledEnumResolver();
        }
        return enumResolver;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected MessageSource getMessageSource() {
        if (messageSource == null) {
            messageSource = Application.services();
        }
        return messageSource;
    }
}