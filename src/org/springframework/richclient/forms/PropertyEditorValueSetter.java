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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import org.springframework.binding.value.ValueModel;

public class PropertyEditorValueSetter extends AbstractValueSetter implements
        PropertyChangeListener {
    private PropertyEditor propertyEditor;

    public PropertyEditorValueSetter(PropertyEditor propertyEditor,
            ValueModel valueModel) {
        super(valueModel);
        this.propertyEditor = propertyEditor;
        this.propertyEditor.addPropertyChangeListener(this);
    }

    protected void setControlValue(Object value) {
        propertyEditor.setValue(value);
    }

    public void dispose() {
        this.propertyEditor.removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent event) {
        componentValueChanged(propertyEditor.getValue());
    }
}