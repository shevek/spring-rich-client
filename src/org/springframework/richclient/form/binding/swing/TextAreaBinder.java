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
package org.springframework.richclient.form.binding.swing;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

/**
 * @author Oliver Hutchison
 */
public class TextAreaBinder extends AbstractBinder {

    public static final String ROWS_KEY = "rows";

    public static final String COLUMNS_KEY = "columns";

    public TextAreaBinder() {
        super(String.class, new String[] {ROWS_KEY, COLUMNS_KEY});
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JTextArea, "Control must be an instance of JTextArea.");
        JTextArea textArea = (JTextArea)control;
        Integer rows = (Integer)context.get(ROWS_KEY);
        if (rows != null) {
            textArea.setRows(rows.intValue());
        }
        Integer columns = (Integer)context.get(COLUMNS_KEY);
        if (columns != null) {
            textArea.setColumns(columns.intValue());
        }
        return new TextComponentBinding((JTextArea)control, formModel, formPropertyPath);
    }

    protected JComponent createControl(Map context) {
        return getComponentFactory().createTextArea();
    }
}
