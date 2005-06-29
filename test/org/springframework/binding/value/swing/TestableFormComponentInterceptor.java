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
package org.springframework.binding.value.swing;

import javax.swing.JComponent;

import org.springframework.richclient.form.builder.FormComponentInterceptor;

/**
 * @author Peter De Bruycker
 */
public class TestableFormComponentInterceptor implements FormComponentInterceptor {

    private JComponent component;

    private int componentCount;

    private String componentProperty;

    private JComponent label;

    private int labelCount;

    private String labelProperty;

    public JComponent getComponent() {
        return component;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public String getComponentProperty() {
        return componentProperty;
    }

    public JComponent getLabel() {
        return label;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public String getLabelProperty() {
        return labelProperty;
    }

    public void processComponent(String propertyName, JComponent component) {
        componentCount++;
        componentProperty = propertyName;
        this.component = component;
    }

    public void processLabel(String propertyName, JComponent label) {
        labelCount++;
        labelProperty = propertyName;
        this.label = label;
    }
}