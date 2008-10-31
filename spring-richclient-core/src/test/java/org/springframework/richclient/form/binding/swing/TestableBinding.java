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

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;

/**
 * @author Oliver Hutchison
 */
public class TestableBinding implements Binding {

    private Class controlType;

    private FormModel formModel;

    private String property;

    private JComponent control;

    private Map context;

    public TestableBinding(Class controlType, JComponent control, FormModel formModel, String property, Map context) {
        this.controlType = controlType;
        this.control = control;
        this.formModel = formModel;
        this.property = property;
        this.context = context;
    }
    
    public Class getControlType() {
        return controlType;
    }

    public FormModel getFormModel() {
        return formModel;
    }

    public String getProperty() {
        return property;
    }

    public JComponent getControl() {
        return control;
    }

    public Map getContext() {
        return context;
    }

}
