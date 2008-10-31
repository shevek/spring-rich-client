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

import org.springframework.binding.form.FormModel;

/**
 * @author Oliver Hutchison
 */
public class CheckBoxBinder extends ToggleButtonBinder {

    public CheckBoxBinder() {
        super();
    }

    protected CheckBoxBinder(Class requiredSourceClass, String[] supportedContextKeys) {
        super(requiredSourceClass, supportedContextKeys);
    }

    protected CheckBoxBinder(Class requiredSourceClass) {
        super(requiredSourceClass);
    }

    protected CheckBoxBinder(String[] supportedContextKeys) {
        super(supportedContextKeys);
    }

    protected JComponent createControl(Map context) {
        return getComponentFactory().createCheckBox("");
    }
    
    protected void applyContext(ToggleButtonBinding toggleButtonBinding, FormModel formModel, String formPropertyPath, Map context) {
        super.applyContext(toggleButtonBinding, formModel, formPropertyPath, context);
        toggleButtonBinding.setConfigureFace(false);
    }
}