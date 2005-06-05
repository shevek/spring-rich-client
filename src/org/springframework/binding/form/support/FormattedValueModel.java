/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form.support;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelWrapper;

/**
 * @author Oliver Hutchison
 */
public class FormattedValueModel extends AbstractValueModelWrapper {
    
    private final Formatter formatter;

    public FormattedValueModel(ValueModel valueModel, Formatter formatter) {
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
