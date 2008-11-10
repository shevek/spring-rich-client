/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.components;

import javax.swing.JLabel;

import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.util.OverlayHelper;

/**
 * Component which can be used as an overlay for an other component to the content of a message. The severity of the
 * message will be used to retrive an icon by using the key <code>severity.{severity.label}.overlay</code> where
 * {severity.label} the content of {@link Severity#getLabel()} is.
 * <p>
 * Use {@link OverlayHelper#attachOverlay(javax.swing.JComponent, javax.swing.JComponent, int, int, int)} to put this
 * component as an overlay of an other component
 * 
 * @author Oliver Hutchison
 * @author Mathias Broekelmann
 */
public class MessageReportingOverlay extends JLabel implements Messagable, Guarded {
    private IconSource iconSource;

    /**
     * Return the used icon source
     * 
     * @return the icon source, must not null
     */
    public IconSource getIconSource() {
        if (iconSource == null) {
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        }
        return iconSource;
    }

    /**
     * Define the iconsource for getting the icon of the overlay
     * 
     * @param iconSource
     *            the icon source, if null the default icon source from {@link ApplicationServices} will be used
     */
    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }

    /**
     * Returns whether this overlay is enabled (=visible) or not
     */
    public boolean isEnabled() {
        return isVisible();
    }

    /**
     * Defines whether this overlay is enabled (=visible) or not
     */
    public void setEnabled(boolean enabled) {
        setVisible(enabled);
    }

    /**
     * set the message wich will be used as the content of the overlay. The message text will be used as tooltip and the
     * severity is used to determine which icon should be shown
     * 
     * @param message
     *            the message, if null tooltip will be empty and icon will be null
     */
    public void setMessage(Message message) {
        if (message == null) {
            message = DefaultMessage.EMPTY_MESSAGE;
        }
        setToolTipText(message.getMessage());
        Severity severity = message.getSeverity();

        if (severity != null) {
            setIcon(getIconSource().getIcon("severity." + severity.getLabel() + ".overlay"));
        } else {
            setIcon(null);
        }
    }
}