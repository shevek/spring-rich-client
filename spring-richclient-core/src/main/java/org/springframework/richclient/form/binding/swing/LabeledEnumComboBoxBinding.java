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
import javax.swing.ListModel;

import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.core.enums.LabeledEnum;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.list.LabeledEnumComboBoxEditor;
import org.springframework.richclient.list.LabeledEnumListRenderer;
import org.springframework.util.comparator.ComparableComparator;
import org.springframework.util.comparator.CompoundComparator;

/**
 * @author Oliver Hutchison
 */
public class LabeledEnumComboBoxBinding extends ComboBoxBinding {

    private MessageSource messageSource;

    public LabeledEnumComboBoxBinding(JComboBox comboBox, FormModel formModel, String formPropertyPath) {
        super(comboBox, formModel, formPropertyPath);
    }

    protected void doBindControl(ListModel bindingModel) {
        setRenderer(new LabeledEnumListRenderer(getMessageSource()));
        setEditor(new LabeledEnumComboBoxEditor(getMessageSource(), getEditor()));
        CompoundComparator comparator = new CompoundComparator();
        comparator.addComparator(LabeledEnum.LABEL_ORDER);
        comparator.addComparator(new ComparableComparator());
        setComparator(comparator);
        super.doBindControl(bindingModel);
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected MessageSource getMessageSource() {
        if (messageSource == null) {
            messageSource = (MessageSource) ApplicationServicesLocator.services().getService(MessageSource.class);
        }
        return messageSource;
    }
}