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
package org.springframework.richclient.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.SwingPropertyChangeSupport;

import org.springframework.rules.values.PropertyChangePublisher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class AbstractPropertyChangePublisher implements
        PropertyChangePublisher {
    private PropertyChangeSupport pcs;

    protected AbstractPropertyChangePublisher() {

    }

    protected AbstractPropertyChangePublisher(PropertyChangeSupport support) {
        this.pcs = support;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        getOrCreatePropertyChangeSupport().addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener l) {
        getOrCreatePropertyChangeSupport().addPropertyChangeListener(
                propertyName, l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        getPropertyChangeSupport().removePropertyChangeListener(l);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener l) {
        getPropertyChangeSupport()
                .removePropertyChangeListener(propertyName, l);
    }

    private PropertyChangeSupport getPropertyChangeSupport() {
        Assert
                .notNull(pcs,
                        "Property change support has not yet been initialized; add a listener first!");
        return pcs;
    }

    private PropertyChangeSupport getOrCreatePropertyChangeSupport() {
        if (pcs == null) {
            pcs = new SwingPropertyChangeSupport(this);
        }
        return pcs;
    }

    protected void firePropertyChange(String propertyName, boolean oldValue,
            boolean newValue) {
        if (pcs != null) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, int oldValue,
            int newValue) {
        if (pcs != null) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        if (pcs != null) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    
    protected boolean hasChanged(Object o1, Object o2) {
        return !ObjectUtils.nullSafeEquals(o1, o2);
    }

}