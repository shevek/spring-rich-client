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

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.dialog.DefaultMessageAreaModel;
import org.springframework.richclient.dialog.MessageAreaModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.util.OverlayHelper;
import org.springframework.rules.reporting.Severity;

/**
 * Adds an "overlay" to a component that is triggered by a validation event. The
 * overlaid image is retrieved by the image key
 * "severity.{severityShortCode}.overlay", where {severityShortCode} is the
 * number returned by {@link Severity#getShortCode()}. The image is placed at
 * the bottom-left corner of the component, and the image's tooltip is set to
 * the validation message.
 * 
 * @author oliverh
 * @see OverlayHelper#attachOverlay
 */
public class OverlayValidationInterceptorFactory implements FormComponentInterceptorFactory {

    private int textCompHeight;

    public OverlayValidationInterceptorFactory() {
        textCompHeight = new JTextField().getPreferredSize().height;
    }

    public FormComponentInterceptor getInterceptor(FormModel formModel) {
        return new OverlayValidationInterceptor(formModel);
    }

    public class OverlayValidationInterceptor extends ValidationInterceptor {

        public OverlayValidationInterceptor(FormModel formModel) {
            super(formModel);
        }

        public void processComponent(String propertyName, JComponent component) {
            ErrorReportingOverlay overlay = new ErrorReportingOverlay();

            int yOffset = component.getPreferredSize().height;

            OverlayHelper.attachOverlay(overlay, component, OverlayHelper.NORTH_WEST, 0, Math.min(yOffset,
                    textCompHeight));

            registerErrorGuarded(propertyName, overlay);
            registerErrorMessageReceiver(propertyName, overlay);
        }
    }

    private class ErrorReportingOverlay extends JLabel implements MessageAreaModel, Guarded {

        private DefaultMessageAreaModel messageBuffer = new DefaultMessageAreaModel(this);

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
            setIcon(Application.services().getIcon("severity." + severity.getShortCode() + ".overlay"));
        }

        public void setErrorMessage(String errorMessage) {
            setMessage(errorMessage, Severity.ERROR);
        }
    }
}