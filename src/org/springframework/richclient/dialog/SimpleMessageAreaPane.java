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

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.EmptyIcon;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.rules.reporting.Severity;
import org.springframework.util.StringUtils;

import com.jgoodies.forms.layout.Sizes;

/**
 * @author Keith Donald
 */
public class SimpleMessageAreaPane extends AbstractControlFactory implements
        MessageAreaPane, MessageListener {

    private static final Log logger = LogFactory
            .getLog(SimpleMessageAreaPane.class);
    
    private static final int ONE_LINE_IN_DLU = 10;

    public static final int DEFAULT_LINES_TO_DISPLAY = 2;

    private int linesToDisplay;

    private JLabel messageLabel;

    private Icon defaultIcon = EmptyIcon.SMALL;

    private MessageBuffer messageBuffer;

    public SimpleMessageAreaPane() {
        this(DEFAULT_LINES_TO_DISPLAY);
    }

    public SimpleMessageAreaPane(int linesToDisplay) {
        this.linesToDisplay = linesToDisplay;
        this.messageBuffer = new MessageBuffer(this);
        this.messageBuffer.addMessageListener(this);
    }

    public SimpleMessageAreaPane(MessageReceiver delegateFor) {
        this(DEFAULT_LINES_TO_DISPLAY, delegateFor);
    }

    public SimpleMessageAreaPane(int linesToDisplay, MessageReceiver delegateFor) {
        this.linesToDisplay = linesToDisplay;
        this.messageBuffer = new MessageBuffer(delegateFor);
        this.messageBuffer.addMessageListener(this);
    }

    public void setDefaultIcon(Icon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    private Icon getDefaultIcon() {
        return defaultIcon;
    }

    protected JComponent createControl() {
        this.messageLabel = new JLabel();
        int prefHeight = Sizes.dialogUnitYAsPixel(linesToDisplay * ONE_LINE_IN_DLU, messageLabel);
        int prefWidth = messageLabel.getPreferredSize().width;
        messageLabel.setPreferredSize(new Dimension(prefWidth, prefHeight));        
        messageLabel.setOpaque(false);        
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        messageLabel.setVerticalTextPosition(SwingConstants.TOP);
        messageLabel.setIcon(getDefaultIcon());
        messageLabel.setText(" ");
        return messageLabel;
    }

    public boolean messageShowing() {
        return StringUtils.hasText(messageLabel.getText());
    }

    public void setMessage(String newMessage) {
        messageBuffer.setMessage(newMessage);
    }

    public void setInfoMessage(String infoMessage) {
        messageBuffer.setMessage(infoMessage, Severity.INFO);
    }

    public void setWarningMessage(String warningMessage) {
        messageBuffer.setMessage(warningMessage, Severity.WARNING);
    }

    public void setErrorMessage(String errorMessage) {
        messageBuffer.setErrorMessage(errorMessage);
    }

    public void setMessage(String message, Severity severity) {
        messageBuffer.setMessage(message, severity);
    }

    private Icon getIcon(Severity severity) {
        if (severity == null) { return getDefaultIcon(); }
        try {
            return getIconSource().getIcon("severity." + severity.getCode());
        }
        catch (NoSuchImageResourceException e) {
            logger.info("No severity icon found for severity " + severity
                    + "; returning default icon.");
            return getDefaultIcon();
        }
    }

    public void clearMessage() {
        setMessage("");
    }

    public void addMessageListener(MessageListener messageListener) {
        messageBuffer.addMessageListener(messageListener);
    }

    public void removeMessageListener(MessageListener messageListener) {
        messageBuffer.removeMessageListener(messageListener);
    }

    public void messageUpdated(MessageReceiver source) {
        String message = messageBuffer.getMessage();
        Severity severity = messageBuffer.getSeverity();
        if (StringUtils.hasText(message)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[Setting message '" + message + ", severity="
                        + severity + "]");
            }
            messageLabel.setText(LabelUtils.htmlBlock(message));
            messageLabel.setIcon(getIcon(severity));
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("[Clearing message area]");
            }
            messageLabel.setText(" ");
            messageLabel.setIcon(null);
        }
    }
}