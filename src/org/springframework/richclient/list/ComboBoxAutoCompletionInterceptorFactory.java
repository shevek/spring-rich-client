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

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor;
import org.springframework.util.Assert;

/**
 * @author Peter De Bruycker
 */
public class ComboBoxAutoCompletionInterceptorFactory implements FormComponentInterceptorFactory {

    private final Log logger = LogFactory.getLog(getClass());

    public class ComboBoxAutoCompletionInterceptor extends AbstractFormComponentInterceptor {

        private class BeanPropertyValueComboBoxEditor implements ComboBoxEditor {

            private BeanWrapper beanWrapper = new BeanWrapperImpl();

            private Object current;

            private ComboBoxEditor inner;

            private String renderedProperty;

            /**
             * Constructs a new <code>BeanPropertyValueComboBoxEditor</code>
             * instance. The <code>toString</code> method is used to render
             * the items.
             * 
             * @param editor
             *            the <code>ComboBoxEditor</code> to use internally
             */
            public BeanPropertyValueComboBoxEditor(ComboBoxEditor editor) {
                this(editor, null);
            }

            /**
             * Constructs a new <code>BeanPropertyValueComboBoxEditor</code>
             * instance.
             * 
             * @param editor
             *            the <code>ComboBoxEditor</code> to use internally
             * @param renderedProperty
             *            the property used to render the items
             */
            public BeanPropertyValueComboBoxEditor(ComboBoxEditor editor, String renderedProperty) {
                Assert.notNull(editor, "Editor cannot be null");
                this.inner = editor;
                this.renderedProperty = renderedProperty;
            }

            /**
             * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
             */
            public void addActionListener(ActionListener l) {
                inner.addActionListener(l);
            }

            /**
             * @see javax.swing.ComboBoxEditor#getEditorComponent()
             */
            public Component getEditorComponent() {
                return inner.getEditorComponent();
            }

            /**
             * @see javax.swing.ComboBoxEditor#getItem()
             */
            public Object getItem() {
                return current;
            }

            /**
             * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
             */
            public void removeActionListener(ActionListener l) {
                inner.removeActionListener(l);
            }

            /**
             * @see javax.swing.ComboBoxEditor#selectAll()
             */
            public void selectAll() {
                inner.selectAll();
            }

            /**
             * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
             */
            public void setItem(Object item) {
                current = item;
                if (item == null) {
                    inner.setItem("");
                } else {
                    beanWrapper.setWrappedInstance(item);
                    if (renderedProperty != null) {
                        inner.setItem(String.valueOf(beanWrapper.getPropertyValue(renderedProperty)));
                    } else {
                        inner.setItem(String.valueOf(item));
                    }
                }
            }
        }

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
                    // if combobox is showing CodedEnum's, install customer editor
                    if (comboBox.getRenderer() instanceof LabeledEnumListRenderer) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Registering CodedEnumComboBoxEditor on ComboBox for property[" + propertyName
                                    + "]");
                        }
                        comboBox.setEditor(new LabeledEnumComboBoxEditor(messages, ((JComboBox) inner).getEditor()));
                    } else {
                        if (comboBox.getRenderer() instanceof BeanPropertyValueListRenderer) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Registering BeanPropertyValueComboBoxEditor on ComboBox for property["
                                        + propertyName + "]");
                            }
                            BeanPropertyValueListRenderer renderer = (BeanPropertyValueListRenderer) comboBox.getRenderer();
                            comboBox.setEditor(new BeanPropertyValueComboBoxEditor(comboBox.getEditor(), renderer
                                    .getPropertyName()));
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Unable to register ComboBoxEditor on ComboBox for property[" + propertyName
                                        + "], set one yourself");
                            }
                        }
                    }
                    new ComboBoxAutoCompletion(comboBox);
                }
            }
        }
    }

    private MessageSource messages;

    /**
     * @see org.springframework.richclient.form.builder.FormComponentInterceptorFactory#getInterceptor(org.springframework.binding.form.FormModel)
     */
    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        return new ComboBoxAutoCompletionInterceptor(formModel);
    }

    public void setMessageSource(MessageSource messageSource) {
        messages = messageSource;
    }

}