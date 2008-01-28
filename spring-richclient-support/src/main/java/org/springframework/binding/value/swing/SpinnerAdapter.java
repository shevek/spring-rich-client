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

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelAdapter;

/**
 * Adapts a value model to a JSpinner control.
 * 
 * @author Oliver Hutchison
 */
public class SpinnerAdapter extends AbstractValueModelAdapter {
    
    private final SpinnerChangeListener listener = new SpinnerChangeListener();
        
    private final JSpinner spinner;

    public SpinnerAdapter(JSpinner spinner, ValueModel valueModel) {
        super(valueModel);
        this.spinner = spinner;
        this.spinner.addChangeListener(listener);
        initalizeAdaptedValue();
    }

    protected void valueModelValueChanged(Object newValue) {
        if (newValue == null) {
            spinner.setValue(new Integer(0));
        }
        else {
            spinner.setValue(newValue);
        }
    }
    
    private class SpinnerChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            adaptedValueChanged(spinner.getValue());
        }
    }
}