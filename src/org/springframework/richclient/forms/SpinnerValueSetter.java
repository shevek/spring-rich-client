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

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueModelWrapper;

public class SpinnerValueSetter extends AbstractValueSetter implements
        ChangeListener {
    private JSpinner spinner;

    public SpinnerValueSetter(JSpinner spinner, ValueModel valueModel) {
        super(valueModel);
        this.spinner = spinner;
        valueChanged();
        this.spinner.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e) {
        componentValueChanged(spinner.getValue());
    }

    public void valueChanged() {
        if (!isUpdating()) {
            setComponentValue(getInnerMostValue());
        }
    }

    protected Object getInnerMostValue() {
        if (getValueModel() instanceof ValueModelWrapper) {
            return ((ValueModelWrapper)getValueModel()).getInnerMostValue();
        }
        else {
            return getValueModel().getValue();
        }
    }

    protected void setComponentValue(Object value) {
        if (value == null) {
            spinner.setValue(new Integer(0));
        }
        else {
            spinner.setValue(value);
        }
    }
}