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
package org.springframework.richclient.form.builder.support;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.MessageBuffer;
import org.springframework.richclient.dialog.MessageListener;
import org.springframework.richclient.dialog.MessageReceiver;
import org.springframework.richclient.util.OverlayHelper;
import org.springframework.rules.reporting.Severity;

/**
 * @author oliverh
 */
public class OverlayValidationInterceptor extends
        ValidationInterceptor {

    private int textCompHeight;

    public OverlayValidationInterceptor() {
        textCompHeight = new JTextField().getPreferredSize().height;
    }

    public JComponent processComponent(String propertyName, JComponent component) {
        ErrorReportingOverlay overlay = new ErrorReportingOverlay();

        int yOffset = component.getPreferredSize().height;

        OverlayHelper.attachOverlay(overlay, component, OverlayHelper.NORTH_WEST,
                0, Math.min(yOffset, textCompHeight));

        registerErrorGuarded(propertyName, overlay);
        registerErrorMessageReceiver(propertyName, overlay);
        return component;
    }

    private class ErrorReportingOverlay extends JLabel implements
            MessageReceiver, Guarded {

        private MessageBuffer messageBuffer = new MessageBuffer(this);

        public ErrorReportingOverlay() {
        }

        public boolean isEnabled() {
            return true;
        }

        public void setEnabled(boolean enabled) {
            setVisible(!enabled);
        }

        public void setMessage(String newMessage) {
            setMessage(newMessage, Severity.INFO);
        }

        public void setMessage(String newMessage, Severity severity) {
            messageBuffer.setMessage(newMessage, severity);
            setToolTipText(messageBuffer.getMessage());
            setIcon(Application.services().getIcon(
                    "severity." + severity.getShortCode() + ".overlay"));
        }

        public void setErrorMessage(String errorMessage) {
            setMessage(errorMessage, Severity.ERROR);
        }

        public void addMessageListener(MessageListener messageListener) {
            messageBuffer.addMessageListener(messageListener);
        }

        public void removeMessageListener(MessageListener messageListener) {
            messageBuffer.removeMessageListener(messageListener);
        }
    }
}