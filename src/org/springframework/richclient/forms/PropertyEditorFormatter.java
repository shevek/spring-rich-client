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
package org.springframework.richclient.forms;

import java.beans.PropertyEditor;
import java.text.ParseException;

import javax.swing.text.DefaultFormatter;

/**
 * @author Keith Donald
 */
public class PropertyEditorFormatter extends DefaultFormatter {
    private PropertyEditor editor;

    public PropertyEditorFormatter(PropertyEditor editor) {
        this.editor = editor;
    }

    /**
     * @see javax.swing.JFormattedTextField.AbstractFormatter#stringToValue(java.lang.String)
     */
    public Object stringToValue(String string) throws ParseException {
        try {
            editor.setAsText(string);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return editor.getValue();
    }

    /**
     * @see javax.swing.JFormattedTextField.AbstractFormatter#valueToString(java.lang.Object)
     */
    public String valueToString(Object value) throws ParseException {
        editor.setValue(value);
        return editor.getAsText();
    }
}