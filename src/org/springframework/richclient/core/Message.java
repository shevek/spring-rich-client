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
package org.springframework.richclient.core;

import java.io.Serializable;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.springframework.binding.validation.Severity;
import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class Message implements Serializable {
    private long timestamp = new Date().getTime();
    
    private String text;

    private Severity severity;

    public static Message EMPTY_MESSAGE = new Message("", null);

    public Message(String text) {
        this(text, Severity.INFO);
    }

    public Message(String text, Severity severity) {
        if (text == null) {
            text = "";
        }
        this.text = text;
        this.severity = severity;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public String getText() {
        return text;
    }

    public Severity getSeverity() {
        return severity;
    }

    public boolean isEmpty() {
        return !StringUtils.hasText(text);
    }

    public boolean isInfoMessage() {
        return !isEmpty() && severity == Severity.INFO;
    }

    public boolean isWarningMessage() {
        return !isEmpty() && severity == Severity.WARNING;
    }

    public boolean isErrorMessage() {
        return !isEmpty() && severity == Severity.ERROR;
    }

    public void renderMessage(JComponent component) {
        if (component instanceof JTextComponent) {
            ((JTextComponent)component).setText(text);
        }
        else if (component instanceof JLabel) {
            JLabel label = (JLabel)component;
            label.setText(LabelUtils.htmlBlock(text));
            label.setIcon(getIcon());
        }
        else {
            throw new IllegalArgumentException("Unsupported component type " + component);
        }
    }

    public Icon getIcon() {
        if (severity == null) {
            return null;
        }
        try {
            return Application.services().getIconSource().getIcon("severity." + severity.getLabel());
        }
        catch (NoSuchImageResourceException e) {
            return null;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Message)) {
            return false;
        }
        Message m = (Message)o;
        return text.equals(m.text) && ObjectUtils.nullSafeEquals(severity, m.severity);
    }

    public int hashCode() {
        return text.hashCode() + (severity != null ? severity.hashCode() : 0);
    }

    public String toString() {
        return new ToStringCreator(this).append("message", text).append("severity", severity).toString();
    }
}