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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueModelWrapper;

/**
 * @author oliverh
 */
public abstract class AbstractValueSetter implements ValueChangeListener {
    protected static final Log logger = LogFactory.getLog(AbstractValueSetter.class);

    private ValueModel displayValueModel;

    public AbstractValueSetter(ValueModel displayValueModel) {
        this.displayValueModel = displayValueModel;
        if (this.displayValueModel != null) {
            this.displayValueModel.addValueChangeListener(this);
        }
    }

    protected ValueModel getValueModel() {
        return displayValueModel;
    }

    protected void componentValueChanged(Object newValue) {
        if (displayValueModel != null) {
            displayValueModel.removeValueChangeListener(this);
            displayValueModel.setValue(newValue);
            displayValueModel.addValueChangeListener(this);
        }
    }

    public void valueChanged() {
        setControlValue(displayValueModel.getValue());
    }

    protected abstract void setControlValue(Object value);

    protected Object getInnerMostValue() {
        if (getValueModel() instanceof ValueModelWrapper) {
            return ((ValueModelWrapper)getValueModel()).getInnerMostValue();
        }
        else {
            return getValueModel().getValue();
        }
    }

}