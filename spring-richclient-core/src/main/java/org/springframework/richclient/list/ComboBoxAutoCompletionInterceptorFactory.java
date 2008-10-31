/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.list;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor;

/**
 * @author Peter De Bruycker
 */
public class ComboBoxAutoCompletionInterceptorFactory implements FormComponentInterceptorFactory {

    public class ComboBoxAutoCompletionInterceptor extends AbstractFormComponentInterceptor {

        /**
         * Constructs a new <code>AutoCompletionInterceptor</code> instance.
         * 
         * @param formModel
         *            the formModel
         */
        public ComboBoxAutoCompletionInterceptor(FormModel formModel) {
            super(formModel);
        }

        /**
         * @see org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor#processComponent(java.lang.String,
         *      javax.swing.JComponent)
         */
        public void processComponent(String propertyName, JComponent component) {
            JComponent inner = getInnerComponent(component);
            if (inner instanceof JComboBox ) {
                JComboBox comboBox = (JComboBox) inner;
                if( comboBox.isEditable()) {
                    // It's editable, so install autocompletion for editable comboboxes
                    new EditableComboBoxAutoCompletion(comboBox);
                } else {
                    new ComboBoxAutoCompletion(comboBox);
                }
            }
        }
    }

    /**
     * @see org.springframework.richclient.form.builder.FormComponentInterceptorFactory#getInterceptor(org.springframework.binding.form.FormModel)
     */
    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        return new ComboBoxAutoCompletionInterceptor(formModel);
    }
}