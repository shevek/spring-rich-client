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
import org.springframework.util.ObjectUtils;

/**
 * Converts ValueModels to the <code>ToggleButtonModel</code> interface.
 * Useful to bind {@link javax.swing.JRadioButton}and
 * {@link javax.swing.JRadioButtonMenuItem} to a ValueModel.
 * <p>
 * 
 * This adapter holds a <em>choice</em> object that is used to determine the
 * selection state if the underlying ValueModel changes its value. This model is
 * selected if the subject's value equals the choice object. And if the
 * selection is set, the choice object is set to the subject.
 */
public final class RadioButtonAdapter extends JToggleButton.ToggleButtonModel {

    /**
     * Refers to the underlying ValueModel that stores the state.
     */
    private final ValueModel valueModel;

    /**
     * Holds the object that is compared with the value model's value to
     * determine whether this adapter is selected or not.
     */
    private final Object choice;

    private ValueChangeListener valueChangeHandler;

    public RadioButtonAdapter(ValueModel valueModel, Object choice) {
        Assert.notNull("The subject must not be null.");
        this.valueModel = valueModel;
        this.choice = choice;
        this.valueChangeHandler = new ValueChangeHandler();
        valueModel.addValueChangeListener(valueChangeHandler);
    }

    // Handles changes in the subject's value.
    private class ValueChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            setSelected(isSelected());
        }
    }

    /**
     * Checks and answers whether the button is selected.
     * 
     * @return true if the button is selected, false if deselected
     */
    public boolean isSelected() {
        return ObjectUtils.nullSafeEquals(choice, valueModel.getValue());
    }

    /**
     * Sets the selected state of the button.
     * 
     * @param selected
     *            true selects the toggle button, false deselects it
     */
    public void setSelected(boolean selected) {
        boolean oldValue = isSelected();
        if (oldValue == selected) { return; }
        if (selected) {
            updateValueModelSilently(selected);
        }
        super.setSelected(selected);
    }

    private void updateValueModelSilently(boolean selected) {
        valueModel.removeValueChangeListener(valueChangeHandler);
        valueModel.setValue(choice);
        valueModel.addValueChangeListener(valueChangeHandler);
    }

}