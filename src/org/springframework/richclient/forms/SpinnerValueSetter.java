/*
 * $Header: /usr/local/cvs/module/src/java/File.java,v 1.7 2004/01/16 22:23:11
 * keith Exp $ $Revision$ $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.forms;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.rules.values.ValueModel;
import org.springframework.rules.values.ValueModelWrapper;

public class SpinnerValueSetter extends AbstractValueSetter implements
        ChangeListener {
    private JSpinner spinner;

    public SpinnerValueSetter(JSpinner spinner, ValueModel valueModel) {
        super(valueModel);
        this.spinner = spinner;
        this.spinner.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e) {
        componentValueChanged(spinner.getValue());
    }

    protected Object getWrappedValue() {
        if (getValueModel() instanceof ValueModelWrapper) {
            return ((ValueModelWrapper)getValueModel()).getWrapped();
        }
        else {
            return getValueModel().get();
        }
    }

    public void valueChanged() {
        if (!isUpdating()) {
            setComponentValue(getWrappedValue());
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