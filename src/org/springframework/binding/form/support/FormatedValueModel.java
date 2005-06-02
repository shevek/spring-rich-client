/*
 * Copyright 23/03/2005 (C) Our Community Pty. Ltd. All Rights Reserved.
 *
 * $Id$
 */
package org.springframework.binding.form.support;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelWrapper;

/**
 * @author Oliver Hutchison
 */
public class FormatedValueModel extends AbstractValueModelWrapper {
    
    private final Formatter formatter;

    public FormatedValueModel(ValueModel valueModel, Formatter formatter) {
        super(valueModel);
        this.formatter = formatter;
    }
    
    public Object getValue() {
        return formatter.formatValue(super.getValue());
    }

    public void setValue(Object value) {
        try {
            super.setValue(formatter.parseValue((String) value));
        } catch(InvalidFormatException e) {
            throw new RuntimeException(e); // FIXME: need to throw better exception
        }
    }
    
    protected Formatter getFormatter() {
        return formatter;
    }
}
