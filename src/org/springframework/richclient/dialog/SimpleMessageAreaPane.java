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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.EmptyIcon;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.rules.reporting.Severity;
import org.springframework.util.StringUtils;

/**
 * @author Keith Donald
 */
public class SimpleMessageAreaPane extends AbstractControlFactory implements
        MessageAreaPane {

    private static final Log logger = LogFactory
            .getLog(SimpleMessageAreaPane.class);

    private JLabel messageLabel;

    private Icon defaultIcon = EmptyIcon.SMALL;

    private IconSource icons = Application.locator();

    public void setDefaultIcon(Icon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    private Icon getDefaultIcon() {
        return defaultIcon;
    }

    protected JComponent createControl() {
        this.messageLabel = new JLabel();
        messageLabel.setOpaque(false);
        messageLabel.setVerticalTextPosition(SwingConstants.TOP);
        messageLabel.setIcon(getDefaultIcon());
        messageLabel.setText(" ");
        return messageLabel;
    }

    public boolean messageShowing() {
        return StringUtils.hasText(messageLabel.getText());
    }

    public void setMessage(String newMessage) {
        setMessage(newMessage, Severity.INFO);
    }

    public void setInfoMessage(String infoMessage) {
        setMessage(infoMessage, Severity.INFO);
    }

    public void setWarningMessage(String warningMessage) {
        setMessage(warningMessage, Severity.WARNING);
    }

    public void setErrorMessage(String errorMessage) {
        setMessage(errorMessage, Severity.ERROR);
    }

    public void setMessage(String message, Severity severity) {
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

    private Icon getIcon(Severity severity) {
        if (severity == null) { return getDefaultIcon(); }
        try {
            return icons.getIcon("severity." + severity.getCode());
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

}