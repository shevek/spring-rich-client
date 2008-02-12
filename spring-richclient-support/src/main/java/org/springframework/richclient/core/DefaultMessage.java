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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.util.ObjectUtils;

/**
 * The default implementation of the {@link Message} interface. This class is
 * capable of rendering itself on {@link JTextComponent}s and {@link JLabel}s.
 * In the case of labels, it is also able to lookup an icon to be displayed on
 * the label.
 *
 * @see #getIcon()
 *
 */
public class DefaultMessage implements Message, Serializable {

    private static final long serialVersionUID = -6524078363891514995L;

	private final long timestamp;

    private final String message;

    private final Severity severity;

    /**
     * A convenience instance representing an empty message. i.e. The message text
     * is empty and there is no associated severity.
     */
    public static final DefaultMessage EMPTY_MESSAGE = new DefaultMessage("", null);

    /**
     * Creates a new {@code DefaultMessage} with the given text and a default
     * severity of {@link Severity#INFO}.
     *
     * @param text The message text.
     */
    public DefaultMessage(String text) {
        this(text, Severity.INFO);
    }

    /**
     * Creates a new {@code DefaultMessage} with the given text and severity.
     *
     * @param message The message text.
     * @param severity The severity of the message. May be null.
     */
    public DefaultMessage(String message, Severity severity) {
        if (message == null) {
            message = "";
        }
        this.timestamp = System.currentTimeMillis();
        this.message = message;
        this.severity = severity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    /**
     * Renders this message on the given GUI component. This implementation only
     * supports components of type {@link JTextComponent} or {@link JLabel}.
     *
     * @throws IllegalArgumentException if {@code component} is not a {@link JTextComponent}
     * or a {@link JLabel}.
     */
    public void renderMessage(JComponent component) {
        if (component instanceof JTextComponent) {
            ((JTextComponent)component).setText(getMessage());
        }
        else if (component instanceof JLabel) {
            JLabel label = (JLabel)component;
            label.setText(LabelUtils.htmlBlock(getMessage()));
            label.setIcon(getIcon());
        }
        else {
            throw new IllegalArgumentException("Unsupported component type " + component);
        }
    }

    /**
     * Returns the icon associated with this instance's severity. The icon is
     * expected to be retrieved using a key {@code severity.&lt;SEVERITY_LABEL&gt;}.
     *
     * @return The icon associated with this instance's severity, or null if the
     * instance has no specified severity, or the icon could not be found.
     *
     * @see Severity#getLabel()
     * @see IconSource#getIcon(String)
     */
    public Icon getIcon() {
        if (severity == null) {
            return null;
        }
        try {
            IconSource iconSource = (IconSource)ApplicationServicesLocator.services().getService(IconSource.class);
            return iconSource.getIcon("severity." + severity.getLabel());
        }
        catch (NoSuchImageResourceException e) {
            return null;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof DefaultMessage)) {
            return false;
        }
        DefaultMessage other = (DefaultMessage)o;
        return this.message.equals(other.message) && ObjectUtils.nullSafeEquals(this.severity, other.severity);
    }

    public int hashCode() {
        return message.hashCode() + (severity != null ? severity.hashCode() : 0);
    }

    public String toString() {
        return new ToStringCreator(this).append("message", message).append("severity", severity).toString();
    }
}