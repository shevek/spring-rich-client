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
import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;

/**
 * @author oliverh
 */
public abstract class AbstractValueSetter implements ValueListener {
    protected final Log logger = LogFactory.getLog(getClass());

    private ValueModel valueModel;

    private boolean updating;

    public AbstractValueSetter(ValueModel valueModel) {
        this.valueModel = valueModel;
        if (this.valueModel != null) {
            this.valueModel.addValueListener(this);
        }
    }

    protected ValueModel getValueModel() {
        return valueModel;
    }
    
    protected boolean isUpdating() {
        return updating;
    }

    protected void componentValueChanged(Object newValue) {
        if (valueModel != null) {
            try {
                updating = true;
                valueModel.set(newValue);
            }
            finally {
                updating = false;
            }
        }
    }

    public void valueChanged() {
        if (!updating) {
            setComponentValue(valueModel.get());
        }
    }

    protected abstract void setComponentValue(Object value);

}