/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.springframework.richclient.util;

import java.beans.PropertyChangeEvent;

/**
 * Useful wrapper for PropertyChangeEvents for debugging purposes.
 * 
 * @author Keith Donald
 */
public class PropertyChangeEventWrapper {
    private PropertyChangeEvent e;
    
    public PropertyChangeEventWrapper(PropertyChangeEvent e) {
        this.e = e;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf
            .append("PropertyChangeEvent [propertyName='")
            .append(e.getPropertyName())
            .append("', oldValue='")
            .append(e.getOldValue())
            .append("', newValue='")
            .append(e.getNewValue())
            .append("']");
        return buf.toString();

    }
}
