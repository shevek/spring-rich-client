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
package org.springframework.richclient.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.ObjectUtils;

/**
 * A concrete implementation of the <code>Messagable</code> interface. Primarily
 * intended to be used as a delegate for the messagable functionality of
 * more complex classes.
 * 
 * @author Oliver Hutchison
 * @see DefaultMessagePane
 */
public class DefaultMessageAreaModel implements Messagable {

    private Messagable delegate;

    private Message message = DefaultMessage.EMPTY_MESSAGE;

    private EventListenerListHelper listenerList = new EventListenerListHelper(PropertyChangeListener.class);

    public DefaultMessageAreaModel() {
        this.delegate = this;
    }

    public DefaultMessageAreaModel(Messagable delegate) {
        this.delegate = delegate;
    }

    /**
     * @return Returns the delegateFor.
     */
    protected Messagable getDelegateFor() {
        return delegate;
    }

    public Message getMessage() {
        return message;
    }

    public boolean hasInfoMessage() {
        return message.getSeverity() == Severity.INFO;
    }

    public boolean hasErrorMessage() {
        return message.getSeverity() == Severity.ERROR;
    }

    public boolean hasWarningMessage() {
        return message.getSeverity() == Severity.WARNING;
    }

    public void setMessage(Message message) {
        if (message == null) {
            message = DefaultMessage.EMPTY_MESSAGE;
        }
        if (ObjectUtils.nullSafeEquals(this.message, message)) {
            return;
        }
        Message oldMsg = this.message;
        this.message = message;
        fireMessageUpdated(oldMsg, this.message);
    }

    public void renderMessage(JComponent component) {
        message.renderMessage(component);
    }
    
    protected void fireMessageUpdated(Message oldMsg, Message newMsg) {
        listenerList.fire("propertyChange", new PropertyChangeEvent(delegate, MESSAGE_PROPERTY, oldMsg, newMsg));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (MESSAGE_PROPERTY.equals(propertyName)) {
            listenerList.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (MESSAGE_PROPERTY.equals(propertyName)) {
            listenerList.remove(listener);
        }
    }
}