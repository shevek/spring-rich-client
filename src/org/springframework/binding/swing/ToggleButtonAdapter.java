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
package org.springframework.binding.swing;

import javax.swing.JToggleButton;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.util.Assert;

/**
 * Converts ValueModels to the <code>ToggleButtonModel</code> interface.
 * Useful to bind <code>JToggleButton</code>,<code>JCheckBox</code> and
 * <code>JCheckBoxMenuItem</code> to a ValueModel.
 * 
 * @author Keith Donald
 * @author Karsten Lentzsch
 * 
 * @see javax.swing.ButtonModel
 * @see javax.swing.JCheckBox
 * @see javax.swing.JCheckBoxMenuItem
 */
public final class ToggleButtonAdapter extends JToggleButton.ToggleButtonModel {

    /**
     * Refers to the underlying ValueModel that is used to read and write
     * values.
     */
    private final ValueModel valueModel;

    /**
     * The value that represents the selected state.
     */
    private final Object selectedValue;

    /**
     * The value that represents the deselected state.
     */
    private final Object deselectedValue;

    private final ValueChangeListener valueChangeHandler;

    /**
     * Constructs a <code>ToggleButtonAdapter</code> on the given subject.
     * 
     * @param valueModel
     *            the subject that holds the value
     * @throws NullPointerException
     *             if the subject is <code>null</code>
     */
    public ToggleButtonAdapter(ValueModel valueModel) {
        this(valueModel, Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Constructs a <code>ToggleButtonAdapter</code> on the given subject.
     * 
     * @param valueModel
     *            the subject that holds the value
     * @param selectedValue
     *            the value that will be set if this is selected
     * @param deselectedValue
     *            the value that will be set if this is deselected
     * 
     * @throws NullPointerException
     *             if the subject is <code>null</code>
     */
    public ToggleButtonAdapter(ValueModel valueModel, Object selectedValue,
            Object deselectedValue) {
        Assert.notNull(valueModel);
        this.valueModel = valueModel;
        this.selectedValue = selectedValue;
        this.deselectedValue = deselectedValue;
        this.valueChangeHandler = new ValueChangeHandler();
        valueModel.addValueChangeListener(valueChangeHandler);
    }

    // Handles changes in the subject's value.
    private class ValueChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            setSelected(isSelected(), false);
        }
    }

    public boolean isSelected() {
        return selectedValue.equals(valueModel.getValue());
    }

    public void setSelected(boolean selected) {
        setSelected(selected, true);
    }

    private void setSelected(boolean selected, boolean updateValueModel) {
        if (isSelected() == selected) { return; }
        if (updateValueModel) {
            updateValueModelSilently(selected);
        }
        super.setSelected(selected);
    }

    private void updateValueModelSilently(boolean selected) {
        Object newValue = selected ? selectedValue : deselectedValue;
        valueModel.removeValueChangeListener(valueChangeHandler);
        valueModel.setValue(newValue);
        valueModel.addValueChangeListener(valueChangeHandler);
    }
}