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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.EmptyIcon;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public class DefaultMessageAreaPane extends AbstractControlFactory implements MessagePane, PropertyChangeListener {
    private static final Log logger = LogFactory.getLog(DefaultMessageAreaPane.class);

    private JLabel messageLabel;

    private Icon defaultIcon = EmptyIcon.SMALL;

    private DefaultMessageAreaModel messageAreaModel;

    public DefaultMessageAreaPane() {
        init(this);
    }

    public DefaultMessageAreaPane(Messagable delegate) {
        init(delegate);
    }

    private void init(Messagable delegate) {
        this.messageAreaModel = new DefaultMessageAreaModel(delegate);
        this.messageAreaModel.addPropertyChangeListener(this);
    }

    public void setDefaultIcon(Icon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    protected JComponent createControl() {
        if (messageLabel == null) {
            this.messageLabel = new JLabel();
            this.messageAreaModel.renderMessage(messageLabel);
        }
        messageLabel.setOpaque(false);
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        messageLabel.setVerticalTextPosition(SwingConstants.TOP);
        messageLabel.setIcon(getDefaultIcon());
        return messageLabel;
    }

    private Icon getDefaultIcon() {
        return defaultIcon;
    }

    public void setMessage(Message message) {
        messageAreaModel.setMessage(message);
        if (messageLabel != null) {
            messageAreaModel.renderMessage(messageLabel);
        }
    }

    public void clearMessage() {
        messageAreaModel.setMessage(null);
    }

    public boolean isMessageShowing() {
        if (messageLabel == null) {
            return false;
        }
        return StringUtils.hasText(messageLabel.getText()) && messageLabel.isVisible();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        messageAreaModel.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        messageAreaModel.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        messageAreaModel.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        messageAreaModel.removePropertyChangeListener(propertyName, listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (messageLabel == null) {
            messageLabel = new JLabel();
        }
        messageAreaModel.getMessage().renderMessage(messageLabel);
    }
}