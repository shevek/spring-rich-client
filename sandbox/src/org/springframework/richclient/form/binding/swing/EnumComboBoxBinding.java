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

import java.util.Map;

import javax.swing.JComboBox;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;

/**
 * @author Oliver Hutchison
 */
public class EnumComboBoxBinding extends ComboBoxBinding implements Binding {

    public EnumComboBoxBinding(FormModel formModel, String formPropertyPath, Map context) {
        super(formModel, formPropertyPath, context);
    }
    
    public EnumComboBoxBinding(JComboBox comboBox, FormModel formModel, String formPropertyPath, Map context) {
        super(comboBox, formModel, formPropertyPath, context);
        configureForEnum(comboBox);
    }

    protected JComboBox createComboBox() {      
        JComboBox comboBox = getComponentFactory().createComboBox();
        configureForEnum(comboBox);
        return comboBox;
    }

    private void configureForEnum(JComboBox comboBox) {
        getComponentFactory().configureForEnum(comboBox, getPropertyType());
    }
}
