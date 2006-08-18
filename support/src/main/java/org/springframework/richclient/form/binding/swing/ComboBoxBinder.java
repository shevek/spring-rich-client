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

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.springframework.binding.form.FormModel;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class ComboBoxBinder extends AbstractListBinder {
    public static final String RENDERER_KEY = "renderer";

    public static final String EDITOR_KEY = "editor";

    private ListCellRenderer renderer;

    private ComboBoxEditor editor;

    public ComboBoxBinder() {
        this(null, new String[] { SELECTABLE_ITEMS_KEY, COMPARATOR_KEY, RENDERER_KEY, EDITOR_KEY, FILTER_KEY });
    }

    public ComboBoxBinder(String[] supportedContextKeys) {
        this(null, supportedContextKeys);
    }

    public ComboBoxBinder(Class requiredSourceClass, String[] supportedContextKeys) {
        super(requiredSourceClass, supportedContextKeys);
    }

    protected AbstractListBinding createListBinding(JComponent control, FormModel formModel, String formPropertyPath) {
        Assert.isInstanceOf(JComboBox.class, control, formPropertyPath);
        return new ComboBoxBinding((JComboBox) control, formModel, formPropertyPath, getRequiredSourceClass());
    }

    protected void applyContext(AbstractListBinding binding, Map context) {
        super.applyContext(binding, context);
        ComboBoxBinding comboBoxBinding = (ComboBoxBinding) binding;
        if (context.containsKey(RENDERER_KEY)) {
            comboBoxBinding.setRenderer((ListCellRenderer) decorate(context.get(RENDERER_KEY), comboBoxBinding
                    .getRenderer()));
        } else if (renderer != null) {
            comboBoxBinding.setRenderer((ListCellRenderer) decorate(renderer, comboBoxBinding.getRenderer()));
        }
        if (context.containsKey(EDITOR_KEY)) {
            comboBoxBinding.setEditor((ComboBoxEditor) decorate(context.get(EDITOR_KEY), comboBoxBinding.getEditor()));
        } else if (editor != null) {
            comboBoxBinding.setEditor((ComboBoxEditor) decorate(editor, comboBoxBinding.getEditor()));
        }
    }

    protected JComponent createControl(Map context) {
        return getComponentFactory().createComboBox();
    }

    public ListCellRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    public ComboBoxEditor getEditor() {
        return editor;
    }

    public void setEditor(ComboBoxEditor editor) {
        this.editor = editor;
    }

}
