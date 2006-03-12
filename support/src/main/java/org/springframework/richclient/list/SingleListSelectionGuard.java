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
package org.springframework.richclient.list;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.Guarded;

public class SingleListSelectionGuard implements PropertyChangeListener {
    private ValueModel selectionIndexHolder;

    private Guarded guarded;

    public SingleListSelectionGuard(ValueModel selectionIndexHolder, Guarded guarded) {
        this.selectionIndexHolder = selectionIndexHolder;
        this.selectionIndexHolder.addValueChangeListener(this);
        this.guarded = guarded;
        propertyChange(null);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Integer value = (Integer)selectionIndexHolder.getValue();
        if (value == null || value.intValue() == -1) {
            guarded.setEnabled(false);
        }
        else {
            guarded.setEnabled(true);
        }
    }
}