/*
 * Copyright (c) 2002-2004 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
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
 * @author Karsten Lentzsch
 * @author Keith Donald
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
    public ToggleButtonAdapter(ValueModel valueModel, Object selectedValue, Object deselectedValue) {
        Assert.notNull(valueModel, "The subject value model is required.");
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
        if (isSelected() == selected) {
            return;
        }
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