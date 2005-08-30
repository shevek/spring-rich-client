/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.value.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.binding.value.DerivedValueModel;
import org.springframework.binding.value.ValueModel;

/**
 * Abstract base class for value models that derive their value from one or
 * more "source" value model. Provides a convinent hook so that subclasses are 
 * notifed when any of the "source" value models change.
 * 
 * @author  Oliver Hutchison
 */
public abstract class AbstractDerivedValueModel extends AbstractValueModel implements DerivedValueModel {

    private final ValueModel[] sourceValueModels;

    private final PropertyChangeListener sourceChangeHandler;

    protected AbstractDerivedValueModel(ValueModel[] sourceValueModels) {
        this.sourceValueModels = sourceValueModels;
        this.sourceChangeHandler = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                sourceValuesChanged();
            }
        };
        for (int i = 0; i < sourceValueModels.length; i++) {
            sourceValueModels[i].addValueChangeListener(sourceChangeHandler);
        }
    }

    public ValueModel[] getSourceValueModels() {
        return sourceValueModels;
    }

    protected Object[] getSourceValues() {
        Object[] values = new Object[sourceValueModels.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = sourceValueModels[i].getValue();
        }
        return values;
    }

    protected abstract void sourceValuesChanged();

    public boolean isReadOnly() {
        return true;
    }

    public void setValue(Object newValue) {
        throw new UnsupportedOperationException("This value model is read only");
    }
}