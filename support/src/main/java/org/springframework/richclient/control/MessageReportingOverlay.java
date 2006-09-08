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
package org.springframework.richclient.control;

import javax.swing.JLabel;

import org.springframework.binding.validation.Severity;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.image.IconSource;

/**
 * Component which can be used as an overlay for an other component to the content of a message. The severity of the
 * message will be used to retrive an icon by using the key <code>severity.{severity.label}.overlay</code> where
 * {severity.label} the content of {@link Severity#getLabel()} is.
 * 
 * @author Oliver Hutchison
 * @author Mathias Broekelmann
 */
public class MessageReportingOverlay extends JLabel implements Messagable, Guarded {
    private IconSource iconSource;

    public IconSource getIconSource() {
        if (iconSource == null) {
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        }
        return iconSource;
    }

    public void setIconSource(IconSource iconSource) {
        this.iconSource = iconSource;
    }

    public boolean isEnabled() {
        return isVisible();
    }

    public void setEnabled(boolean enabled) {
        setVisible(enabled);
    }

    public void setMessage(Message message) {
        if (message == null) {
            message = Message.EMPTY_MESSAGE;
        }
        setToolTipText(message.getText());
        Severity severity = message.getSeverity();

        if (severity != null) {
            setIcon(getIconSource().getIcon("severity." + severity.getLabel() + ".overlay"));
        } else {
            setIcon(null);
        }
    }
}