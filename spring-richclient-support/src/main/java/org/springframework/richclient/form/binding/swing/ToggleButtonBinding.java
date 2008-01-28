/*
 * Copyright 2002-2006 the original author or authors.
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;

/**
 * @author Mathias Broekelmann
 * 
 */
public class ToggleButtonBinding extends CustomBinding {

    private final JToggleButton toggleButton;

    private ItemListener selectionListener = new SelectionListener();

    private boolean configureFace = true;

    public ToggleButtonBinding(JToggleButton toggleButton, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, Boolean.class);
        this.toggleButton = toggleButton;
    }

    protected JComponent doBindControl() {
        if(configureFace) {
            getFieldFace().configure(toggleButton);
        }
        toggleButton.getModel().addItemListener(selectionListener);
        toggleButton.setSelected(Boolean.TRUE.equals(getValue()));
        return toggleButton;
    }
    
    void setConfigureFace(boolean configureFace) {
        this.configureFace = configureFace;        
    }
    
    protected void readOnlyChanged() {
        toggleButton.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void enabledChanged() {
        toggleButton.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void valueModelChanged(Object newValue) {
        toggleButton.setSelected(Boolean.TRUE.equals(newValue));
    }

    protected class SelectionListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            controlValueChanged(Boolean.valueOf(toggleButton.isSelected()));
        }

    }
}
